<?php

namespace App\Controller;

use App\Dto\RegisterDto;
use App\Entity\SimpleUser;
use App\Entity\Driver;
use Doctrine\DBAL\Exception\UniqueConstraintViolationException;
use App\Service\KeycloakAdmin;
use Doctrine\ORM\EntityManagerInterface as EM;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\IsGranted;

class AuthController extends AbstractController
{
    public function __construct(private HttpClientInterface $http) {} // 👈 inject http client here

    #[Route('/auth/register', methods: ['POST'])]
    public function register(Request $req, ValidatorInterface $validator, KeycloakAdmin $kc, EM $em): JsonResponse
    {
        $payload = json_decode($req->getContent(), true) ?? [];
        $dto = new RegisterDto();
        $dto->username = $payload['username'] ?? ($payload['email'] ?? null);
        $dto->email = $payload['email'] ?? null;
        $dto->password = $payload['password'] ?? null;
        $dto->role = strtoupper($payload['role'] ?? 'USER');

        $errors = $validator->validate($dto);
        if (count($errors)) {
            return $this->json(['error' => (string)$errors], 400);
        }

        $existingSimple = $em->getRepository(SimpleUser::class)->findOneBy(['email' => $dto->email]);
        $existingDriver = $em->getRepository(Driver::class)->findOneBy(['email' => $dto->email]);
        if ($existingSimple || $existingDriver) {
            return $this->json([
                'error' => 'email_already_registered',
                'message' => 'An account already exists for this email address.'
            ], 409);
        }

        try {
            // 1️⃣ Create Keycloak user
            $kcId = $kc->createUser($dto->username, $dto->email, $dto->password, [$dto->role]);

            // 2️⃣ Save to PostgreSQL
            $u = new SimpleUser();
            $u->setEmail($dto->email)
              ->setRole($dto->role)
              ->setKeycloakId($kcId)
              ->setFirstName($payload['firstName'] ?? null)
              ->setLastName($payload['lastName'] ?? null);

            $em->persist($u);
            $em->flush();
        } catch (UniqueConstraintViolationException $e) {
            return $this->json([
                'error' => 'email_already_registered',
                'message' => 'An account already exists for this email address.'
            ], 409);
        } catch (\Throwable $e) {
            return $this->json([
                'error' => 'registration_failed',
                'message' => $e->getMessage(),
            ], 500);
        }

        return $this->json([
            'message' => 'registered',
            'keycloakId' => $kcId,
            'id' => $u->getId()
        ], 201);
    }




#[Route('/auth/login', methods: ['POST'])]
public function login(Request $req, EM $em): JsonResponse
{
    $data = json_decode($req->getContent(), true) ?? [];
    $username = $data['username'] ?? $data['email'] ?? '';
    $password = $data['password'] ?? '';

    if (!$username || !$password) {
        return $this->json(['error' => 'Missing username or password'], 400);
    }

    // 1) Ask Keycloak
    try {
        $resp = $this->http->request(
            'POST',
            $_ENV['KEYCLOAK_BASE'] . '/realms/' . $_ENV['KEYCLOAK_REALM'] . '/protocol/openid-connect/token',
            [
                'body' => [
                    'grant_type' => 'password',
                    'client_id' => $_ENV['KEYCLOAK_ADMIN_CLIENT_ID'],
                    'client_secret' => $_ENV['KEYCLOAK_ADMIN_CLIENT_SECRET'],
                    'username' => $username,
                    'password' => $password,
                ],
                'timeout' => 5
            ]
        )->toArray(false);
    } catch (\Throwable $e) {
        return $this->json(['error' => 'Keycloak login request failed', 'details' => $e->getMessage()], 500);
    }

    if (empty($resp['access_token'])) {
        return $this->json(['error' => 'Invalid credentials'], 401);
    }

    // 2) Decode token
    $jwt = $resp['access_token'];
    $parts = explode('.', $jwt);
    $payload = json_decode(base64_decode($parts[1] ?? ''), true) ?? [];

    // 3) Extract identities & roles
    $email = $payload['email'] ?? $payload['preferred_username'] ?? $username;

    $roleContext = $this->extractRoleContext($payload);
    $allowed = ['ADMIN', 'DRIVER', 'USER'];
    $role = $roleContext['preferred'][0] ?? 'USER';

    // 5) DB override if valid
    $user = $em->getRepository(\App\Entity\SimpleUser::class)->findOneBy(['email' => $email]);
    $driver = null;

    if ($user && $user->getRole()) {
        $dbRole = strtoupper($user->getRole());
        if (in_array($dbRole, $allowed, true)) {
            $priority = array_flip($allowed);
            if ($priority[$role] < $priority[$dbRole]) {
                // Token role outranks stored role – upgrade local record
                $user->setRole($role);
                $em->flush();
                $dbRole = $role;
            } else {
                $role = $dbRole;
            }
        } else {
            $user->setRole($role);
            $em->flush();
        }
    }

    if (!$user && $role === 'DRIVER') {
        $driver = $em->getRepository(\App\Entity\Driver::class)->findOneBy(['email' => $email]);
        if ($driver) {
            $driverRole = strtoupper($driver->getRole() ?? 'DRIVER');
            if (in_array($driverRole, $allowed, true)) {
                $role = $driverRole;
            } else {
                $role = 'DRIVER';
            }
        }
    }

    // ---------- TEMP DEBUG (remove after one test) ----------
    error_log('[LOGIN DEBUG] email='.$email.
        ' realmRoles='.json_encode($roleContext['realm']).
        ' clientRoles='.json_encode($roleContext['client']).
        ' allRoles='.json_encode($roleContext['all']).
        ' onlyAllowed='.json_encode($roleContext['preferred']).
        ' pickedRole='.$role.
        ' dbRole='.(isset($dbRole)?$dbRole:'(none)'));
    // --------------------------------------------------------

    $resolvedUser = $user ?? $driver;
    $resolvedId = $resolvedUser?->getId();

    return $this->json([
        'token' => 'Bearer ' . $jwt,
        'refresh_token' => $resp['refresh_token'] ?? null,
        'role' => $role,
        'user' => [
            'userId' => $resolvedId,
            'driverId' => $driver?->getId(),
            'firstName' => $resolvedUser?->getFirstName(),
            'lastName' => $resolvedUser?->getLastName(),
            'email' => $email,
            'profilePhoto' => $resolvedUser?->getProfilePhoto(),
        ],
        // TEMP: include debug once; delete after confirming
        'debug' => [
            'realmRoles' => $roleContext['realm'],
            'clientRoles' => $roleContext['client'],
            'allRoles' => $roleContext['all'],
            'onlyAllowed' => $roleContext['preferred'],
            'pickedRole' => $role,
        ],
    ]);
}



    #[Route('/auth/refresh', methods: ['POST'])]
    public function refresh(Request $req): JsonResponse
    {
        $refresh = (json_decode($req->getContent(), true) ?? [])['refresh_token'] ?? '';
        $resp = $this->http->request('POST',
            $_ENV['KEYCLOAK_BASE'].'/realms/'.$_ENV['KEYCLOAK_REALM'].'/protocol/openid-connect/token',
            [
                'body' => [
                    'grant_type' => 'refresh_token',
                    'client_id' => 'user-service',
                    'refresh_token' => $refresh,
                ],
                'timeout' => 5
            ]
        )->toArray(false);

        return $this->json($resp);
    }

    #[Route('/auth/logout', methods: ['POST'])]
    public function logout(Request $req): JsonResponse
    {
        $data = json_decode($req->getContent(), true) ?? [];
        $refresh = $data['refresh_token'] ?? null;

        if (!$refresh) {
            return $this->json(['error' => 'refresh_token required'], 400);
        }

        $resp = $this->http->request('POST',
            $_ENV['KEYCLOAK_BASE'].'/realms/'.$_ENV['KEYCLOAK_REALM'].'/protocol/openid-connect/logout',
            [
                'body' => [
                    'client_id' => 'user-service',
                    'refresh_token' => $refresh,
                ],
                'timeout' => 5
            ]
        );

        return $this->json(['message' => 'logged out', 'status' => $resp->getStatusCode()]);
    }


  #[Route('/api/profile', methods: ['GET'])]
public function getProfile(Request $request, EM $em): JsonResponse
{
    $kcUser = $this->getUser();
    $email = method_exists($kcUser, 'getEmail') ? $kcUser->getEmail() : null;
    $username = $kcUser->getUserIdentifier();

    // Try email first, then username
    $user = null;
    if ($email) {
        $user = $em->getRepository(\App\Entity\User::class)->findOneBy(['email' => $email]);
    }
    if (!$user && $username) {
        $user = $em->getRepository(\App\Entity\User::class)->findOneBy(['email' => $username]);
    }

    $payload = $this->extractJwtPayload($request);
    $roleContext = $this->extractRoleContext($payload);
    $tokenRole = $roleContext['preferred'][0] ?? 'USER';
    $resolvedEmail = $email ?? $payload['email'] ?? $username;
    $hasDriverRole = in_array('DRIVER', $roleContext['all'], true);

    if (!$user) {
        if ($hasDriverRole) {
            $driver = $em->getRepository(Driver::class)->findOneBy(['email' => $resolvedEmail]);
            if (!$driver) {
                $driver = new Driver();
                $driver->setEmail($resolvedEmail);
                $driver->setRole('DRIVER');
                $driver->setKeycloakId($payload['sub'] ?? $resolvedEmail ?? uniqid('kc_', true));
                $driver->setFirstName($payload['given_name'] ?? null);
                $driver->setLastName($payload['family_name'] ?? null);
                $driver->setAddress($payload['address'] ?? null);
                $driver->setAvailability(false);
                $em->persist($driver);
                $em->flush();
            }
            $user = $driver;
        } else {
            $simple = new SimpleUser();
            $simple->setEmail($resolvedEmail);
            $simple->setRole($tokenRole);
            $simple->setKeycloakId($payload['sub'] ?? $resolvedEmail ?? uniqid('kc_', true));
            $simple->setFirstName($payload['given_name'] ?? null);
            $simple->setLastName($payload['family_name'] ?? null);
            $em->persist($simple);
            $em->flush();
            $user = $simple;
        }
    }

    $isDriver = $user instanceof Driver;

    return $this->json([
        'id' => $user->getId(),
        'firstName' => $user->getFirstName(),
        'lastName' => $user->getLastName(),
        'email' => $user->getEmail(),
        'profilePhoto' => $user->getProfilePhoto(),
        'address' => $user->getAddress(),
        'birthDate' => $user->getBirthDate()?->format('Y-m-d'),
        'role' => $isDriver ? ($user->getRole() ?? 'DRIVER') : ($user->getRole() ?? $tokenRole),
    ]);
}

#[Route('/api/updateProfile', methods: ['PUT'])]
public function updateProfile(Request $req, EM $em): JsonResponse
{
    $kcUser = $this->getUser();
    $email = method_exists($kcUser, 'getEmail') ? $kcUser->getEmail() : null;
    $username = $kcUser->getUserIdentifier();

    // Try to find local user by email or username
    $user = null;
    if ($email) {
        $user = $em->getRepository(\App\Entity\User::class)->findOneBy(['email' => $email]);
    }
    if (!$user && $username) {
        $user = $em->getRepository(\App\Entity\User::class)->findOneBy(['email' => $username]);
    }

    $payload = $this->extractJwtPayload($req);
    $roleContext = $this->extractRoleContext($payload);
    $tokenRole = $roleContext['preferred'][0] ?? 'USER';
    $resolvedEmail = $email ?? $payload['email'] ?? $username;
    $hasDriverRole = in_array('DRIVER', $roleContext['all'], true);

    $isDriver = $user instanceof Driver;
    if (!$user) {
        if ($hasDriverRole) {
            $driver = $em->getRepository(Driver::class)->findOneBy(['email' => $resolvedEmail]);

            if (!$driver) {
                $driver = new Driver();
                $driver->setEmail($resolvedEmail);
                $driver->setRole('DRIVER');
                $driver->setKeycloakId($payload['sub'] ?? $resolvedEmail ?? uniqid('kc_', true));
                $driver->setFirstName($payload['given_name'] ?? null);
                $driver->setLastName($payload['family_name'] ?? null);
                $driver->setAddress($payload['address'] ?? null);
                $driver->setAvailability(false);
                $em->persist($driver);
                $em->flush();
            }

            $user = $driver;
            $isDriver = true;
        } else {
            $simple = new SimpleUser();
            $simple->setEmail($resolvedEmail);
            $simple->setRole($tokenRole);
            $simple->setKeycloakId($payload['sub'] ?? $resolvedEmail ?? uniqid('kc_', true));
            $simple->setFirstName($payload['given_name'] ?? null);
            $simple->setLastName($payload['family_name'] ?? null);
            $em->persist($simple);
            $em->flush();

            $user = $simple;
            $isDriver = false;
        }
    }

    $data = json_decode($req->getContent(), true) ?? [];

    // ✅ Only update safe fields
    if (isset($data['firstName'])) $user->setFirstName($data['firstName']);
    if (isset($data['lastName'])) $user->setLastName($data['lastName']);
    if (isset($data['address'])) $user->setAddress($data['address']);
    if (isset($data['profilePhoto'])) $user->setProfilePhoto($data['profilePhoto']);
    if (isset($data['birthDate'])) {
        try {
            $user->setBirthDate(new \DateTime($data['birthDate']));
        } catch (\Exception $e) {
            return $this->json(['error' => 'Invalid birthDate format (expected YYYY-MM-DD)'], 400);
        }
    }

    $em->flush();

    return $this->json([
        'message' => 'Profile updated successfully',
        'updated' => [
            'firstName' => $user->getFirstName(),
            'lastName' => $user->getLastName(),
            'address' => $user->getAddress(),
            'birthDate' => $user->getBirthDate()?->format('Y-m-d'),
            'profilePhoto' => $user->getProfilePhoto(),
            'role' => $user->getRole() ?? ($isDriver ? 'DRIVER' : $tokenRole ?? 'USER'),
        ],
    ]);
}



#[Route('/api/change-password', methods: ['PUT'])]
public function changePassword(Request $req, KeycloakAdmin $kc, EM $em): JsonResponse
{
    $kcUser = $this->getUser();
    $email = method_exists($kcUser, 'getEmail') ? $kcUser->getEmail() : null;
    $username = $kcUser->getUserIdentifier();

    // 1️⃣ Find local user to get Keycloak ID
    $user = null;
    if ($email) {
        $user = $em->getRepository(\App\Entity\User::class)->findOneBy(['email' => $email]);
    }
    if (!$user) {
        $user = $em->getRepository(\App\Entity\User::class)->findOneBy(['email' => $username]);
    }

    if (!$user) {
        return $this->json(['error' => 'User not found in local DB'], 404);
    }

    $data = json_decode($req->getContent(), true) ?? [];
    $newPassword = $data['newPassword'] ?? null;

    if (!$newPassword || strlen($newPassword) < 6) {
        return $this->json(['error' => 'Password must be at least 6 characters'], 400);
    }

    // 2️⃣ Ask Keycloak to update the password
    try {
        $kc->resetUserPassword($user->getKeycloakId(), $newPassword);
    } catch (\Throwable $e) {
        return $this->json([
            'error' => 'Failed to change password in Keycloak',
            'details' => $e->getMessage()
        ], 500);
    }

    return $this->json(['message' => 'Password changed successfully']);
}


#[Route('/api/deleteUser', methods: ['DELETE'])]
public function deleteProfile(KeycloakAdmin $kc, EM $em): JsonResponse
{
    $kcUser = $this->getUser();
    $email = method_exists($kcUser, 'getEmail') ? $kcUser->getEmail() : null;
    $username = $kcUser->getUserIdentifier();

    // 1️⃣ Find local user
    $user = null;
    if ($email) {
        $user = $em->getRepository(\App\Entity\User::class)->findOneBy(['email' => $email]);
    }
    if (!$user) {
        $user = $em->getRepository(\App\Entity\User::class)->findOneBy(['email' => $username]);
    }

    if (!$user) {
        return $this->json(['error' => 'User not found in local DB'], 404);
    }

    $keycloakId = $user->getKeycloakId();

    // 2️⃣ Delete in Keycloak first
    try {
        $kc->deleteUser($keycloakId);
    } catch (\Throwable $e) {
        return $this->json([
            'error' => 'Failed to delete user in Keycloak',
            'details' => $e->getMessage()
        ], 500);
    }

    // 3️⃣ Then delete locally
    $em->remove($user);
    $em->flush();

    return $this->json(['message' => 'User deleted successfully']);
}


#[Route('/api/admin/users', methods: ['GET'])]
#[IsGranted('ROLE_ADMIN')]
public function listAllUsers(EM $em): JsonResponse
{
    $users = $em->getRepository(\App\Entity\User::class)->findAll();
    return $this->json($users);
}

private function extractJwtPayload(Request $request): array
{
    $authHeader = $request->headers->get('Authorization');
    if (!$authHeader || !str_starts_with($authHeader, 'Bearer ')) {
        return [];
    }

    try {
        $token = substr($authHeader, 7);
        $parts = explode('.', $token);
        if (count($parts) < 2) {
            return [];
        }
        $payload = base64_decode(strtr($parts[1], '-_', '+/'));
        $data = json_decode($payload, true);
        return is_array($data) ? $data : [];
    } catch (\Throwable $e) {
        return [];
    }
}

private function extractRoleContext(?array $payload): array
{
    $payload = $payload ?? [];

    $realmRoles = array_map('strtoupper', $payload['realm_access']['roles'] ?? []);
    $clientRoles = [];
    if (!empty($payload['resource_access'])) {
        foreach ($payload['resource_access'] as $clientData) {
            if (!empty($clientData['roles'])) {
                $clientRoles = array_merge($clientRoles, array_map('strtoupper', $clientData['roles']));
            }
        }
    }

    $allRoles = array_values(array_unique(array_merge($realmRoles, $clientRoles)));
    $allowed = ['ADMIN', 'DRIVER', 'USER'];
    $preferred = [];
    foreach ($allowed as $candidate) {
        if (in_array($candidate, $allRoles, true)) {
            $preferred[] = $candidate;
        }
    }

    return [
        'realm' => $realmRoles,
        'client' => $clientRoles,
        'all' => $allRoles,
        'preferred' => $preferred,
    ];
}
}

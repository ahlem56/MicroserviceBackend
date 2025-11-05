<?php

namespace App\Controller;

use App\Dto\RegisterDto;
use App\Entity\SimpleUser;
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
        $dto->username = $payload['username'] ?? null;
        $dto->email = $payload['email'] ?? null;
        $dto->password = $payload['password'] ?? null;
        $dto->role = strtoupper($payload['role'] ?? 'USER');

        $errors = $validator->validate($dto);
        if (count($errors)) {
            return $this->json(['error' => (string)$errors], 400);
        }

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

    $realmRoles = array_map('strtoupper', $payload['realm_access']['roles'] ?? []);
    $clientRoles = [];
    if (!empty($payload['resource_access'])) {
        foreach ($payload['resource_access'] as $clientData) {
            if (!empty($clientData['roles'])) {
                $clientRoles = array_merge($clientRoles, array_map('strtoupper', $clientData['roles']));
            }
        }
    }

    // 4) Whitelist + priority (ADMIN > DRIVER > USER)
    $allowed = ['ADMIN', 'DRIVER', 'USER'];
    $allRoles = array_values(array_unique(array_merge($realmRoles, $clientRoles)));
    $onlyAllowed = array_values(array_intersect($allowed, $allRoles));
    $role = $onlyAllowed[0] ?? 'USER';

    // 5) DB override if valid
    $user = $em->getRepository(\App\Entity\SimpleUser::class)->findOneBy(['email' => $email]);
    if ($user && $user->getRole()) {
        $dbRole = strtoupper($user->getRole());
        if (in_array($dbRole, $allowed, true)) {
            $role = $dbRole;
        }
    }

    // ---------- TEMP DEBUG (remove after one test) ----------
    error_log('[LOGIN DEBUG] email='.$email.
        ' realmRoles='.json_encode($realmRoles).
        ' clientRoles='.json_encode($clientRoles).
        ' allRoles='.json_encode($allRoles).
        ' onlyAllowed='.json_encode($onlyAllowed).
        ' pickedRole='.$role.
        ' dbRole='.(isset($dbRole)?$dbRole:'(none)'));
    // --------------------------------------------------------

    return $this->json([
        'token' => 'Bearer ' . $jwt,
        'refresh_token' => $resp['refresh_token'] ?? null,
        'role' => $role,
        'user' => [
            'userId' => $user?->getId(),
            'firstName' => $user?->getFirstName(),
            'lastName' => $user?->getLastName(),
            'email' => $email,
        ],
        // TEMP: include debug once; delete after confirming
        'debug' => [
            'realmRoles' => $realmRoles,
            'clientRoles' => $clientRoles,
            'allRoles' => $allRoles,
            'onlyAllowed' => $onlyAllowed,
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
public function getProfile(EM $em): JsonResponse
{
    $kcUser = $this->getUser();
    $email = method_exists($kcUser, 'getEmail') ? $kcUser->getEmail() : null;
    $username = $kcUser->getUserIdentifier();

    // Try email first, then username
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

    return $this->json([
        'id' => $user->getId(),
        'firstName' => $user->getFirstName(),
        'lastName' => $user->getLastName(),
        'email' => $user->getEmail(),
        'profilePhoto' => $user->getProfilePhoto(),
        'address' => $user->getAddress(),
        'birthDate' => $user->getBirthDate()?->format('Y-m-d'),
        'role' => $user->getRole(),
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
    if (!$user) {
        $user = $em->getRepository(\App\Entity\User::class)->findOneBy(['email' => $username]);
    }

    if (!$user) {
        return $this->json(['error' => 'User not found in local DB'], 404);
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
}

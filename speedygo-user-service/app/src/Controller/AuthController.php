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
public function login(Request $req): JsonResponse
{
    $data = json_decode($req->getContent(), true) ?? [];
    $username = $data['username'] ?? '';
    $password = $data['password'] ?? '';

    $resp = $this->http->request('POST',
        $_ENV['KEYCLOAK_BASE'].'/realms/'.$_ENV['KEYCLOAK_REALM'].'/protocol/openid-connect/token',
        [
            'body' => [
                'grant_type' => 'password',
                'client_id' => $_ENV['KEYCLOAK_ADMIN_CLIENT_ID'], // ✅ use the ID from .env
                'client_secret' => $_ENV['KEYCLOAK_ADMIN_CLIENT_SECRET'], // ✅ include secret
                'username' => $username,
                'password' => $password,
            ],
            'timeout' => 5
        ]
    )->toArray(false);

    return $this->json($resp);
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
}

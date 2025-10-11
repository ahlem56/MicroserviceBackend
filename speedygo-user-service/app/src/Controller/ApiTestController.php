<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;

class ApiTestController extends AbstractController
{
    #[Route('/api/test', name: 'api_test', methods: ['GET'])]
    public function test(): JsonResponse
    {
        $user = $this->getUser();

        return $this->json([
            'message' => 'Token valid ✅',
            'username' => $user?->getUserIdentifier(),
            'email' => $user?->getEmail(),
            'roles' => $user?->getRoles(),
        ]);
    }
}

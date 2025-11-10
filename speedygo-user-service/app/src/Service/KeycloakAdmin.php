<?php
namespace App\Service;

use Symfony\Contracts\HttpClient\HttpClientInterface;

class KeycloakAdmin
{
    public function __construct(
        private HttpClientInterface $http,
        private string $base,
        private string $realm,
        private string $clientId,
        private string $clientSecret
    ) {}

    private function token(): string {
        $resp = $this->http->request('POST', "{$this->base}/realms/{$this->realm}/protocol/openid-connect/token", [
            'body' => [
                'grant_type' => 'client_credentials',
                'client_id' => $this->clientId,
                'client_secret' => $this->clientSecret,
            ],
            'timeout' => 5,
        ])->toArray(false);

        return $resp['access_token'] ?? throw new \RuntimeException('Keycloak admin token fail');
    }

    public function createUser(string $username, string $email, string $password, array $realmRoles = ['USER']): string {
        $token = $this->token();

        // 1) create user
        $r = $this->http->request('POST', "{$this->base}/admin/realms/{$this->realm}/users", [
            'headers' => ['Authorization' => "Bearer {$token}"],
            'json' => [
                'username' => $username,
                'email' => $email,
                'enabled' => true,
                'emailVerified' => false,
            ],
        ]);
        $status = $r->getStatusCode();
        $userExists = ($status === 409);
        
        // Allow 201 Created and 409 Conflict (already exists)
        if (!in_array($status, [201, 204, 409], true)) {
            $errorBody = '';
            try {
                $errorBody = $r->getContent(false);
            } catch (\Throwable $e) {
                $errorBody = 'Could not read error body';
            }
            throw new \RuntimeException("Keycloak create user failed with status {$status}: {$errorBody}");
        }

        // 2) find by username to get id
        $list = $this->http->request('GET', "{$this->base}/admin/realms/{$this->realm}/users", [
            'headers' => ['Authorization' => "Bearer {$token}"],
            'query' => ['username' => $username, 'exact' => 'true'],
        ])->toArray(false);
        $id = $list[0]['id'] ?? null;
        if (!$id) throw new \RuntimeException('Keycloak user id not found');

        // 3) set password (only if user was just created, not if already exists)
        if (!$userExists) {
            $this->http->request('PUT', "{$this->base}/admin/realms/{$this->realm}/users/{$id}/reset-password", [
                'headers' => ['Authorization' => "Bearer {$token}"],
                'json' => ['type' => 'password', 'temporary' => false, 'value' => $password],
            ]);
        }

        // 4) assign realm-roles (always update roles)
        if ($realmRoles) {
            $allRoles = $this->http->request('GET', "{$this->base}/admin/realms/{$this->realm}/roles", [
                'headers' => ['Authorization' => "Bearer {$token}"],
            ])->toArray(false);

            $toAssign = array_values(array_filter($allRoles, fn($r) => in_array($r['name'], $realmRoles, true)));
            if ($toAssign) {
                $this->http->request('POST', "{$this->base}/admin/realms/{$this->realm}/users/{$id}/role-mappings/realm", [
                    'headers' => ['Authorization' => "Bearer {$token}"],
                    'json' => $toAssign,
                ]);
            }
        }

        return $id; // keycloakId
    }


    public function resetUserPassword(string $keycloakId, string $newPassword): void
{
    $token = $this->token();

    $this->http->request(
        'PUT',
        "{$this->base}/admin/realms/{$this->realm}/users/{$keycloakId}/reset-password",
        [
            'headers' => ['Authorization' => "Bearer {$token}"],
            'json' => [
                'type' => 'password',
                'temporary' => false,
                'value' => $newPassword,
            ],
        ]
    );
}


public function deleteUser(string $keycloakId): void
{
    $token = $this->token();

    $response = $this->http->request(
        'DELETE',
        "{$this->base}/admin/realms/{$this->realm}/users/{$keycloakId}",
        ['headers' => ['Authorization' => "Bearer {$token}"]]
    );

    if ($response->getStatusCode() >= 300) {
        throw new \RuntimeException('Failed to delete user in Keycloak (status '.$response->getStatusCode().')');
    }
}

}

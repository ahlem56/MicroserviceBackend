<?php

namespace App\Security;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Security\Http\Authenticator\AbstractAuthenticator;
use Symfony\Component\Security\Http\Authenticator\Passport\Passport;
use Symfony\Component\Security\Http\Authenticator\Passport\SelfValidatingPassport;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use Jose\Component\Core\AlgorithmManager;
use Jose\Component\Core\JWKSet;
use Jose\Component\Signature\Algorithm\RS256;
use Jose\Component\Signature\JWSVerifier;
use Jose\Component\Signature\Serializer\CompactSerializer;

class KeycloakGuardAuthenticator extends AbstractAuthenticator
{
    public function __construct(
        private HttpClientInterface $http,
        private string $jwksUrl,
        private string $issuer
    ) {}

    public function supports(Request $request): ?bool
    {
        return $request->headers->has('Authorization');
    }

    public function authenticate(Request $request): Passport
    {
        $authHeader = $request->headers->get('Authorization');
        error_log('[AUTH DEBUG] Authorization header=' . ($authHeader ?? '(none)'));
        if (!$authHeader || !str_starts_with($authHeader, 'Bearer ')) {
            throw new AuthenticationException('No Bearer token found');
        }

        $token = substr($authHeader, 7);
        $serializer = new CompactSerializer();
        $jws = $serializer->unserialize($token);
        $jwsHeader = $jws->getSignature(0)->getProtectedHeader();
        $kid = $jwsHeader['kid'] ?? null;

        // 🔐 Fetch JWKS (public keys from Keycloak)
        $response = $this->http->request('GET', $this->jwksUrl, ['timeout' => 5]);
        if ($response->getStatusCode() !== 200) {
            error_log('[AUTH DEBUG] Failed to fetch JWKS status=' . $response->getStatusCode());
            throw new AuthenticationException('Cannot fetch JWKS');
        }

        $jwksData = json_decode($response->getContent(false), true);
        if (!isset($jwksData['keys'])) {
            throw new AuthenticationException('Invalid JWKS data structure');
        }

        $keySet = JWKSet::createFromKeyData($jwksData);
        $key = null;
        foreach ($keySet->all() as $candidate) {
            if (($candidate->has('kid') && $candidate->get('kid') === $kid) && $candidate->get('use') === 'sig') {
                $key = $candidate;
                break;
            }
        }
        if (!$key) {
            error_log('[AUTH DEBUG] No matching key for kid=' . ($kid ?? '(none)'));
            throw new AuthenticationException("No matching key for kid: {$kid}");
        }

        // 🔏 Verify token signature
        $verifier = new JWSVerifier(new AlgorithmManager([new RS256()]));
        if (!$verifier->verifyWithKey($jws, $key, 0)) {
            error_log('[AUTH DEBUG] Invalid signature for token');
            throw new AuthenticationException('Invalid signature');
        }

        $payload = json_decode($jws->getPayload(), true);

        // ✅ Issuer validation with docker/local tolerance
        $tokenIssuer = rtrim($payload['iss'] ?? '', '/');
        $expectedIssuer = rtrim($this->issuer, '/');
        $normalizedTokenIssuer = str_replace('localhost', 'host.docker.internal', $tokenIssuer);
        $normalizedExpectedIssuer = str_replace('localhost', 'host.docker.internal', $expectedIssuer);

        if ($normalizedTokenIssuer !== $normalizedExpectedIssuer) {
            error_log('[AUTH DEBUG] Issuer mismatch token=' . $normalizedTokenIssuer . ' expected=' . $normalizedExpectedIssuer);
            throw new AuthenticationException('Issuer mismatch');
        }

        // ✅ Build a virtual authenticated Keycloak user
        return new SelfValidatingPassport(
            new UserBadge($payload['preferred_username'], function () use ($payload) {
                return new class($payload) implements UserInterface {
                    public function __construct(private array $data) {}

                    public function getUserIdentifier(): string
                    {
                        return $this->data['preferred_username'] ?? 'anonymous';
                    }

                    public function getEmail(): ?string
                    {
                        return $this->data['email'] ?? null;
                    }

                    public function getRoles(): array
                    {
                        $roles = $this->data['realm_access']['roles'] ?? [];

                        // 🔹 Normalize Keycloak roles to Symfony-style (ROLE_ADMIN, ROLE_DRIVER, etc.)
                        $mappedRoles = array_map(fn($r) => 'ROLE_' . strtoupper($r), $roles);

                        // 🔹 Always include ROLE_USER
                        if (!in_array('ROLE_USER', $mappedRoles, true)) {
                            $mappedRoles[] = 'ROLE_USER';
                        }

                        return array_unique($mappedRoles);
                    }

                    public function eraseCredentials(): void {}
                };
            })
        );
    }

    public function onAuthenticationSuccess(Request $request, $token, string $firewallName): ?JsonResponse
    {
        // Continue request if authenticated successfully
        return null;
    }

    public function onAuthenticationFailure(Request $request, AuthenticationException $exception): ?JsonResponse
    {
        return new JsonResponse([
            'message' => 'Invalid JWT Token',
            'error' => $exception->getMessage(),
        ], 401);
    }
}

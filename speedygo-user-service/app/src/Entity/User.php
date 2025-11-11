<?php

namespace App\Entity;

use App\Entity\SimpleUser;
use App\Entity\Driver;
use App\Entity\Admin;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
#[ORM\Table(name: 'users')]
#[ORM\InheritanceType('JOINED')]
#[ORM\DiscriminatorColumn(name: 'user_type', type: 'string')]
#[ORM\DiscriminatorMap([
    'SIMPLE' => SimpleUser::class,
    'DRIVER' => Driver::class,
    'ADMIN' => Admin::class
])]
abstract class User
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(type: 'integer')]
    protected ?int $id = null;

    #[ORM\Column(length: 100, nullable: true)]
    protected ?string $firstName = null;

    #[ORM\Column(length: 100, nullable: true)]
    protected ?string $lastName = null;

    #[ORM\Column(length: 180, unique: true)]
    protected ?string $email = null;

    #[ORM\Column(length: 255, nullable: true)]
protected ?string $password = null;


    #[ORM\Column(length: 255, nullable: true)]
    protected ?string $profilePhoto = null;

    #[ORM\Column(length: 255, nullable: true)]
    protected ?string $address = null;

    #[ORM\Column(nullable: true)]
    protected ?int $cin = null;

    #[ORM\Column(type: 'date', nullable: true)]
    protected ?\DateTimeInterface $birthDate = null;

    #[ORM\Column(length: 255, nullable: true)]
    protected ?string $emergencyContactEmail = null;

    #[ORM\Column(length: 50, nullable: true)]
    protected ?string $role = null;

    #[ORM\Column(length: 36, unique: true, nullable: true)]
    protected ?string $keycloakId = null;

    // 🔹 Getters / Setters
    public function getId(): ?int { return $this->id; }

    public function getFirstName(): ?string { return $this->firstName; }
    public function setFirstName(?string $firstName): static { $this->firstName = $firstName; return $this; }

    public function getLastName(): ?string { return $this->lastName; }
    public function setLastName(?string $lastName): static { $this->lastName = $lastName; return $this; }

    public function getEmail(): ?string { return $this->email; }
    public function setEmail(string $email): static { $this->email = $email; return $this; }

    public function getPassword(): ?string { return $this->password; }
    public function setPassword(?string $password): static { $this->password = $password; return $this; }

    public function getProfilePhoto(): ?string { return $this->profilePhoto; }
    public function setProfilePhoto(?string $photo): static { $this->profilePhoto = $photo; return $this; }

    public function getAddress(): ?string { return $this->address; }
    public function setAddress(?string $address): static { $this->address = $address; return $this; }

    public function getCin(): ?int { return $this->cin; }
    public function setCin(?int $cin): static { $this->cin = $cin; return $this; }

    public function getBirthDate(): ?\DateTimeInterface { return $this->birthDate; }
    public function setBirthDate(?\DateTimeInterface $date): static { $this->birthDate = $date; return $this; }

    public function getEmergencyContactEmail(): ?string { return $this->emergencyContactEmail; }
    public function setEmergencyContactEmail(?string $email): static { $this->emergencyContactEmail = $email; return $this; }

    public function getRole(): ?string { return $this->role; }
    public function setRole(?string $role): static { $this->role = $role; return $this; }

    public function getKeycloakId(): ?string { return $this->keycloakId; }
    public function setKeycloakId(?string $id): static { $this->keycloakId = $id; return $this; }
}

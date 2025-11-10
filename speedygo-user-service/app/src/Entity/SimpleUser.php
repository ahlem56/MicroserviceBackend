<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class SimpleUser extends User
{
    #[ORM\Column(length: 20, nullable: true)]
    private ?string $phoneNumber = null;

    #[ORM\Column(length: 50, nullable: true)]
    private ?string $city = null;

    #[ORM\Column(length: 50, nullable: true)]
    private ?string $country = null;

    #[ORM\Column(type: 'boolean')]
    private bool $subscription = false;

    #[ORM\Column(type: 'integer', nullable: true)]
    private ?int $points = 0;

    #[ORM\Column(type: 'float', nullable: true)]
    private ?float $averageRating = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $bio = null;

    // getters/setters…
}

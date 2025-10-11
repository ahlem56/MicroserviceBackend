<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class Driver extends User
{
    #[ORM\Column(length: 100, nullable: true)]
    private ?string $licenseNumber = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $insuranceDetails = null;

    #[ORM\Column(type: 'float', nullable: true)]
    private ?float $performanceRating = null;

    #[ORM\Column(length: 100, nullable: true)]
    private ?string $schedule = null;

    #[ORM\Column(type: 'boolean')]
    private bool $availability = false;

    public function isAvailable(): bool
    {
        return $this->availability;
    }

    public function setAvailability(bool $availability): static
    {
        $this->availability = $availability;
        return $this;
    }
}

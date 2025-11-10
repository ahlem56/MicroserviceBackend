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

    // -----------------------------
    // ✅ Getters and setters
    // -----------------------------

    public function getLicenseNumber(): ?string
    {
        return $this->licenseNumber;
    }

    public function setLicenseNumber(?string $licenseNumber): static
    {
        $this->licenseNumber = $licenseNumber;
        return $this;
    }

    public function getInsuranceDetails(): ?string
    {
        return $this->insuranceDetails;
    }

    public function setInsuranceDetails(?string $insuranceDetails): static
    {
        $this->insuranceDetails = $insuranceDetails;
        return $this;
    }

    public function getPerformanceRating(): ?float
    {
        return $this->performanceRating;
    }

    public function setPerformanceRating(?float $performanceRating): static
    {
        $this->performanceRating = $performanceRating;
        return $this;
    }

    public function getSchedule(): ?string
    {
        return $this->schedule;
    }

    public function setSchedule(?string $schedule): static
    {
        $this->schedule = $schedule;
        return $this;
    }

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

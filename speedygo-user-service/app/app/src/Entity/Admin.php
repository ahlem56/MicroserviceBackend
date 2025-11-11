<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class Admin extends User
{
    #[ORM\Column(length: 255, nullable: true)]
    private ?string $permissions = null;

    public function getPermissions(): ?string
    {
        return $this->permissions;
    }

    public function setPermissions(?string $permissions): static
    {
        $this->permissions = $permissions;
        return $this;
    }
}

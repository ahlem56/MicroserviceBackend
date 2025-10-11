<?php
// src/Dto/RegisterDto.php
namespace App\Dto;


use Symfony\Component\Validator\Constraints as Assert;

class RegisterDto
{
    #[Assert\NotBlank] #[Assert\Length(min:3, max:100)]
    public ?string $username = null;

    #[Assert\NotBlank] #[Assert\Email]
    public ?string $email = null;

    #[Assert\NotBlank] #[Assert\Length(min:6)]
    public ?string $password = null;

    #[Assert\Choice(choices: ['USER','DRIVER','ADMIN'])]
    public ?string $role = 'USER';
}

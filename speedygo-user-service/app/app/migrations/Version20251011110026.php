<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20251011110026 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE SEQUENCE users_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE TABLE admin (id INT NOT NULL, permissions VARCHAR(255) DEFAULT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE TABLE driver (id INT NOT NULL, license_number VARCHAR(100) DEFAULT NULL, insurance_details VARCHAR(255) DEFAULT NULL, performance_rating DOUBLE PRECISION DEFAULT NULL, schedule VARCHAR(100) DEFAULT NULL, availability BOOLEAN NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE TABLE users (id INT NOT NULL, first_name VARCHAR(100) NOT NULL, last_name VARCHAR(100) NOT NULL, email VARCHAR(180) NOT NULL, password VARCHAR(255) DEFAULT NULL, profile_photo VARCHAR(255) DEFAULT NULL, address VARCHAR(255) DEFAULT NULL, cin INT DEFAULT NULL, birth_date DATE DEFAULT NULL, emergency_contact_email VARCHAR(255) DEFAULT NULL, role VARCHAR(50) NOT NULL, user_type VARCHAR(255) NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_1483A5E9E7927C74 ON users (email)');
        $this->addSql('ALTER TABLE admin ADD CONSTRAINT FK_880E0D76BF396750 FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE driver ADD CONSTRAINT FK_11667CD9BF396750 FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE SCHEMA public');
        $this->addSql('DROP SEQUENCE users_id_seq CASCADE');
        $this->addSql('ALTER TABLE admin DROP CONSTRAINT FK_880E0D76BF396750');
        $this->addSql('ALTER TABLE driver DROP CONSTRAINT FK_11667CD9BF396750');
        $this->addSql('DROP TABLE admin');
        $this->addSql('DROP TABLE driver');
        $this->addSql('DROP TABLE users');
    }
}

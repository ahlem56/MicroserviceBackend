<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20251011112052 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('DROP SEQUENCE simple_user_id_seq CASCADE');
        $this->addSql('ALTER TABLE simple_user ADD phone_number VARCHAR(20) DEFAULT NULL');
        $this->addSql('ALTER TABLE simple_user ADD city VARCHAR(50) DEFAULT NULL');
        $this->addSql('ALTER TABLE simple_user ADD country VARCHAR(50) DEFAULT NULL');
        $this->addSql('ALTER TABLE simple_user ADD subscription BOOLEAN NOT NULL');
        $this->addSql('ALTER TABLE simple_user ADD points INT DEFAULT NULL');
        $this->addSql('ALTER TABLE simple_user ADD average_rating DOUBLE PRECISION DEFAULT NULL');
        $this->addSql('ALTER TABLE simple_user ADD bio VARCHAR(255) DEFAULT NULL');
        $this->addSql('ALTER TABLE simple_user ADD CONSTRAINT FK_2272B4F0BF396750 FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE SCHEMA public');
        $this->addSql('CREATE SEQUENCE simple_user_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('ALTER TABLE simple_user DROP CONSTRAINT FK_2272B4F0BF396750');
        $this->addSql('ALTER TABLE simple_user DROP phone_number');
        $this->addSql('ALTER TABLE simple_user DROP city');
        $this->addSql('ALTER TABLE simple_user DROP country');
        $this->addSql('ALTER TABLE simple_user DROP subscription');
        $this->addSql('ALTER TABLE simple_user DROP points');
        $this->addSql('ALTER TABLE simple_user DROP average_rating');
        $this->addSql('ALTER TABLE simple_user DROP bio');
    }
}

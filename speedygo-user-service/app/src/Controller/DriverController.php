<?php

namespace App\Controller;

use App\Entity\Driver;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/driver')]
class DriverController extends AbstractController
{
    public function __construct(private EntityManagerInterface $em) {}

    /**
     * ✅ 1. Get all available drivers (availability = true)
     */
    #[Route('/get-available-drivers', name: 'get_available_drivers', methods: ['GET'])]
    public function getAvailableDrivers(): JsonResponse
    {
        $drivers = $this->em->getRepository(Driver::class)->findBy(['availability' => true]);

        if (!$drivers) {
            return $this->json(['message' => 'No available drivers found'], 404);
        }

        // Return simple array (no circular refs)
        $data = array_map(fn(Driver $d) => [
            'userId' => $d->getId(),
            'firstName' => $d->getFirstName(),
            'lastName' => $d->getLastName(),
            'email' => $d->getEmail(),
            'availability' => $d->isAvailable(),
            'licenseNumber' => $d->getLicenseNumber(),
            'insuranceDetails' => $d->getInsuranceDetails(),
            'performanceRating' => $d->getPerformanceRating(),
        ], $drivers);

        return $this->json($data, 200);
    }

    /**
     * ✅ 2. Get all drivers (admin use)
     */
    #[Route('/get-all-drivers', name: 'get_all_drivers', methods: ['GET'])]
    public function getAllDrivers(): JsonResponse
    {
        $drivers = $this->em->getRepository(Driver::class)->findAll();

        $data = array_map(fn(Driver $d) => [
            'userId' => $d->getId(),
            'firstName' => $d->getFirstName(),
            'lastName' => $d->getLastName(),
            'email' => $d->getEmail(),
            'availability' => $d->isAvailable(),
            'licenseNumber' => $d->getLicenseNumber(),
            'insuranceDetails' => $d->getInsuranceDetails(),
            'performanceRating' => $d->getPerformanceRating(),
        ], $drivers);

        return $this->json($data);
    }

    /**
     * ✅ 3. Find driver by ID
     */
    #[Route('/find-driver/{id}', name: 'find_driver', methods: ['GET'])]
    public function findDriver(int $id): JsonResponse
    {
        $driver = $this->em->getRepository(Driver::class)->find($id);

        if (!$driver) {
            return $this->json(['error' => 'Driver not found'], 404);
        }

        $data = [
            'userId' => $driver->getId(),
            'firstName' => $driver->getFirstName(),
            'lastName' => $driver->getLastName(),
            'email' => $driver->getEmail(),
            'availability' => $driver->isAvailable(),
            'licenseNumber' => $driver->getLicenseNumber(),
            'insuranceDetails' => $driver->getInsuranceDetails(),
            'performanceRating' => $driver->getPerformanceRating(),
        ];

        return $this->json($data);
    }

    /**
     * ✅ 4. Update driver availability or info
     */
    #[Route('/update/{id}', name: 'update_driver', methods: ['PUT'])]
    public function updateDriver(Request $request, int $id): JsonResponse
    {
        $driver = $this->em->getRepository(Driver::class)->find($id);
        if (!$driver) {
            return $this->json(['error' => 'Driver not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        if (isset($data['availability'])) {
            $driver->setAvailability((bool)$data['availability']);
        }
        if (isset($data['licenseNumber'])) {
            $driver->setLicenseNumber($data['licenseNumber']);
        }
        if (isset($data['insuranceDetails'])) {
            $driver->setInsuranceDetails($data['insuranceDetails']);
        }

        $this->em->flush();

        return $this->json([
            'message' => 'Driver updated successfully',
            'updated' => [
                'availability' => $driver->isAvailable(),
                'licenseNumber' => $driver->getLicenseNumber(),
                'insuranceDetails' => $driver->getInsuranceDetails(),
            ],
        ]);
    }
}

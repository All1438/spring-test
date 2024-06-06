package com.stack.park.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import com.stack.park.entities.Park;
import com.stack.park.repositories.ParkRepository;


@SpringBootTest // pour les tests d'intégration qui nécessitent la contexte complet de l'application
@ActiveProfiles("test") // pour utilisé le profil de test et la configuration H2. ce qui inclut le fichier 'application-test.properties'
@Transactional // garantissant un état propre de la base de données pour chaque test
public class ParkServiceTest {

    @Mock // permettant de simuler leur comportement. au lieu d'utilisé une véritable instance qui pourrait impliquer des opérations sur une base de données
    private ParkRepository parkRepository;

    @InjectMocks // pour créer l'instance de la classe à tester et injecter les mocks dans cette instance
    private ParkService parkService;

    private Park park;

    @BeforeEach
    void setUp() {
        // initialise les mocks
        MockitoAnnotations.openMocks(this);
        
        park = new Park();
        park.setParkId(2000);
        park.setParkName("Dallas Fort Worth International Airport");
        park.setCapacity(40000);
        park.setOccupiedSpace(1000);
    }

    @Test
    void testCreateParkSuccess() {
        when(parkRepository.save(any(Park.class))).thenReturn(park);

        Park createdPark = parkService.create(park);
        assertNotNull(createdPark); // assertNotNull = vérifie que l'objet n'est pas null
        assertEquals(park.getParkName(), createdPark.getParkName()); // assertEqual = vérifie que le nom du park créer (createdPark) correspondent au nom du park initial ('park') # vérifie si la park à bien été insérer
    }

    @Test
    void testCreateParkOccupiedSpaceExceedsCapacity() {
        park.setOccupiedSpace(50000);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> { // assertThrows() = vérifie si une exception de type 'IllegalArgumentException' est levée lors de l'execution de la méthode create de ParkService
            parkService.create(park);
        });

        assertEquals("Occupied space cannot exceed capacity", thrown.getMessage()); // thrown.getMessage() = récupère le message de l'exception '' levé par la méthode 'create'
    }

    @Test
    void testCreateParkDuplicateName() {
        
    }




}

package com.stack.park.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stack.park.entities.Park;
import com.stack.park.services.ParkService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ParkController.class) // configure pour tester uniquement le contrôleur spécifié qui est dans main
public class ParkControllerTest {

    @Autowired // inject mockMvc pour executer et valider les requêtes HTTP
    private MockMvc mockMvc;

    @MockBean
    private ParkService parkService;

    @Autowired
    private ObjectMapper objectMapper;

    private Park park; 

    @BeforeEach // pour spécifier qu'une méthode doit executer avant le test
    void setUp() {
        park = new Park();
        park.setParkId(21213);
        park.setParkName("Dallas Fort Worth International Airport");
        park.setCapacity(40000);
        park.setOccupiedSpace(100000);
    }

    @Test
    void createUser() throws Exception {
        when(parkService.create(any(Park.class))).thenReturn(park); // indique que lorsqu'une méthode create est appelé sur l'objet 'parkService'
        // create(any(Park.class)) = permet de dire que peu importe quel objet 'Park' est passé à la méthode, retournez simplement 'park'

        mockMvc.perform(post("/api/parks")
                .contentType(MediaType.APPLICATION_JSON) // spécifie le type de contenu de la requête HTTP (ici c'est sous forme JSON)
                .content(objectMapper.writeValueAsString(park))) // ajoute le contenu de la requête HTTP. nous utilisons 'objectMapper', un utilitaire Jackson pour convertir un objet Java en une chaine JSON
                .andExpect(status().isCreated()) // andExpect() = vérifie que le status de la réponse est 201 Created
                .andExpect(jsonPath("$.name").value(park.getParkName())); // vérifie que la valeur de cette clé correnspond à park.getName()
                // jsonPath("$.name") = la valeur attendu
    }
}

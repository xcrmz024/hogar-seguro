package com.hogar.seguro.integration;


import com.hogar.seguro.model.Resident;
import com.hogar.seguro.model.enums.HelpType;
import com.hogar.seguro.repository.ApplicationRepository;
import com.hogar.seguro.repository.ResidentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Test - Application & Relationships (resident)")
public class ApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;


    @Test
    @DisplayName("Flow: Create application for existing resident -> Verify relationship in DB")
    void shouldLinkApplicationToResidentCorrectly() throws Exception {
       //arrange
        Resident resident = new Resident();
        resident.setName("Gatricia");
        resident.setSpecies("Gato");
        resident.setStory("Gatricia fue rescatada de un refugio en malas condiciones.");
        resident.setPhotoUrl("imagenes/gatricia.jpg");
        resident.setAvailable(true);
        resident.setHelpType(HelpType.ADOPTAR);
        resident.setActive(true);
        Resident savedResident = residentRepository.save(resident);

        // act
            //WebController - @PostMapping("/solicitud")- processApplication():
        mockMvc.perform(post("/solicitud")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Carlos")
                        .param("email", "carlos@mail.com")
                        .param("phoneNumber", "12345678")
                        .param("message", "Quisiera adoptar a Gatricia")
                        .param("applicationType", "ADOPTAR")
                        .param("residentId", savedResident.getId().toString()))
                .andExpect(status().isFound())//302 status code redirect
                .andExpect(redirectedUrl("/gracias?type=solicitud"));

        //Assert:
        var applications = applicationRepository.findAll();
        assertEquals(1, applications.size());
        assertNotNull(applications.get(0).getResident());
        assertEquals(savedResident.getId(), applications.get(0).getResident().getId());
    }


    @Test
    @DisplayName("Error flow: Error handling when resident does not exist (GlobalExceptionHandler Test)")
    void shouldHandleErrorWhenResidentNotFound() throws Exception {
            //WebController - @PostMapping("/solicitud")- processApplication():
        mockMvc.perform(post("/solicitud")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Infiltrado")
                        .param("email", "test@mail.com")
                        .param("phoneNumber", "000000")
                        .param("message", "Error")
                        .param("applicationType", "ADOPTAR")
                        .param("residentId", "999"))
                .andExpect(status().isNotFound()) // 404 status code
                .andExpect(view().name("error/404"));
    }

}



package com.hogar.seguro.controller;

import com.hogar.seguro.dto.ApplicationDto;
import com.hogar.seguro.dto.ResidentDto;
import com.hogar.seguro.security.JwtService;
import com.hogar.seguro.service.ApplicationService;
import com.hogar.seguro.service.DonationService;
import com.hogar.seguro.service.ResidentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@WebMvcTest(WebController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("WebController - Application Flow Tests")
public class WebApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private ResidentService residentService;

    @MockBean
    private DonationService donationService;


// ========================================================================
// 1. Application Form - @GetMapping("/solicitud") - showApplicationForm()
// ========================================================================

    //ADOPTAR case - (residentId != null)
    @Test
    @DisplayName("Should pre-fill form with resident info when residentId is provided")
    void shouldShowFormWithResidentInfo() throws Exception {
        // Arrange
        Long residentId = 1L;
        ResidentDto mockResident = new ResidentDto();
        mockResident.setName("Firulais");

        when(residentService.getResidentDtoById(residentId)).thenReturn(mockResident);

        //Act & Assert
        mockMvc.perform(get("/solicitud")
                        .param("residentId", residentId.toString())
                        .param("type", "ADOPTAR"))
                .andExpect(status().isOk())
                .andExpect(view().name("solicitud"))
                .andExpect(model().attribute("residentName", "Firulais"))
                .andExpect(model().attributeExists("applicationDto"));

        verify(residentService).getResidentDtoById(residentId);
    }


    //VOLUNTARIADO case - (residentId == null)
    @Test
    @DisplayName("Should show volunteering form without resident info")
    void shouldShowVolunteeringForm() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/solicitud")
                        .param("type", "VOLUNTARIADO"))
                .andExpect(status().isOk())
                .andExpect(view().name("solicitud"))
                .andExpect(model().attributeDoesNotExist("residentName"))
                .andExpect(model().attributeExists("applicationDto"));

        verifyNoInteractions(residentService);
    }


// ========================================================================
// 2. Process Application -  @PostMapping("/solicitud") - processApplication()
// ========================================================================

    //successful Post: - (redirect to gracias.html)
    @Test
    @DisplayName("Should redirect to thanks page when application is valid")
    void shouldProcessValidApplication() throws Exception {
        mockMvc.perform(post("/solicitud")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Ana Lopez")
                        .param("email", "ana@mail.com")
                        .param("phoneNumber", "99887766")
                        .param("message", "Me encantaría adoptar")
                        .param("applicationType", "ADOPTAR"))
                .andExpect(status().isFound())// 302 status code - redirect
                .andExpect(redirectedUrl("/gracias?type=solicitud"));

        verify(applicationService, times(1)).saveApplication(any(ApplicationDto.class));
    }


    //invalid Post: (validation error -> show solicitud.html)
    @Test
    @DisplayName("Should return to application form when validation fails")
    void shouldReturnFormOnErrors() throws Exception {
        mockMvc.perform(post("/solicitud")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .param("email", "not-an-email")
                        .param("applicationType", "ADOPTAR"))
                .andExpect(status().isOk())//ok - solicitud.html
                .andExpect(view().name("solicitud"))
                .andExpect(model().hasErrors());

        verifyNoInteractions(applicationService);
    }

}

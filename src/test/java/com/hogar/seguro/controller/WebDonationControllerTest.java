package com.hogar.seguro.controller;

import com.hogar.seguro.dto.DonationDto;
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
@DisplayName("WebController - Donation Flow Tests")
public class WebDonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private DonationService donationService;

    @MockBean
    private ResidentService residentService;
    @MockBean
    private ApplicationService applicationService;


// ========================================================================
// 1. Donation Form - @GetMapping("/donar") - showDonationForm()
// ========================================================================

    @Test
    @DisplayName("Should return donation form with empty DTO")
    void shouldShowDonationForm() throws Exception {
        mockMvc.perform(get("/donar"))
                .andExpect(status().isOk())
                .andExpect(view().name("donar"))
                .andExpect(model().attributeExists("donationDto"));
    }


// ========================================================================
// 2. Process Donation -  @PostMapping("/donar") - processDonation()
// ========================================================================

    //successful post - (redirect to gracias.html)
    @Test
    @DisplayName("Should redirect to thanks page when donation is valid")
    void shouldProcessValidDonation() throws Exception {
        mockMvc.perform(post("/donar")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                         //from DonationDto:
                        .param("name", "Juan Perez")
                        .param("email", "juan@mail.com")
                        .param("amount", "100.00")
                        .param("message", "Mucha suerte"))
                .andExpect(status().isFound())// 302 status code - redirect
                .andExpect(redirectedUrl("/gracias?type=donacion"));

        verify(donationService, times(1)).saveDonation(any(DonationDto.class));
    }


    //invalid post - (validation error -> show donar.html)
    @Test
    @DisplayName("Should stay on form page when validation fails")
    void shouldReturnFormOnValidationErrors() throws Exception {
        mockMvc.perform(post("/donar")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .param("email", "email-invalido")
                        .param("amount", "0.50"))//no min 1.00 -> will fail
                .andExpect(status().isOk())
                .andExpect(view().name("donar"))
                .andExpect(model().hasErrors());

        verifyNoInteractions(donationService);
    }


// ========================================================================
// 3. Thanks Page - @GetMapping("/gracias") - showThanks()
// ========================================================================

    //- GET (/gracias?type=donacion)
    @Test
    @DisplayName("Should show thanks page with correct action type")
    void shouldShowThanksPage() throws Exception {
        mockMvc.perform(get("/gracias").param("type", "donacion"))
                .andExpect(status().isOk())
                .andExpect(view().name("gracias"))
                .andExpect(model().attribute("actionType", "donacion"));//<div th:if="${actionType == 'donacion'}"> in thymeleaf html


    }


}

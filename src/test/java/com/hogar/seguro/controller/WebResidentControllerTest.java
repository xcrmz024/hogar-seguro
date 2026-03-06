package com.hogar.seguro.controller;

import com.hogar.seguro.dto.ResidentDto;
import com.hogar.seguro.model.enums.HelpType;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(WebController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("WebController - Home & Resident Flow Tests")
public class WebResidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ResidentService residentService;

    @MockBean
    private DonationService donationService;

    @MockBean
    private ApplicationService applicationService;


// ========================================================================
// 1. Home Page Test-  @GetMapping("/") - home()
// ========================================================================

    @Test
    @DisplayName("Should return index view with total donations collected")
    void shouldReturnHomeView() throws Exception {
        // Arrange
        BigDecimal total = new BigDecimal("5000.00");
        when(donationService.getTotalDonations()).thenReturn(total);

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk()) //200 status code
                .andExpect(view().name("index"))
                .andExpect(model().attribute("totalCollected", total));
    }


// ========================================================================
// 2. Residents List Test-  @GetMapping("/habitantes") -  showResidents()
// ========================================================================

    @Test
    @DisplayName("Should return habitantes view with a list of residents")
    void shouldShowResidentsList() throws Exception {
        // Arrange
        ResidentDto r1 = new ResidentDto();
        r1.setName("Rex");
        r1.setSpecies("Perro");
        r1.setStory("Rescatado de la calle.");
        r1.setPhotoUrl("imagenes/rex.jpg");
        r1.setAvailable(true);
        r1.setHelpType(HelpType.ADOPTAR);

        List<ResidentDto> mockList = List.of(r1);

        when(residentService.getAll()).thenReturn(mockList);

        // Act & Assert
                //"ACT:"
        mockMvc.perform(get("/habitantes"))
                //"ASSERT":
                .andExpect(status().isOk())
                .andExpect(view().name("habitantes"))
                .andExpect(model().attributeExists("residents"))
                .andExpect(model().attribute("residents", mockList));
    }


// ========================================================================
// 3. Static html Page Test - @GetMapping("/como-ayudar") - showHowToHelp()
// ========================================================================

    @Test
    @DisplayName("Should return static view como-ayudar")
    void shouldReturnHowToHelpView() throws Exception {
        mockMvc.perform(get("/como-ayudar"))
                .andExpect(status().isOk())
                .andExpect(view().name("como-ayudar"));
    }



}
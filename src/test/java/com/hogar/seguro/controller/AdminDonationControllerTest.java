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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController - Donation Management Tests")
public class AdminDonationControllerTest {

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

    @Test
    @DisplayName("Should list all donations and show total accumulated")
    void shouldListDonationsAndTotal() throws Exception {
        //Arrange
        BigDecimal total = new BigDecimal("1500.50");
        when(donationService.getAll()).thenReturn(List.of(new DonationDto()));
        when(donationService.getTotalDonations()).thenReturn(total);

        //Act & Assert
        mockMvc.perform(get("/admin/donaciones"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-donaciones"))
                .andExpect(model().attributeExists("donations"))
                .andExpect(model().attribute("totalAccumulated", total));

        verify(donationService).getAll();
        verify(donationService).getTotalDonations();
    }
}






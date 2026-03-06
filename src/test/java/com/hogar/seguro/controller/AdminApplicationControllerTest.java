package com.hogar.seguro.controller;

import com.hogar.seguro.dto.ApplicationDto;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController - Application Management Tests")
public class AdminApplicationControllerTest {

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
// 1. Admin Applications List Test-   @GetMapping("/solicitudes") -  listApplications()
// ========================================================================

    @Test
    @DisplayName("Should list all applications for the admin")
    void shouldListApplications() throws Exception {
        //Arrange
        when(applicationService.getAll()).thenReturn(List.of(new ApplicationDto(), new ApplicationDto()));

        //Act & Assert
        mockMvc.perform(get("/admin/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-solicitudes"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(model().attribute("applications", hasSize(2)));

        verify(applicationService, times(1)).getAll();
    }

}

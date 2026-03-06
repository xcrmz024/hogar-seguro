package com.hogar.seguro.controller;

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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController - Resident Management Tests")
public class AdminResidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ResidentService residentService;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private DonationService donationService;


// ========================================================================
// 1. Admin Home -   @GetMapping({"", "/"}) - adminHome()
// ========================================================================

    @Test
    @DisplayName("Should show admin dashboard with correct total stats")
    void shouldShowAdminHome() throws Exception {
        when(residentService.getAll()).thenReturn(List.of(new ResidentDto(), new ResidentDto()));

        mockMvc.perform(get("/admin/"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-index"))
                .andExpect(model().attribute("totalResidents", 2));
    }


// ========================================================================
// 2. Admin Residents List Test-   @GetMapping("/habitantes") -  listResidents()
// ========================================================================

    @Test
    @DisplayName("Should list all residents for the admin")
    void shouldListResidents() throws Exception {
        mockMvc.perform(get("/admin/habitantes"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-habitantes"))
                .andExpect(model().attributeExists("residents"));
    }


// ========================================================================
// 3. Form Logic (Create & Update) CRUD tests:
// ========================================================================

    //CREATE -> @GetMapping("/habitantes/nuevo") - showNewResidentForm()
    @Test
    @DisplayName("Should show empty form for new resident")
    void shouldShowNewResidentForm() throws Exception {
        mockMvc.perform(get("/admin/habitantes/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-formulario"))//HTML esperado
                .andExpect(model().attributeExists("residentDto"))
                .andExpect(model().attribute("pageTitle", "Registrar Nuevo Habitante"));
    }


    //UPDTAE -> @GetMapping ("/habitantes/editar/{id}") - showEditResidentForm()
    @Test
    @DisplayName("Should show pre-filled form for update resident")
    void shouldShowEditResidentForm() throws Exception {
        Long id = 1L;
        ResidentDto dto = new ResidentDto();
        dto.setId(id);
        dto.setName("Julio");

        when(residentService.getResidentDtoById(id)).thenReturn(dto);

        mockMvc.perform(get("/admin/habitantes/editar/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-formulario"))
                .andExpect(model().attribute("residentDto", dto))
                .andExpect(model().attribute("pageTitle", "Editar Habitante"));
    }


// ========================================================================
// 3. Form Logic (Save(create) & Delete)  CRUD tests:
// ========================================================================

    //SAVE: (create)- @PostMapping("/habitantes/guardar") -> saveResident()
    @Test
    @DisplayName("Should redirect after successful save")
    void shouldSaveResidentSuccessfully() throws Exception {
        mockMvc.perform(post("/admin/habitantes/guardar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Max")
                        .param("species", "perro")
                        .param("story", "rescatado del camino")
                        .param("photoUrl", "imagenes/max.jpg")
                        .param("available", "true")
                        .param("helpType", "ADOPTAR")
                        .param("description", "Un buen chico"))
                .andExpect(status().isFound())// 302 status code - redirect
                .andExpect(redirectedUrl("/admin/habitantes"));

        verify(residentService).saveResident(any(ResidentDto.class));
    }


        //SAVE: invalid case (redirect to ->"admin/admin-formulario")
    @Test
    @DisplayName("Should return to form when validation fails in save")
    void shouldReturnFormOnSaveError() throws Exception {
        mockMvc.perform(post("/admin/habitantes/guardar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-formulario"))//redirect to form
                .andExpect(model().hasErrors())
                .andExpect(model().attribute("pageTitle", "Registrar nuevo habitante"));
    }



    //DELETE: @PostMapping("/habitantes/eliminar/{id}") -> deleteResident()
    @Test
    @DisplayName("Should redirect after deleting a resident")
    void shouldDeleteResident() throws Exception {
        Long id = 5L;

        mockMvc.perform(post("/admin/habitantes/eliminar/{id}", id)
                        .with(csrf()))
                .andExpect(status().isFound())// 302 status code - redirect
                .andExpect(redirectedUrl("/admin/habitantes"));

        verify(residentService).deleteResidentById(id);
    }


}

package com.hogar.seguro.controller;

import com.hogar.seguro.dto.ResidentDto;
import com.hogar.seguro.service.ApplicationService;
import com.hogar.seguro.service.DonationService;
import com.hogar.seguro.service.ResidentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


/*
            Functionlaity	          |   File Name (.html)        |    URL Path
            ---------------------------------------------------------------------------------
            *Inicio del Panel	           admin-index.html	                /admin
                -Listar Habitantes	       admin-habitantes.html	        /admin/habitantes ....... /admin/habitantes/eliminar/{id}
                    +Formulario Nuevo	   admin-formulario.html	        /admin/habitantes/nuevo (empty form)
                    +Formulario Editar	   admin-formulario.html	        /admin/habitantes/editar/{id}
                                                                                      .../admin/habitantes/guardar (process: nuevo(create)/update)
                -Ver Solicitudes	       admin-solicitudes.html	        /admin/solicitudes
                -Ver Donaciones	           admin-donaciones.html	        /admin/donaciones

*/

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private final ResidentService residentService;
    private final ApplicationService applicationService;
    private final DonationService donationService;


    public AdminController(ResidentService residentService,
                           ApplicationService applicationService,
                           DonationService donationService) {
        this.residentService = residentService;
        this.applicationService = applicationService;
        this.donationService = donationService;
    }



    //--Admin Home page (admin panel)-------
    @GetMapping({"", "/"})
    public String adminHome(Model model) {
        model.addAttribute("totalResidents", residentService.getAll().size());
        return "admin/admin-index";
    }


// ========================================================================
// 1. ResidentService:
// ========================================================================
//------------------------------------------------------------------------------------------------
// RESIDENT MANAGEMENT: (admin-indext.html)-> (admin-habitantes.html)-> (admin-formulario.html)
//------------------------------------------------------------------------------------------------

    // list of registered residents (create/update/delete resident)
    @GetMapping("/habitantes")
    public String listResidents(Model model) {
        model.addAttribute("residents", residentService.getAll());
        return "admin/admin-habitantes";
    }


        //-------admin-formulario.html------------
        //CREATE FORM:
        @GetMapping("/habitantes/nuevo")
        public String showNewResidentForm(Model model) {
            model.addAttribute("residentDto", new ResidentDto());
            model.addAttribute("pageTitle", "Registrar Nuevo Habitante"); //dynamic string ("Registrar Nuevo Hbaitante"/ "Editar Hahbitante")
            return  "admin/admin-formulario";
        }


        //UPDATE FORM:
        @GetMapping ("/habitantes/editar/{id}")
        public String showEditResidentForm(@PathVariable("id")Long id, Model model) {
            ResidentDto residentDto = residentService.getResidentDtoById(id);
            model.addAttribute("residentDto", residentDto);
            model.addAttribute("pageTitle", "Editar Habitante");
            return "admin/admin-formulario";
        }


            //SAVE FORM (create/update)
        @PostMapping("/habitantes/guardar")
        public String saveResident(@Valid @ModelAttribute("residentDto") ResidentDto residentDto,
                                   BindingResult result,
                                   Model model) {
            if (result.hasErrors()) {
                model.addAttribute("pageTitle", residentDto.getId() == null ? "Registrar nuevo habitante" : "Editar Habitante");
                return "admin/admin-formulario";
            }

            residentService.saveResident(residentDto);
            return "redirect:/admin/habitantes"; //admin/habitantes.html
        }


    //DELETE:
    @PostMapping("/habitantes/eliminar/{id}")
    public String deleteResident(@PathVariable("id") Long id) {
        residentService.deleteResidentById(id);
        return "redirect:/admin/habitantes";
    }


// ========================================================================
// 2. ApplicaitonService:
// ========================================================================
//--------------------------------------------------------------
//VIEW REQUEST: (admin-index.html)-> (admin-solicitudes.html)
//--------------------------------------------------------------

    @GetMapping("/solicitudes")
    public String listApplications(Model model) {
        model.addAttribute("applications", applicationService.getAll());
        return "admin/admin-solicitudes";
    }


// ========================================================================
// 3. DontationService:
// ========================================================================
//-------------------------------------------------------------
//VIEW HISTORY: (admin-index.html)-> (admin-donaciones.html)
//-------------------------------------------------------------

    @GetMapping("/donaciones")
    public String listDonations(Model model) {
        model.addAttribute("donations", donationService.getAll());
        model.addAttribute("totalAccumulated", donationService.getTotalDonations());
        return "admin/admin-donaciones";
    }

}




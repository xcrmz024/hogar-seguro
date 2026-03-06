package com.hogar.seguro.controller;


import com.hogar.seguro.dto.ApplicationDto;
import com.hogar.seguro.dto.DonationDto;
import com.hogar.seguro.dto.ResidentDto;
import com.hogar.seguro.model.enums.ApplicationType;
import com.hogar.seguro.service.ApplicationService;
import com.hogar.seguro.service.DonationService;
import com.hogar.seguro.service.ResidentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;


@Controller
public class WebController {

    private final ResidentService residentService;
    private final DonationService donationService;
    private final ApplicationService applicationService;

    public WebController(ResidentService residentService, DonationService donationService,
                         ApplicationService applicationService) {
        this.residentService = residentService;
        this.donationService = donationService;
        this.applicationService = applicationService;
    }


    //------Home page------------
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalCollected", donationService.getTotalDonations());
        return "index";
    }


// ========================================================================
// 1. ResidentService:
// ========================================================================

    //-------Habitantes page-----------
    @GetMapping("/habitantes")
    public String showResidents(Model model) {
        List<ResidentDto> residentsList = residentService.getAll();
        model.addAttribute("residents", residentsList);//${residents}, residentList obj in thymeleaf
        return "habitantes";
    }

    //------Como ayudar page--------
    @GetMapping("/como-ayudar")
    public String showHowToHelp() {
        return "como-ayudar";
    }


// ========================================================================
// 2. DontationService:
// ========================================================================

    //-----Donation portal-------

    @GetMapping("/donar")
    public String showDonationForm(Model model) {
        model.addAttribute("donationDto", new DonationDto());//<form th:action="@{/donar}" th:object="${donationDto}" method="post"> in thymeleaf
        return "donar";
    }


    @PostMapping("/donar")
    public String processDonation(@Valid @ModelAttribute("donationDto") DonationDto dto,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "donar";
        }
        donationService.saveDonation(dto);
        return "redirect:/gracias?type=donacion";//Dynamic page (gracias.html)- QueryParam (type=donacion)
                                                //<div th:if="${actionType == 'donacion'}"> in thymeleaf html
    }



        //- GET (/gracias?type=donacion)
        //- GET (/gracias?type=solicitud)
    @GetMapping("/gracias")
    public String showThanks(@RequestParam(name = "type", required = false) String type, Model model) {
        model.addAttribute("actionType", type);
        return "gracias";//Specific view in gracias.html for each query parameter ("if/unless" logic in Thymeleaf):
                                //<div th:if="${actionType == 'donacion'}"> -> custom message for donation action
                                // <div th:unless="${actionType == 'donacion'}"> -> custom messager for solicitud action
    }


// ========================================================================
// 3. ApplicationService:
// ========================================================================

    //Application Form (amadrinar/adoptar/voluntariado):
    @GetMapping("/solicitud")
    public String showApplicationForm(@RequestParam(name = "residentId", required = false) Long residentId,
                                      @RequestParam(name = "type")ApplicationType type,
                                      Model model) {

        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setApplicationType(type);

               //helpType = ADOPTAR/AMADRINAR case (required residentId):
        if (residentId != null) {
            applicationDto.setResidentId(residentId);
            ResidentDto residentDto = residentService.getResidentDtoById(residentId);
            model.addAttribute("residentName", residentDto.getName());//for custom resident name in view
        }

        model.addAttribute("applicationDto", applicationDto);

        return "solicitud"; //With fields displayed based on thymeleaf logic and param:
                            //ADOPTAR = solicitud?residentId={id}&type=ADOPTAR
                            //AMADRINAR = /solicitud?residentId={id}&type=AMADRINAR
                            //VOLUNTARIADO = /solicitud?type=VOLUNTARIADO
    }


    @PostMapping("/solicitud")
    public String processApplication(@Valid @ModelAttribute("applicationDto") ApplicationDto dto,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors()) {
            return "solicitud";
        }
        applicationService.saveApplication(dto);
        return "redirect:/gracias?type=solicitud";//<div th:unless="${actionType == 'donacion'}"> thymeleaf logic (gracias.html)
    }

}



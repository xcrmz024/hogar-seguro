package com.hogar.seguro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


//403 Error controller
@Controller
public class CustomErrorController {

    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";//403.html
    }

}

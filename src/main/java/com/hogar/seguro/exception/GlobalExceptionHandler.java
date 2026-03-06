package com.hogar.seguro.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    //ex. ResidentId not found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404 status code
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404"; //404.html
    }


    //server error
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//500 status code
    public String handleGlobalException(Exception ex, Model model) {
        logger.error("Error interno del servidor: ", ex);

        model.addAttribute("errorMessage", "Lo sentimos, ha ocurrido un " +
                "error inesperado en nuestro sistema. Por favor, inténtalo más tarde.");

        return "error/500";//500.html
    }


}

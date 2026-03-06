package com.hogar.seguro.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//Authentication Dto
public class LoginDto {

    public LoginDto() {};

    @NotBlank(message = "El nombre del usuario es requerido")
    @Size(max = 50)
    private String username;

    @NotBlank(message = "La contraseña es requerida")
    @Size(max = 255)
    private String password;


//----Getters & Setters:
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}







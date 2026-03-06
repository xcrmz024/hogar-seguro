package com.hogar.seguro.controller;

import com.hogar.seguro.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//AuthControllerTest (login validation)
@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController - Authentication Flow Tests")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

// ========================================================================
// 1. Login View - @GetMapping("/login") - login()
// ========================================================================

    @Test
    @DisplayName("Should return the login view")
    public void shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

// ========================================================================
// 2. Authentication Process -  @PostMapping("/auth/login") - authenticate()
// ========================================================================

    //successful login: - (redirect to /admin)
    @Test
    @DisplayName("Should set JWT cookie and redirect to admin on successful login")
    void shouldLoginSuccessfully() throws Exception {
        // arrange
        String username = "admin";
        String token = "mocked-jwt-token";

        Authentication mockAuth = mock(Authentication.class);
        UserDetails mockUser = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn(token);

        // act & assert
        mockMvc.perform(post("/auth/login")

                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", username)
                        .param("password", "12345"))
                .andExpect(status().isFound())// 302 status code - redirect
                .andExpect(redirectedUrl("/admin"))
                .andExpect(cookie().exists("jwt"))
                .andExpect(cookie().value("jwt", token))
                .andExpect(cookie().httpOnly("jwt", true));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(mockUser);

    }


    //invalid login: - (redirect to "/login?error=true")
    @Test
    @DisplayName("Should redirect to login with error param on failed authentication")
    void shouldRedirectOnError() throws Exception {
        // arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // act & asssert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "wrong-user")
                        .param("password", "wrong-pass"))
                .andExpect(status().isFound())// 302 status code - redirect
                .andExpect(redirectedUrl("/login?error=true"))
                .andExpect(cookie().doesNotExist("jwt"));

        verify(jwtService, never()).generateToken(any());
    }


}


package com.hogar.seguro.integration;


import com.hogar.seguro.repository.UserRepository;
import com.hogar.seguro.security.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Test - Security & JWT Flow")
public class SecurityIntegrationTest {

// ========================================================================
// SecurityConfig + JwtAuthenticationFilter
// ========================================================================

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setup() {
        //Create raal admin in h2 db:
        User admin = new User();
        admin.setUsername("admin_real");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);
    }


    @Test
    @DisplayName("Full Security Flow: Login -> Get Cookie -> Access Admin Page")
    void shouldLoginAndAccessAdminRoute() throws Exception {

       //Try to access to "/admin" without being logged in
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection());//redirect expected


        //Try real login:
        var result = mockMvc.perform(post("/auth/login")//AuthController.java -  @PostMapping("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "admin_real")
                        .param("password", "password123"))
                .andExpect(status().isFound())//status code 302 redirect
                .andExpect(redirectedUrl("/admin"))
                .andExpect(cookie().exists("jwt"))
                .andReturn();

        // Extract the cookie from the login result (result.getResponse())
        Cookie jwtCookie = result.getResponse().getCookie("jwt");

        // Access to "/admin" using the obtained cookie
        mockMvc.perform(get("/admin")//-> AdminController - @GetMapping({"", "/"}) - adminHome()
                        .cookie(jwtCookie)) //send real generated cookie
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-index"));
    }


    @Test
    @DisplayName("Should reject access with invalid password")
    void shouldRejectInvalidLogin() throws Exception {
        //authentication flow:
        mockMvc.perform(post("/auth/login")//AuthController.java -  @PostMapping("/auth/login")
                        .with(csrf())
                        .param("username", "admin_real")
                        .param("password", "wrong_pass"))
                .andExpect(status().isFound())//302 status code redirect
                .andExpect(redirectedUrl("/login?error=true"))
                .andExpect(cookie().doesNotExist("jwt"));
    }

}




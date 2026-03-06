package com.hogar.seguro.integration;

import com.hogar.seguro.repository.DonationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Test - Donation Flow")
public class DonationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DonationRepository donationRepository;


    //WebController
    @Test
    @DisplayName("Donation flow: Submit donation form -> Verify record in DB -> Verify sum update")
    void shouldProcessFullDonationFlow() throws Exception {
        //Verify initial state of db:
        BigDecimal initialTotal = donationRepository.sumAllDonations();
        if (initialTotal == null) initialTotal = BigDecimal.ZERO;

        //act
        mockMvc.perform(post("/donar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Donante Integración")
                        .param("email", "test@integration.com")
                        .param("amount", "150.00")
                        .param("message", "Donación real de prueba"))
                .andExpect(status().isFound())// 302 satus code redirect
                .andExpect(redirectedUrl("/gracias?type=donacion"));

        //assert (verify exists in H2 db)
        assertEquals(1, donationRepository.count(), "Should have 1 record in the database");

        //assert (verify that the total sum was updated correctly)
        BigDecimal finalTotal = donationRepository.sumAllDonations();
        assertEquals(initialTotal.add(new BigDecimal("150.00")), finalTotal, "Total sum in DB should be updated");
    }

}




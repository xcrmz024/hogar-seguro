package com.hogar.seguro.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
public class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("fake_secret_key_for_test_12345678900");
        when(mockUserDetails.getUsername()).thenReturn("admin_test");
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());
    }

// ========================================================================
// 1. Token Lifecycle - Generation(generateToken()) & Extraction(extractUsername())
// ========================================================================

    //verify extraction of claims
    @Test
    @DisplayName("Should generate a valid JWT and extract the correct username")
    void shouldGenerateAndExtractUsername() {
        //ACTS:

            //generate token
        String token = jwtService.generateToken(mockUserDetails);

            //extract username from token
        String extractedUsername = jwtService.extractUsername(token);

        //Assert
        assertNotNull(token, "Generated token should not be null");
        assertEquals("admin_test", extractedUsername, "Extracted username should match the subject");
    }


// ========================================================================
// 2. Validation Logic - isTokenValid()
// ========================================================================

    //valid username case:
    @Test
    @DisplayName("Should validate a correct token for the right user")
    void shouldValidateCorrectToken() {
        // Arrange
            //generate token
        String token = jwtService.generateToken(mockUserDetails);

        //Act
        boolean isValid = jwtService.isTokenValid(token, mockUserDetails);

        // Assert
        assertTrue(isValid, "Token should be valid for the user who generated it");
    }


    //invalid username case:
    @Test
    @DisplayName("Should fail validation when username does not match")//when username form claim token != username from UserDetails
    void shouldFailWhenUsernameDoesNotMatch() {
        //Arrange
            //generate token
        String token = jwtService.generateToken(mockUserDetails);

            //generate manual mock (UserDetails)
        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("wrong_user");

        //Act
        boolean isValid = jwtService.isTokenValid(token, otherUser);

        //Assert
        assertFalse(isValid, "Token should be invalid for a different user");
    }


// ========================================================================
// 3. Claims:
// ========================================================================

    @Test
    @DisplayName("Should extract specific claims correctly")
    void shouldExtractSpecificClaims() {
        // Arrange
            //generate token
        String token = jwtService.generateToken(mockUserDetails);

        //Act:
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        // Assert
        assertNotNull(issuedAt, "Should be able to extract IssuedAt claim");
        assertTrue(issuedAt.before(new Date(System.currentTimeMillis() + 1000)));
    }

}



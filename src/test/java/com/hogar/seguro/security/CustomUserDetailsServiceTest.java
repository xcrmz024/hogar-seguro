package com.hogar.seguro.security;

import com.hogar.seguro.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;


// ========================================================================
// 1. loadUserByUsername()
// ========================================================================

    //username found:
    @Test
    @DisplayName("Should return a valid UserDetails when username exists")
    void shouldReturnUserDetailsWhenUserExists() {
        // arrange
        String username = "admin";

            //"existing user":
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword");
        mockUser.setRole("ROLE_ADMIN");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // assert
        assertAll("Verification of UserDetails retrieval",
                () -> assertNotNull(userDetails, "The returned UserDetails should not be null"),
                () -> assertEquals(username, userDetails.getUsername(), "Usernames do not match"),
                () -> assertEquals(mockUser.getPassword(), userDetails.getPassword(), "Passwords do not match"),
                () -> assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")), "Should contain ROLE_ADMIN authority")
        );

        verify(userRepository).findByUsername(username);
    }


    //username not found (exception):
    @Test
    @DisplayName("Should throw UsernameNotFoundException when username does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // arrange
        String invalidUsername = "nonexistent_user";
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        //act & assert
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(invalidUsername);
        }, "Expected UsernameNotFoundException to be thrown");

        //extra assert: (verify message (from exception))
        assertEquals("User not found: " + invalidUsername, ex.getMessage());
        verify(userRepository).findByUsername(invalidUsername);
    }


}

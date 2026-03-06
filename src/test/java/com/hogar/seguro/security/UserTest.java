package com.hogar.seguro.security;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("User test")
public class UserTest {

// ========================================================================
// 1. Security Contract - isEnabled, isAccountNonLocked, etc.
// ========================================================================

    @Test
    @DisplayName("Should verify that UserDetails methods return true by default")
    void shouldVerifyDefaultUserDetailsSecurityStatus() {
        // arrange
        User user = new User();

        //assert
        assertAll("Verification of security account status",
                () -> assertTrue(user.isEnabled(), "Account should be enabled"),
                () -> assertTrue(user.isAccountNonExpired(), "Account should not be expired"),
                () -> assertTrue(user.isAccountNonLocked(), "Account should not be locked"),
                () -> assertTrue(user.isCredentialsNonExpired(), "Credentials should not be expired")
        );
    }


}

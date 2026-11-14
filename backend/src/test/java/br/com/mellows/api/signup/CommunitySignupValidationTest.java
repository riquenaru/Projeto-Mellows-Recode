package br.com.mellows.api.signup;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class CommunitySignupValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsValidSignupWithConsent() {
        CommunitySignupRequest request = new CommunitySignupRequest("Leitora Mellows", "reader@example.com", true);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsInvalidEmailAndMissingConsent() {
        CommunitySignupRequest request = new CommunitySignupRequest("Leitora Mellows", "email-invalido", false);

        assertFalse(validator.validate(request).isEmpty());
    }
}

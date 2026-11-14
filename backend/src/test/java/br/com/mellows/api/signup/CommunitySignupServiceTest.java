package br.com.mellows.api.signup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommunitySignupServiceTest {
    @Mock
    private CommunitySignupRepository repository;

    @InjectMocks
    private CommunitySignupService service;

    @Test
    void trimsNameAndNormalizesEmailBeforeSaving() {
        CommunitySignupRequest request = new CommunitySignupRequest("  Ana Leitora  ", " ANA@EXAMPLE.COM ", true);
        when(repository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);

        service.create(request);

        ArgumentCaptor<CommunitySignup> signup = ArgumentCaptor.forClass(CommunitySignup.class);
        verify(repository).save(signup.capture());
        assertEquals("Ana Leitora", signup.getValue().getName());
        assertEquals("ana@example.com", signup.getValue().getEmail());
        verify(repository).existsByEmailIgnoreCase("ana@example.com");
    }

    @Test
    void rejectsDuplicateEmailWithoutSaving() {
        CommunitySignupRequest request = new CommunitySignupRequest("Ana Leitora", "ana@example.com", true);
        when(repository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);

        assertThrows(DuplicateSignupException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }
}

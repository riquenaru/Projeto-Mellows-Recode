package br.com.mellows.api.signup;

import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunitySignupService {
    private final CommunitySignupRepository repository;

    public CommunitySignupService(CommunitySignupRepository repository) { this.repository = repository; }

    @Transactional
    public CommunitySignupResponse create(CommunitySignupRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (repository.existsByEmailIgnoreCase(email)) throw new DuplicateSignupException();
        CommunitySignup signup = repository.save(new CommunitySignup(request.name().trim(), email, request.consent()));
        return CommunitySignupResponse.from(signup);
    }
}

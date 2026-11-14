package br.com.mellows.api.signup;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunitySignupRepository extends JpaRepository<CommunitySignup, Long> {
    boolean existsByEmailIgnoreCase(String email);
}

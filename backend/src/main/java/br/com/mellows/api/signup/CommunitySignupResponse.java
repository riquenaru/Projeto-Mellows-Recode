package br.com.mellows.api.signup;

import java.time.Instant;

public record CommunitySignupResponse(Long id, Instant createdAt) {
    static CommunitySignupResponse from(CommunitySignup signup) {
        return new CommunitySignupResponse(signup.getId(), signup.getCreatedAt());
    }
}

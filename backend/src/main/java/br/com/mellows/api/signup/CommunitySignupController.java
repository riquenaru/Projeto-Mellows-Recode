package br.com.mellows.api.signup;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community-signups")
public class CommunitySignupController {
    private final CommunitySignupService service;

    public CommunitySignupController(CommunitySignupService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CommunitySignupResponse> create(@Valid @RequestBody CommunitySignupRequest request) {
        CommunitySignupResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

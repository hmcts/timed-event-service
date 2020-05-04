package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.springframework.http.ResponseEntity.ok;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemTokenGenerator;

@RestController
public class TestingSupportController {

    private final SystemTokenGenerator systemTokenGenerator;

    public TestingSupportController(SystemTokenGenerator systemTokenGenerator) {
        this.systemTokenGenerator = systemTokenGenerator;
    }

    @GetMapping("/testing-support/token")
    public ResponseEntity<String> token() {
        return ok(systemTokenGenerator.generate());
    }

}

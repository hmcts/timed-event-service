package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.springframework.http.ResponseEntity.ok;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.TimedEvent;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.CcdEventAuthorizor;

@RestController
public class TimedEventController {

    private CcdEventAuthorizor ccdEventAuthorizor;

    public TimedEventController(CcdEventAuthorizor ccdEventAuthorizor) {
        this.ccdEventAuthorizor = ccdEventAuthorizor;
    }

    @PostMapping("/timed-event")
    public ResponseEntity<String> timedEvent(@RequestBody TimedEvent timedEvent) {

        ccdEventAuthorizor.throwIfNotAuthorized(timedEvent.getEvent());

        return ok("Welcome to timed-event controller");
    }
}

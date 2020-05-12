package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.springframework.http.ResponseEntity.*;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.timedevent.domain.entities.EventExecution;
import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.Event;
import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.EventNotFoundException;
import uk.gov.hmcts.reform.timedevent.domain.services.EventExecutor;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemTokenGenerator;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemUserProvider;

@Slf4j
@RestController
public class TestingSupportController {

    private final SystemTokenGenerator systemTokenGenerator;

    private final SystemUserProvider systemUserProvider;

    private final EventExecutor eventExecutor;

    public TestingSupportController(
        SystemTokenGenerator systemTokenGenerator,
        SystemUserProvider systemUserProvider,
        EventExecutor eventExecutor) {

        this.systemTokenGenerator = systemTokenGenerator;
        this.systemUserProvider = systemUserProvider;
        this.eventExecutor = eventExecutor;
    }

    @GetMapping("/testing-support/token")
    public ResponseEntity<String> token() {

        return ok(systemTokenGenerator.generate());
    }

    @GetMapping("/testing-support/system-user")
    public ResponseEntity<String> systemUser() {

        return ok(systemUserProvider.getSystemUserId("Bearer " + systemTokenGenerator.generate()));
    }

    @PostMapping("/testing-support/execute/jurisdiction/{jurisdiction}/case-type/{caseType}/cid/{cid}/event/{event}")
    public ResponseEntity<String> execute(
        @PathVariable("jurisdiction") String jurisdiction,
        @PathVariable("caseType") String caseType,
        @PathVariable("cid") long id,
        @PathVariable("event") String event
    ) {

        try {

            eventExecutor.execute(
                new EventExecution(
                    Event.fromString(event),
                    jurisdiction,
                    caseType,
                    id
                )
            );

            return ok("event: " + event + ", executed for id: " + id);

        } catch (EventNotFoundException e) {
            log.error(e.getMessage(), e);
            return badRequest().body(e.getMessage());

        } catch (FeignException e) {
            log.error(e.getMessage(), e);
            return status(e.status()).body(e.contentUTF8());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);

        }

    }
}

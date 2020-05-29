package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.springframework.http.ResponseEntity.*;

import feign.FeignException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation("Generating system user token")
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Generated system user token",
            response = String.class
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error"
        )
    })
    @GetMapping("/testing-support/token")
    public ResponseEntity<String> token() {

        return ok(systemTokenGenerator.generate());
    }

    @ApiOperation("Getting system user id")
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "system user id",
            response = String.class
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error"
        )
    })
    @GetMapping("/testing-support/system-user")
    public ResponseEntity<String> systemUser() {

        return ok(systemUserProvider.getSystemUserId("Bearer " + systemTokenGenerator.generate()));
    }

    @ApiOperation("Executing event in CCD")
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "confirmation message",
            response = String.class
        ),
        @ApiResponse(
            code = 403,
            message = "Forbidden / re-thrown from dependent service"
        ),
        @ApiResponse(
            code = 422,
            message = "Unprocessable Entity / re-thrown from dependent service"
        ),

        @ApiResponse(
            code = 400,
            message = "Bad Request / re-thrown from dependent service"
        ),
        @ApiResponse(
            code = 404,
            message = "Not Found / re-thrown from dependent service"
        ),
        @ApiResponse(
            code = 504,
            message = "Gateway Timeout / re-thrown from dependent service"
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error"
        )
    })
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

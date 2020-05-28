package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.springframework.http.ResponseEntity.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.timedevent.domain.entities.TimedEvent;
import uk.gov.hmcts.reform.timedevent.domain.services.SchedulerService;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.CcdEventAuthorizor;

@RestController
public class TimedEventController {

    private CcdEventAuthorizor ccdEventAuthorizor;
    private SchedulerService schedulerService;

    public TimedEventController(CcdEventAuthorizor ccdEventAuthorizor, SchedulerService timedEventService) {
        this.ccdEventAuthorizor = ccdEventAuthorizor;
        this.schedulerService = timedEventService;
    }

    @ApiOperation(
        value = "Scheduling / rescheduling timed event",
        authorizations =
        {
            @Authorization(value = "Authorization"),
            @Authorization(value = "ServiceAuthorization")
        }
    )
    @ApiResponses({
        @ApiResponse(
            code = 201,
            message = "Created TimeEvent object",
            response = TimedEvent.class
        ),
        @ApiResponse(
            code = 415,
            message = "Unsupported Media Type"
        ),
        @ApiResponse(
            code = 400,
            message = "Bad Request"
        ),
        @ApiResponse(
            code = 403,
            message = "Forbidden"
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error"
        )
    })
    @PostMapping("/timed-event")
    public ResponseEntity<TimedEvent> post(@RequestBody TimedEvent timedEvent) {

        if (!isValid(timedEvent)) {
            return badRequest().build();
        }

        ccdEventAuthorizor.throwIfNotAuthorized(timedEvent.getEvent());

        String identity = "";
        if (StringUtils.isBlank(timedEvent.getId())) {
            identity = schedulerService.schedule(timedEvent);
        } else {
            identity = schedulerService.reschedule(timedEvent, 0);
        }

        return status(HttpStatus.CREATED).body(
            new TimedEvent(
                identity,
                timedEvent.getEvent(),
                timedEvent.getScheduledDateTime(),
                timedEvent.getJurisdiction(),
                timedEvent.getCaseType(),
                timedEvent.getCaseId()
            )
        );
    }

    @ApiOperation(
        value = "Getting scheduled event",
        authorizations =
            {
                @Authorization(value = "Authorization"),
                @Authorization(value = "ServiceAuthorization")
            }
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "TimeEvent object",
            response = TimedEvent.class
        ),
        @ApiResponse(
            code = 404,
            message = "Not Found"
        ),
        @ApiResponse(
            code = 401,
            message = "Forbidden"
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error"
        )
    })
    @GetMapping("/timed-event/{id}")
    public ResponseEntity<TimedEvent> get(@PathVariable("id") String id) {

        return schedulerService
            .get(id)
            .map(ResponseEntity::ok)
            .orElse(notFound().build());
    }

    private boolean isValid(TimedEvent timedEvent) {
        return timedEvent != null
               && timedEvent.getEvent() != null
               && timedEvent.getScheduledDateTime() != null
               && timedEvent.getCaseId() != 0
               && Strings.isNotBlank(timedEvent.getJurisdiction())
               && Strings.isNotBlank(timedEvent.getCaseType());
    }
}

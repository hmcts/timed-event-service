package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.springframework.http.ResponseEntity.*;

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

    @GetMapping("/timed-event/{identity}")
    public ResponseEntity<TimedEvent> get(@PathVariable("identity") String identity) {

        return schedulerService
            .get(identity)
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

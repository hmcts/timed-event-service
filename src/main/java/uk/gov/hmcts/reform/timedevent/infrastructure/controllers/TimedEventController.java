package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.springframework.http.ResponseEntity.ok;

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

        ccdEventAuthorizor.throwIfNotAuthorized(timedEvent.getEvent());

        String identity = schedulerService.schedule(timedEvent);

        return ok(
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

        return ok(schedulerService.get(identity));
    }
}

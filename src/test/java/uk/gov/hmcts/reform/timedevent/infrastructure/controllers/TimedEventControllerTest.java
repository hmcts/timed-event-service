package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import uk.gov.hmcts.reform.timedevent.domain.entities.TimedEvent;
import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.Event;
import uk.gov.hmcts.reform.timedevent.domain.services.SchedulerService;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.CcdEventAuthorizor;

@ExtendWith(MockitoExtension.class)
class TimedEventControllerTest {

    @Mock
    private CcdEventAuthorizor ccdEventAuthorizor;

    @Mock
    private SchedulerService schedulerService;

    private TimedEventController timedEventController;

    private TimedEvent timedEvent = new TimedEvent(
        "",
        Event.UNKNOWN,
        ZonedDateTime.now(),
        "jurisdiction",
        "caseType",
        1234
    );

    private String identity = "someId";

    @Test
    void should_return_scheduled_timed_event() {

        when(schedulerService.schedule(timedEvent)).thenReturn(identity);

        timedEventController = new TimedEventController(ccdEventAuthorizor, schedulerService);

        ResponseEntity<TimedEvent> response = timedEventController.post(timedEvent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(timedEvent.getEvent(), response.getBody().getEvent());
        assertEquals(timedEvent.getScheduledDateTime(), response.getBody().getScheduledDateTime());
        assertEquals(timedEvent.getJurisdiction(), response.getBody().getJurisdiction());
        assertEquals(timedEvent.getCaseType(), response.getBody().getCaseType());
        assertEquals(timedEvent.getCaseId(), response.getBody().getCaseId());

        assertEquals(identity, response.getBody().getId());

        verify(ccdEventAuthorizor).throwIfNotAuthorized(timedEvent.getEvent());
        verify(schedulerService).schedule(timedEvent);

    }

    @Test
    void should_throw_access_denied_exception_when_ccd_event_authorizor_complains() {

        doThrow(new AccessDeniedException("Event 'unknown' not allowed")).when(ccdEventAuthorizor).throwIfNotAuthorized(Event.UNKNOWN);
        timedEventController = new TimedEventController(ccdEventAuthorizor, schedulerService);

        AccessDeniedException thrown = assertThrows(
            AccessDeniedException.class,
            () -> timedEventController.post(timedEvent)
        );
        assertEquals("Event 'unknown' not allowed", thrown.getMessage());

        verifyNoInteractions(schedulerService);
    }
}
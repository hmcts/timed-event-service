package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.Event;
import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.TimedEvent;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.CcdEventAuthorizor;

@ExtendWith(MockitoExtension.class)
class TimedEventControllerTest {

    @Mock
    private CcdEventAuthorizor ccdEventAuthorizor;

    private TimedEventController timedEventController;

    @Test
    void should_return_welcome_message() {

        timedEventController = new TimedEventController(ccdEventAuthorizor);

        TimedEvent timedEvent = new TimedEvent(Event.UNKNOWN);

        ResponseEntity<String> response = timedEventController.timedEvent(timedEvent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome to timed-event controller", response.getBody());
    }

    @Test
    void should_throw_access_denied_exception_when_ccd_event_authorizor_complains() {

        doThrow(new AccessDeniedException("Event 'unknown' not allowed")).when(ccdEventAuthorizor).throwIfNotAuthorized(Event.UNKNOWN);
        timedEventController = new TimedEventController(ccdEventAuthorizor);

        TimedEvent timedEvent = new TimedEvent(Event.UNKNOWN);

        AccessDeniedException thrown = assertThrows(
            AccessDeniedException.class,
            () -> timedEventController.timedEvent(timedEvent)
        );
        assertEquals("Event 'unknown' not allowed", thrown.getMessage());
    }
}
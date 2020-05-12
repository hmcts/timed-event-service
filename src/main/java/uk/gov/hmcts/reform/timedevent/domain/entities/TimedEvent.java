package uk.gov.hmcts.reform.timedevent.domain.entities;

import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.Event;

public class TimedEvent {

    private Event event;

    private TimedEvent() {
    }

    public TimedEvent(Event event) {

        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}

package uk.gov.hmcts.reform.timedevent.domain.services;

import uk.gov.hmcts.reform.timedevent.domain.entities.TimedEvent;

public interface SchedulerService {

    String schedule(TimedEvent timedEvent);

    TimedEvent get(String identity);
}
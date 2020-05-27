package uk.gov.hmcts.reform.timedevent.domain.services;

import java.util.Optional;
import uk.gov.hmcts.reform.timedevent.domain.entities.TimedEvent;

public interface SchedulerService {

    String schedule(TimedEvent timedEvent);

    Optional<TimedEvent> get(String identity);
}

package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import java.time.ZonedDateTime;

public interface DateTimeProvider {

    ZonedDateTime now();
}

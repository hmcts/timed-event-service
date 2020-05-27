package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

@Component
public class SystemDateTimeProvider implements DateTimeProvider {

    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }
}

package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class SystemDateTimeProviderTest {

    @Test
    public void should_return_current_zoned_date_time_in_utc() {

        ZonedDateTime expectedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime dateTime = new SystemDateTimeProvider().now();

        assertEquals(expectedDateTime.getZone(), dateTime.getZone());
    }
}
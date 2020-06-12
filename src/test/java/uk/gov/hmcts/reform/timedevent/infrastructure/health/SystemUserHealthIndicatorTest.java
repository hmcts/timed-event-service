package uk.gov.hmcts.reform.timedevent.infrastructure.health;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemTokenGenerator;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemUserProvider;

@ExtendWith(MockitoExtension.class)
class SystemUserHealthIndicatorTest {

    @Mock
    private SystemUserProvider systemUserProvider;

    @Mock
    private SystemTokenGenerator systemTokenGenerator;

    private SystemUserHealthIndicator systemUserHealthIndicator;

    private String someToken = "someToken";

    private String someUser = "someUser";

    @Test
    public void should_return_up_up_status_when_token_and_user_are_fetched() {
        when(systemTokenGenerator.generate()).thenReturn(someToken);
        when(systemUserProvider.getSystemUserId("Bearer " + someToken)).thenReturn(someUser);

        systemUserHealthIndicator = new SystemUserHealthIndicator(systemTokenGenerator, systemUserProvider);

        Health health = systemUserHealthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("UP", health.getDetails().get("userStatus"));
    }

    @Test
    public void should_return_up_user_blank_status_when_token_is_fetched_and_user_is_not() {
        when(systemTokenGenerator.generate()).thenReturn(someToken);
        when(systemUserProvider.getSystemUserId("Bearer " + someToken)).thenReturn(null);

        systemUserHealthIndicator = new SystemUserHealthIndicator(systemTokenGenerator, systemUserProvider);

        Health health = systemUserHealthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("user is blank", health.getDetails().get("userStatus"));
    }

    @Test
    public void should_return_up_token_blank_status_when_token_is_not_fetched() {
        when(systemTokenGenerator.generate()).thenReturn(null);

        systemUserHealthIndicator = new SystemUserHealthIndicator(systemTokenGenerator, systemUserProvider);

        Health health = systemUserHealthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("token is blank", health.getDetails().get("userStatus"));
    }

    @Test
    public void should_return_up_and_exception_message_when_generator_throws_exception() {
        String someExceptionMessage = "someMessage";
        when(systemTokenGenerator.generate()).thenThrow(new RuntimeException(someExceptionMessage));

        systemUserHealthIndicator = new SystemUserHealthIndicator(systemTokenGenerator, systemUserProvider);

        Health health = systemUserHealthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("cannot authenticate system user: " + someExceptionMessage, health.getDetails().get("userStatus"));
    }
}
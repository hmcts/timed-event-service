package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions.RetryableException;

class RetryableExceptionHandlerTest {

    @Test
    public void should_throw_retryable_exception_by_default() {

        assertThrows(
            RetryableException.class,
            () -> new RetryableExceptionHandler().wrapException(new RuntimeException())
        );
    }

    // TODO add business logic here
}
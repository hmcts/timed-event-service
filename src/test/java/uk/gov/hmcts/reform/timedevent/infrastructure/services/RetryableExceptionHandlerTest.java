package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions.NonRetryableException;
import uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions.RetryableException;

@ExtendWith(MockitoExtension.class)
class RetryableExceptionHandlerTest {

    @Mock
    private FeignException exception;

    @Test
    public void should_throw_retryable_exception_by_default() {

        assertThrows(
            RetryableException.class,
            () -> new RetryableExceptionHandler().wrapException(new RuntimeException())
        );
    }

    @Test
    public void should_throw_non_retryable_when_case_is_in_wrong_status() {

        when(exception.status()).thenReturn(422);
        when(exception.contentUTF8()).thenReturn("The case status did not qualify for the event");

        assertThrows(
            NonRetryableException.class,
            () -> new RetryableExceptionHandler().wrapException(exception)
        );
    }

    @Test
    public void should_throw_non_retryable_when_case_is_not_existing() {

        when(exception.status()).thenReturn(400);
        when(exception.contentUTF8()).thenReturn("Case reference is not valid");

        assertThrows(
            NonRetryableException.class,
            () -> new RetryableExceptionHandler().wrapException(exception)
        );
    }
}
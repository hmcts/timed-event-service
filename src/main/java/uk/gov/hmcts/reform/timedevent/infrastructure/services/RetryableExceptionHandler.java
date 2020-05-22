package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions.NonRetryableException;
import uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions.RetryableException;

@Component
public class RetryableExceptionHandler {

    public void wrapException(Exception ex) throws RetryableException, NonRetryableException {

        if (isExceptionRetryable(ex)) {
            throw new RetryableException();
        } else {
            throw new NonRetryableException();
        }
    }

    private boolean isExceptionRetryable(Exception ex) {

        // TODO implement logic for retries based on the exception type / message

        return false;
    }
}

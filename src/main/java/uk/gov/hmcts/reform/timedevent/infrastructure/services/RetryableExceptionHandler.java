package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions.NonRetryableException;
import uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions.RetryableException;

@Slf4j
@Component
public class RetryableExceptionHandler {

    public void wrapException(Exception ex) throws RetryableException, NonRetryableException {

        if (isExceptionNonRetryable(ex)) {

            log.error(ex.getMessage(), ex);
            throw new NonRetryableException();
        }

        log.info(ex.getMessage(), ex);
        // throw RetryableException by default - it is safe because we have limited number of retries
        throw new RetryableException();
    }

    private boolean isExceptionNonRetryable(Exception ex) {

        // TODO implement logic for retries based on the exception type / message

        return false;
    }
}

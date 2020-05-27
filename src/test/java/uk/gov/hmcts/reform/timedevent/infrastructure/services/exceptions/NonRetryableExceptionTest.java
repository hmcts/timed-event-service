package uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionException;

class NonRetryableExceptionTest {

    @Test
    public void should_wrap_job_execution_exception() {

        assertTrue(new NonRetryableException() instanceof JobExecutionException);
    }
}
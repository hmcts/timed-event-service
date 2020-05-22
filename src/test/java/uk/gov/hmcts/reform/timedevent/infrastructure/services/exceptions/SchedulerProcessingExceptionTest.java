package uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SchedulerProcessingExceptionTest {

    @Test
    public void should_be_runtime_exception_exception() {

        String someMessage = "someMessage";
        SchedulerProcessingException ex = new SchedulerProcessingException(new NullPointerException("someMessage"));
        assertEquals(NullPointerException.class, ex.getCause().getClass());
        assertEquals(someMessage, ex.getCause().getMessage());
    }
}
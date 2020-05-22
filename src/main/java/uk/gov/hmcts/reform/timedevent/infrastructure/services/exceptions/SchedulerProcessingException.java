package uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions;

public class SchedulerProcessingException extends RuntimeException {
    public SchedulerProcessingException(Throwable throwable) {
        super(throwable);
    }
}

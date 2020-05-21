package uk.gov.hmcts.reform.timedevent.infrastructure.services;

public class SchedulerProcessingException extends RuntimeException {
    public SchedulerProcessingException(Throwable throwable) {
        super(throwable);
    }
}

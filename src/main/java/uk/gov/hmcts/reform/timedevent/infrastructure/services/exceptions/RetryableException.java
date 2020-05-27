package uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions;

import org.quartz.JobExecutionException;

public class RetryableException extends JobExecutionException {
}

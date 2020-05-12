package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.timedevent.domain.entities.EventExecution;
import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.Event;
import uk.gov.hmcts.reform.timedevent.domain.services.EventExecutor;

@Slf4j
@Component
public class TimedEventJob implements Job {

    private final EventExecutor eventExecutor;

    public TimedEventJob(EventExecutor eventExecutor) {
        this.eventExecutor = eventExecutor;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getJobDetail().getJobDataMap();

        try {
            eventExecutor.execute(
                new EventExecution(
                    Event.fromString(data.getString("event")),
                    data.getString("jurisdiction"),
                    data.getString("caseType"),
                    data.getLong("caseId")
                )
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            // TODO re-trigger strategy
        }
    }
}

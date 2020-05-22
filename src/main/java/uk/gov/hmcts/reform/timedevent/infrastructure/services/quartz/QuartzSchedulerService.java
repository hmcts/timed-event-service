package uk.gov.hmcts.reform.timedevent.infrastructure.services.quartz;

import static java.time.ZoneOffset.UTC;

import com.google.common.collect.ImmutableMap;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.timedevent.domain.entities.TimedEvent;
import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.Event;
import uk.gov.hmcts.reform.timedevent.domain.services.SchedulerService;
import uk.gov.hmcts.reform.timedevent.infrastructure.services.IdentityProvider;
import uk.gov.hmcts.reform.timedevent.infrastructure.services.exceptions.SchedulerProcessingException;

@Slf4j
@Service
public class QuartzSchedulerService implements SchedulerService {

    private final Scheduler quartzScheduler;
    private final IdentityProvider identityProvider;

    public QuartzSchedulerService(Scheduler quartzScheduler, IdentityProvider identityProvider) {
        this.quartzScheduler = quartzScheduler;
        this.identityProvider = identityProvider;
    }

    @Override
    public String schedule(TimedEvent timedEvent) {

        return scheduleWithRetry(timedEvent, 0);
    }

    String scheduleWithRetry(TimedEvent timedEvent, long retryCount) {
        String identity = identityProvider.identity();

        JobDataMap data = new JobDataMap(
            new ImmutableMap.Builder<String, String>()
                .put("jurisdiction", timedEvent.getJurisdiction())
                .put("caseType", timedEvent.getCaseType())
                .put("caseId", String.valueOf(timedEvent.getCaseId()))
                .put("event", timedEvent.getEvent().toString())
                .put("retryCount", String.valueOf(retryCount))
                .build()
        );

        JobDetail job = JobBuilder.newJob().ofType(TimedEventJob.class)
            .storeDurably()
            .withIdentity(identity)
            .withDescription("Timed Event job")
            .usingJobData(data)
            .build();

        Trigger trigger = TriggerBuilder.newTrigger().forJob(job)
            .withIdentity(identity)
            .withDescription("Timed Event trigger")
            .usingJobData(data)
            .startAt(Date.from(timedEvent.getScheduledDateTime().toInstant()))
            .build();

        try {

            quartzScheduler.scheduleJob(job, trigger);

            log.info(
                "Timed Event scheduled for event: {}, case id: {} at: {}",
                timedEvent.getEvent().toString(),
                timedEvent.getCaseId(),
                timedEvent.getScheduledDateTime().toString()
            );

            return trigger.getKey().getName();

        } catch (SchedulerException e) {

            throw new SchedulerProcessingException(e);
        }
    }

    @Override
    public Optional<TimedEvent> get(String identity) {

        try {

            return Optional.ofNullable(quartzScheduler.getTrigger(new TriggerKey(identity)))
                .map(trigger -> {
                    JobDataMap data = trigger.getJobDataMap();

                    return new TimedEvent(
                        identity,
                        Event.fromString(data.getString("event")),
                        ZonedDateTime.ofInstant(trigger.getFinalFireTime().toInstant(), UTC),
                        data.getString("jurisdiction"),
                        data.getString("caseType"),
                        data.getLongFromString("caseId")
                    );
                });

        } catch (SchedulerException e) {

            throw new SchedulerProcessingException(e);
        }
    }
}

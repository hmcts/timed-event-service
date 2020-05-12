package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import static java.time.ZoneOffset.UTC;

import com.google.common.collect.ImmutableMap;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.timedevent.domain.entities.TimedEvent;
import uk.gov.hmcts.reform.timedevent.domain.entities.ccd.Event;
import uk.gov.hmcts.reform.timedevent.domain.services.SchedulerService;

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

        String identity = identityProvider.identity();

        JobDataMap data = new JobDataMap(
            new ImmutableMap.Builder<String, String>()
                .put("jurisdiction", timedEvent.getJurisdiction())
                .put("caseType", timedEvent.getCaseType())
                .put("caseId", String.valueOf(timedEvent.getCaseId()))
                .put("event", timedEvent.getEvent().toString())
                .build()
        );

        JobDetail job = JobBuilder.newJob().ofType(TimedEventJob.class)
            .storeDurably()
            .withIdentity(identity)
            .withDescription("Time Event job")
            .usingJobData(data)
            .build();

        Trigger trigger = TriggerBuilder.newTrigger().forJob(job)
            .withIdentity(identity)
            .withDescription("Time Event trigger")
            .usingJobData(data)
            .startAt(Date.from(timedEvent.getScheduledDateTime().toInstant()))
            .build();

        try {

            quartzScheduler.scheduleJob(job, trigger);

            log.info(
                "Timed Event scheduled for event: {}, case id: {}",
                timedEvent.getEvent().toString(),
                timedEvent.getCaseId()
            );

            return trigger.getKey().getName();

        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);

            // TODO handle gracefully
            throw new RuntimeException(e);
        }
    }

    @Override
    public TimedEvent get(String identity) {

        try {

            Trigger trigger = quartzScheduler.getTrigger(new TriggerKey(identity));

            JobDataMap data = trigger.getJobDataMap();

            return new TimedEvent(
                identity,
                Event.fromString(data.getString("event")),
                ZonedDateTime.ofInstant(trigger.getFinalFireTime().toInstant(), UTC),
                data.getString("jurisdiction"),
                data.getString("caseType"),
                data.getLongFromString("caseId")
            );

        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);

            // TODO handle gracefully
            throw new RuntimeException(e);
        }
    }
}

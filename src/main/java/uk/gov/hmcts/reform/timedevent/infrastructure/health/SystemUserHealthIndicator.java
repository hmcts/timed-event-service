package uk.gov.hmcts.reform.timedevent.infrastructure.health;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemTokenGenerator;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemUserProvider;

@Component
public class SystemUserHealthIndicator implements HealthIndicator {

    private final SystemTokenGenerator systemTokenGenerator;
    private final SystemUserProvider systemUserProvider;

    public SystemUserHealthIndicator(SystemTokenGenerator systemTokenGenerator, SystemUserProvider systemUserProvider) {
        this.systemTokenGenerator = systemTokenGenerator;
        this.systemUserProvider = systemUserProvider;
    }

    @Override
    public Health health() {

        String message;
        try {
            String token = systemTokenGenerator.generate();
            if (StringUtils.isNotBlank(token)) {

                String user = systemUserProvider.getSystemUserId("Bearer " + token);
                if (StringUtils.isNotBlank(user)) {
                    message = "UP";
                } else {
                    message = "user is blank";
                }

            } else {
                message = "token is blank";
            }

        } catch (Exception e) {

            message = "cannot authenticate system user: " + e.getMessage();
        }

        return Health.up()
            .withDetail("userStatus", message)
            .build();
    }
}

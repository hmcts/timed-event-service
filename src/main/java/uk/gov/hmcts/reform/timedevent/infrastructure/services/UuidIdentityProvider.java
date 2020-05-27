package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidIdentityProvider implements IdentityProvider {

    public String identity() {
        return UUID.randomUUID().toString();
    }
}

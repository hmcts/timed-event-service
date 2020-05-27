package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;


class UuidIdentityProviderTest {

    private UuidIdentityProvider identityProvider = new UuidIdentityProvider();

    @Test
    public void should_return_correct_uuid_string() {

        assertThat(identityProvider.identity())
            .matches(Pattern.compile("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"));
    }

}
package uk.gov.hmcts.reform.timedevent.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.IdamApi;
import uk.gov.hmcts.reform.timedevent.infrastructure.config.ServiceTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.timedevent.testutils.clients.*;
import uk.gov.hmcts.reform.timedevent.testutils.data.CaseDataFixture;
import uk.gov.hmcts.reform.timedevent.testutils.data.IdamAuthProvider;

@SpringBootTest(classes = {ServiceTokenGeneratorConfiguration.class, FunctionalSpringContext.class})
public class FunctionalTest {

    @Value("${idam.redirectUrl}") protected String idamRedirectUrl;
    @Value("${idam.system.scope}") protected String userScope;
    @Value("${spring.security.oauth2.client.registration.oidc.client-id}") protected String idamClientId;
    @Value("${spring.security.oauth2.client.registration.oidc.client-secret}") protected String idamClientSecret;

    @Value("classpath:templates/minimal-appeal-started.json")
    protected Resource minimalAppealStarted;

    @Autowired
    protected IdamApi idamApi;

    @Autowired
    protected AuthTokenGenerator s2sAuthTokenGenerator;

    @Autowired
    protected ExtendedCcdApi ccdApi;

    protected IdamAuthProvider idamAuthProvider;

    protected CaseDataFixture caseDataFixture;

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected final String targetInstance =
        StringUtils.defaultIfBlank(
            System.getenv("TEST_URL"),
            "http://localhost:8095"
        );

    protected RequestSpecification requestSpecification;

    @BeforeEach
    public void setup() {
        requestSpecification = new RequestSpecBuilder()
            .setBaseUri(targetInstance)
            .setRelaxedHTTPSValidation()
            .build();

        idamAuthProvider = new IdamAuthProvider(
            idamApi,
            idamRedirectUrl,
            userScope,
            idamClientId,
            idamClientSecret
        );

        caseDataFixture = new CaseDataFixture(
            ccdApi,
            objectMapper,
            s2sAuthTokenGenerator,
            minimalAppealStarted,
            idamAuthProvider
        );
    }

}

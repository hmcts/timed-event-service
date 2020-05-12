package uk.gov.hmcts.reform.timedevent.infrastructure.controllers.support;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import ru.lanwen.wiremock.ext.WiremockResolver;
import uk.gov.hmcts.reform.timedevent.testutils.SpringBootIntegrationTest;
import uk.gov.hmcts.reform.timedevent.testutils.StaticPortWiremockFactory;
import uk.gov.hmcts.reform.timedevent.testutils.WithIdamStub;
import uk.gov.hmcts.reform.timedevent.testutils.WithServiceAuthStub;

public class PostExecuteEventIntegrationTest extends SpringBootIntegrationTest implements WithIdamStub, WithServiceAuthStub {

    private String event = "example";
    private String caseId = "1234";
    private String jurisdiction = "someJurisdiction";
    private String caseType = "someCaseType";
    private String eventToken = "eventToken";
    private String state = "someState";

    @BeforeEach
    public void stubRequests(@WiremockResolver.Wiremock(factory = StaticPortWiremockFactory.class) WireMockServer server) {

        addIdamTokenStub(server);
        addUserInfoStub(server);

        addServiceAuthStub(server);

        server.addStubMapping(
            new StubMapping(
                newRequestPattern(RequestMethod.GET, urlEqualTo("/s2s/details"))
                    .build(),
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("ia")
                    .build()));

        String ccdStartUrl = "/ccd/caseworkers/" + USER_ID
                             + "/jurisdictions/" + jurisdiction
                             + "/case-types/" + caseType
                             + "/cases/" + caseId
                             + "/event-triggers/" + event
                             + "/token?ignore-warning=true";

        server.addStubMapping(
            new StubMapping(
                newRequestPattern(RequestMethod.GET, urlEqualTo(ccdStartUrl))
                    .build(),
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"token\": \"" + eventToken + "\", \"eventId\": \"" + event + "\"}")
                    .build()
            )
        );

        String ccdSubmitUrl = "/ccd/caseworkers/" + USER_ID
                             + "/jurisdictions/" + jurisdiction
                             + "/case-types/" + caseType
                             + "/cases/" + caseId
                             + "/events";

        server.addStubMapping(
            new StubMapping(
                newRequestPattern(RequestMethod.POST, urlEqualTo(ccdSubmitUrl))
                    .build(),
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"" + caseId + "\", \"state\": \"" + state + "\"}")
                    .build()
            )
        );

    }

    @Test
    public void executionEndpoint() throws Exception {

        String url = "/testing-support/execute/jurisdiction/" + jurisdiction + "/case-type/" + caseType + "/cid/" + caseId + "/event/" + event;

        MvcResult response = mockMvc
            .perform(post(url))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals("event: " + event + ", executed for id: " + caseId, response.getResponse().getContentAsString());
    }
}

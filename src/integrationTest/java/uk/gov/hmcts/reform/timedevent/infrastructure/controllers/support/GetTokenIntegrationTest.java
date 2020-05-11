package uk.gov.hmcts.reform.timedevent.infrastructure.controllers.support;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

public class GetTokenIntegrationTest extends SpringBootIntegrationTest {

    private static final String SYSTEM_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiYi9PNk92VnYxK3krV2dySDVVaTlXVGlvTHQwPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjY2QtaW1wb3J0QGZha2UuaG1jdHMubmV0IiwiYXV0aF9sZXZlbCI6MCwiYXVkaXRUcmFja2luZ0lkIjoiNjk0MjgxZmQtNzk5NS00N2FlLTlhM2QtMzk0M2IwYmM1NjY3IiwiaXNzIjoiaHR0cDovL2ZyLWFtOjgwODAvb3BlbmFtL29hdXRoMi9obWN0cyIsInRva2VuTmFtZSI6ImFjY2Vzc190b2tlbiIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJhdXRoR3JhbnRJZCI6ImE3M2I5YmNkLWFmYmItNDI0Mi1iNDZkLTg2NGM4MTE2ODZkYSIsImF1ZCI6ImNjZF9nYXRld2F5IiwibmJmIjoxNTg4NjY5MTYzLCJncmFudF90eXBlIjoicGFzc3dvcmQiLCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwicm9sZXMiXSwiYXV0aF90aW1lIjoxNTg4NjY5MTYzLCJyZWFsbSI6Ii9obWN0cyIsImV4cCI6MTU4ODY5Nzk2MywiaWF0IjoxNTg4NjY5MTYzLCJleHBpcmVzX2luIjoyODgwMCwianRpIjoiNjc1YzY2OWYtYzQ3YS00YjczLWI0NmItMDc1YzM5M2RjMmFkIn0.YZabJX_gnhnWB2k8bmOfB_xPLEc-OLgJ9Dj9J-BWvgWQAlWQNODfnItVDpTbFsX_FeNu7ivSd0IUtmL_2H-iaMEo0taGGUBl7Pewf28KXc3m3rb1lfm2083moKuYmtC23nH8XoUPyfdo2EwPgQa31nvwQMlCPNvEzKMAcpAZPWNDk4mPE-VMOrcvJwUan-mOEia4O6VotHU0VbPiPrubZG_PsyJVGJUVVWgmBqGk7WK_jfYfza4cbTGEL4eflYlqNUMmyM-wGd4ldtzaMdeuZvwByERymihnGu-yF7ZG5u1zr2pCvqTjH0Wgia7ToPhGH_vudoj-cKui-U5JGl6_Uw";

    @BeforeEach
    public void stubRequests(@WiremockResolver.Wiremock(factory = StaticPortWiremockFactory.class) WireMockServer server) {
        server.addStubMapping(
            new StubMapping(
                newRequestPattern(RequestMethod.POST, urlEqualTo("/idam/o/token"))
                    .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded;charset=UTF-8"))
                    .withRequestBody(
                        equalTo("grant_type=password"
                                + "&redirect_uri=http%3A%2F%2Flocalhost%3A3002%2Foauth2%2Fcallback"
                                + "&client_id=ia"
                                + "&client_secret=something"
                                + "&username=ia-system-user%40fake.hmcts.net"
                                + "&password=London05"
                                + "&scope=openid+profile+roles"
                        )
                    )
                    .build(),
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"access_token\": \"" + SYSTEM_USER_TOKEN + "\"}")
                    .build()
            )
        );
    }

    @Test
    public void tokenSupportEndpoint() throws Exception {
        MvcResult response = mockMvc
            .perform(get("/testing-support/token"))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(SYSTEM_USER_TOKEN, response.getResponse().getContentAsString());
    }
}

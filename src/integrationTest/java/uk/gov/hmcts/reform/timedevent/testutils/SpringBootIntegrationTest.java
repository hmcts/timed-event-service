package uk.gov.hmcts.reform.timedevent.testutils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static uk.gov.hmcts.reform.timedevent.testutils.StaticPortWiremockFactory.WIREMOCK_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;
import uk.gov.hmcts.reform.timedevent.Application;

@SpringBootTest(classes = {
    TestConfiguration.class,
    Application.class
})
@TestPropertySource(properties = {
    "CCD_URL=http://127.0.0.1:" + WIREMOCK_PORT + "/ccd"
})
@ExtendWith({
    WiremockResolver.class
})
@AutoConfigureMockMvc
public class SpringBootIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    public void stubRequests(@Wiremock(factory = StaticPortWiremockFactory.class) WireMockServer server) {

        server.addStubMapping(
            new StubMapping(
                newRequestPattern(RequestMethod.GET, urlEqualTo("/ccd/some-example"))
                    .build(),
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{}}")
                    .build()
            )
        );
    }
}

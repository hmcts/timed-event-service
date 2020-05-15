package uk.gov.hmcts.reform.timedevent.testutils;

import static uk.gov.hmcts.reform.timedevent.testutils.StaticPortWiremockFactory.WIREMOCK_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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
    "CCD_URL=http://127.0.0.1:" + WIREMOCK_PORT + "/ccd",
    "IDAM_URL=http://127.0.0.1:" + WIREMOCK_PORT + "/idam",
    "S2S_URL=http://127.0.0.1:" + WIREMOCK_PORT + "/s2s",
    "IA_IDAM_CLIENT_ID=ia",
    "IA_IDAM_SECRET=something"
})
@ExtendWith({
    WiremockResolver.class
})
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class SpringBootIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    public void stubRequests(@Wiremock(factory = StaticPortWiremockFactory.class) WireMockServer server) {

    }
}

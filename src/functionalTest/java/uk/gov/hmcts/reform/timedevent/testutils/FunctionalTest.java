package uk.gov.hmcts.reform.timedevent.testutils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {FunctionalSpringContext.class})
public class FunctionalTest {

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
    }

}

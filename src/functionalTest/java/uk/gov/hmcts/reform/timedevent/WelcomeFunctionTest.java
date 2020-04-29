package uk.gov.hmcts.reform.timedevent;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class WelcomeFunctionTest {

    private final String targetInstance =
        StringUtils.defaultIfBlank(
            System.getenv("TEST_URL"),
            "http://localhost:8095"
        );

    @Test
    public void should_allow_unauthenticated_requests_to_welcome_message_and_return_200_response_code() {

        String expected = "Welcome to Timed Event Service";

        RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBaseUri(targetInstance)
            .setRelaxedHTTPSValidation()
            .build();

        Response response = given(requestSpecification)
            .when()
            .get("/")
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().asString()).contains(expected);
    }

}

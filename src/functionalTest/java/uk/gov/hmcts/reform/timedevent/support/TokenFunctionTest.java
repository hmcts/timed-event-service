package uk.gov.hmcts.reform.timedevent.support;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.timedevent.testutils.FunctionalTest;

public class TokenFunctionTest extends FunctionalTest {

    @Test
    public void should_return_system_user_token() {

        Response response = given(requestSpecification)
            .when()
            .get("/testing-support/token")
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().asString()).matches(Pattern.compile("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"));
    }

}

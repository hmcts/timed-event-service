package uk.gov.hmcts.reform.timedevent.support;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.timedevent.testutils.FunctionalTest;

public class SystemUserIdFunctionTest extends FunctionalTest {

    @Test
    public void should_return_system_user_id() {

        Response response = given(requestSpecification)
            .when()
            .get("/testing-support/system-user")
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().asString()).matches(Pattern.compile("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"));
    }

}

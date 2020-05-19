package uk.gov.hmcts.reform.timedevent.support;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.timedevent.testutils.FunctionalTest;

public class ExecuteEventFunctionTest extends FunctionalTest {

    @Test
    public void should_return_400_when_event_not_found() {

        String jurisdiction = "IA";
        String caseType = "Asylum";
        String event = "notExistingEvent";
        String caseId = "123456789";

        String url = String.format("/execute/testing-support/jurisdiction/%s/case-type/%s/cid/%s/event/%s", jurisdiction, caseType, caseId, event);

        Response response = given(requestSpecification)
            .when()
            .post(url)
            .then()
            .extract()
            .response();

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getBody().asString()).contains("cannot find event: " + event);
    }

    @Test
    public void should_return_400_when_case_is_not_existing() {

        String jurisdiction = "IA";
        String caseType = "Asylum";
        String event = "example";
        String caseId = "123456789";

        String url = String.format("/execute/testing-support/jurisdiction/%s/case-type/%s/cid/%s/event/%s", jurisdiction, caseType, caseId, event);

        Response response = given(requestSpecification)
            .when()
            .post(url)
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getBody().asString()).contains("Case reference is not valid");
    }

    // TODO should_return_400_when_system_user_does_not_ccd_access_to_event

    // TODO should_return_400_when_case_is_in_wrong_state

    // TODO happy path

}

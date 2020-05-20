package uk.gov.hmcts.reform.timedevent.support;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.timedevent.testutils.FunctionalTest;
import uk.gov.hmcts.reform.timedevent.testutils.data.CaseDataFixture;

public class ExecuteEventFunctionTest extends FunctionalTest {

    private String jurisdiction = "IA";
    private String caseType = "Asylum";

    @Test
    public void should_return_400_when_event_not_found() {

        String event = "notExistingEvent";
        String caseId = "123456789";

        String url = String.format("/testing-support/execute/jurisdiction/%s/case-type/%s/cid/%s/event/%s", jurisdiction, caseType, caseId, event);

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
    public void should_return_400_when_case_does_not_exists() {

        String event = "example";
        String caseId = "123456789";

        String url = String.format("/testing-support/execute/jurisdiction/%s/case-type/%s/cid/%s/event/%s", jurisdiction, caseType, caseId, event);

        Response response = given(requestSpecification)
            .when()
            .post(url)
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getBody().asString()).contains("Case reference is not valid");
    }

    @Test
    public void should_return_404_when_event_is_not_defined_in_ccd() {

        CaseDataFixture caseDataFixture = new CaseDataFixture(
            ccdApi,
            objectMapper,
            s2sAuthTokenGenerator,
            minimalAppealStarted,
            idamAuthProvider,
            mapValueExpander
        );

        caseDataFixture.startAppeal();

        long caseId = caseDataFixture.getCaseId();
        String event = "unknown";

        String url = String.format("/testing-support/execute/jurisdiction/%s/case-type/%s/cid/%d/event/%s", jurisdiction, caseType, caseId, event);

        Response response = given(requestSpecification)
            .when()
            .post(url)
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(404);
        assertThat(response.getBody().asString()).contains("Cannot find event unknown for case type Asylum");
    }

    @Test
    public void should_return_504_when_system_user_does_not_have_access_in_ccd() {

        CaseDataFixture caseDataFixture = new CaseDataFixture(
            ccdApi,
            objectMapper,
            s2sAuthTokenGenerator,
            minimalAppealStarted,
            idamAuthProvider,
            mapValueExpander
        );

        caseDataFixture.startAppeal();
        caseDataFixture.submitAppeal();

        long caseId = caseDataFixture.getCaseId();
        String event = "requestRespondentEvidence";

        String url = String.format("/testing-support/execute/jurisdiction/%s/case-type/%s/cid/%d/event/%s", jurisdiction, caseType, caseId, event);

        Response response = given(requestSpecification)
            .when()
            .post(url)
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(504);
        assertThat(response.getBody().asString()).contains("Callback to service has been unsuccessful for event Request respondent evidence");
    }

    @Test
    public void should_return_422_when_case_is_in_wrong_state() {

        CaseDataFixture caseDataFixture = new CaseDataFixture(
            ccdApi,
            objectMapper,
            s2sAuthTokenGenerator,
            minimalAppealStarted,
            idamAuthProvider,
            mapValueExpander
        );

        caseDataFixture.startAppeal();
        caseDataFixture.submitAppeal();

        long caseId = caseDataFixture.getCaseId();
        String event = "requestHearingRequirementsFeature";

        String url = String.format("/testing-support/execute/jurisdiction/%s/case-type/%s/cid/%d/event/%s", jurisdiction, caseType, caseId, event);

        Response response = given(requestSpecification)
            .when()
            .post(url)
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(422);
        assertThat(response.getBody().asString()).contains("The case status did not qualify for the event");
    }
}

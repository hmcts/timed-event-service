package uk.gov.hmcts.reform.timedevent;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.timedevent.testutils.FunctionalTest;

public class RequestHearingRequirementsFunctionTest extends FunctionalTest {

    private String event = "requestHearingRequirementsFeature";
    private long caseId;

    @BeforeEach
    public void createCase() {

        caseId = caseDataFixture.startAppeal();
        caseDataFixture.submitAppeal(caseId);
        caseDataFixture.requestRespondentEvidence(caseId);
        caseDataFixture.uploadRespondentEvidence(caseId);
        caseDataFixture.buildCase(caseId);
        caseDataFixture.submitCase(caseId);
        caseDataFixture.requestRespondentReview(caseId);
        caseDataFixture.requestResponseReview(caseId);
    }

    @Test
    public void should_trigger_requestHearingRequirementsFeature_event() {

        Response response = given(requestSpecification)
            .when()
            .header(new Header("Authorization", idamAuthProvider.getCaseOfficerToken()))
            .header(new Header("ServiceAuthorization", s2sAuthTokenGenerator.generate()))
            .contentType("application/json")
            .body("{ \"jurisdiction\": \"IA\","
                  + " \"caseType\": \"Asylum\","
                  + " \"caseId\": " + caseId + ","
                  + " \"event\": \"" + event + "\","
                  + " \"scheduledDateTime\": \"2020-05-13T10:00:00Z\" }"
            )
            .post("/timed-event")
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);

        // TODO add assert for case data in submitHearingRequirements state
    }

    // TODO no-authenticated

    // TODO authenticated by s2s only

    // TODO authenticated by idam only

    // TODO wrong request body - missing fields

    // TODO happy path with date in future - assert with get

    // TODO happy path with date in past - immediate run assert with get (NotFound) ?? consider archive endpoint ??

}

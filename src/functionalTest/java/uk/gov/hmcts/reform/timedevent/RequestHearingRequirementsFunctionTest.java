package uk.gov.hmcts.reform.timedevent;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.timedevent.testutils.FunctionalTest;

import java.time.ZonedDateTime;

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

        String jurisdiction = "IA";
        String caseType = "Asylum";

        // TODO assert TimedEvent creation by ia-case-api

        // run Timed Event with DateTime now
        Response response = given(requestSpecification)
            .when()
            .header(new Header("Authorization", idamAuthProvider.getCaseOfficerToken()))
            .header(new Header("ServiceAuthorization", s2sAuthTokenGenerator.generate()))
            .contentType("application/json")
            .body("{ \"jurisdiction\": \"" + jurisdiction + "\","
                  + " \"caseType\": \"" + caseType + "\","
                  + " \"caseId\": " + caseId + ","
                  + " \"event\": \"" + event + "\","
                  + " \"scheduledDateTime\": \"" + ZonedDateTime.now().toString() + "\" }"
            )
            .post("/timed-event")
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);

        String token = idamAuthProvider.getCaseOfficerToken();

        await().pollInterval(2, SECONDS).atMost(10, SECONDS).until(() -> {
            CaseDetails caseDetails = ccdApi.get(
                token,
                s2sAuthTokenGenerator.generate(),
                idamAuthProvider.getUserId(token),
                jurisdiction,
                caseType,
                String.valueOf(caseId)
            );

            return caseDetails.getState().equals("submitHearingRequirements");
        });
    }

    // TODO no-authenticated

    // TODO authenticated by s2s only

    // TODO authenticated by idam only

    // TODO wrong request body - missing fields

    // TODO happy path with date in future - assert with get

    // TODO happy path with date in past - immediate run assert with get (NotFound) ?? consider archive endpoint ??

}

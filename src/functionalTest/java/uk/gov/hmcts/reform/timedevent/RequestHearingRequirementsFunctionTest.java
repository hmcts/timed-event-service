package uk.gov.hmcts.reform.timedevent;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.restassured.http.Header;
import io.restassured.response.Response;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.timedevent.testutils.FunctionalTest;
import uk.gov.hmcts.reform.timedevent.testutils.data.CaseDataFixture;

public class RequestHearingRequirementsFunctionTest extends FunctionalTest {

    private String jurisdiction = "IA";
    private String caseType = "Asylum";
    private String event = "requestHearingRequirementsFeature";

    private String caseDataField = "automaticDirectionRequestingHearingRequirements";
    private Pattern uuidPattern = Pattern.compile("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}");

    private String systemUserToken;
    private String systemUserId;

    private CaseDataFixture caseDataFixture;

    @BeforeEach
    public void createCase() {

        systemUserToken = idamAuthProvider.getSystemUserToken();
        systemUserId = idamApi.userInfo(systemUserToken).getUid();

        caseDataFixture = new CaseDataFixture(
            ccdApi,
            objectMapper,
            s2sAuthTokenGenerator,
            minimalAppealStarted,
            idamAuthProvider,
            mapValueExpander
        );

        caseDataFixture.startAppeal();
        caseDataFixture.submitAppeal();
        caseDataFixture.requestRespondentEvidence();
        caseDataFixture.uploadRespondentEvidence();
        caseDataFixture.buildCase();
        caseDataFixture.submitCase();
        caseDataFixture.requestRespondentReview();
        caseDataFixture.uploadHomeOfficeAppealResponse();
        caseDataFixture.requestResponseReview();
    }

    @Test
    public void should_trigger_requestHearingRequirementsFeature_event() {

        long caseId = caseDataFixture.getCaseId();

        // confirm than Timed Event has been created by ia-case-api
        CaseDetails caseDetails = ccdApi.get(
            systemUserToken,
            caseDataFixture.getS2sToken(),
            systemUserId,
            jurisdiction,
            caseType,
            String.valueOf(caseId)
        );
        assertThat((String) caseDetails.getCaseData().get(caseDataField)).matches(uuidPattern);

        // execute Timed Event now
        Response response = scheduleEventNow(caseId);
        assertThat(response.getStatusCode()).isEqualTo(200);

        // assert that Timed Event execution changed case state
        assertThatCaseIsInState(caseId, "submitHearingRequirements");
    }

    private Response scheduleEventNow(long caseId) {


        return given(requestSpecification)
            .when()
            .header(new Header("Authorization", caseDataFixture.getCaseOfficerToken()))
            .header(new Header("ServiceAuthorization", caseDataFixture.getS2sToken()))
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
    }

    private void assertThatCaseIsInState(long caseId, String state) {
        await().pollInterval(2, SECONDS).atMost(10, SECONDS).until(() ->
            ccdApi.get(
                systemUserToken,
                caseDataFixture.getS2sToken(),
                systemUserId,
                jurisdiction,
                caseType,
                String.valueOf(caseId)
            ).getState().equals(state)
        );
    }

    // TODO no-authenticated

    // TODO authenticated by s2s only

    // TODO authenticated by idam only

    // TODO wrong request body - missing fields

    // TODO happy path with date in future - assert with get

    // TODO happy path with date in past - immediate run assert with get (NotFound) ?? consider archive endpoint ??

}

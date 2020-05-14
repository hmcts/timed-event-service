package uk.gov.hmcts.reform.timedevent;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.timedevent.testutils.FunctionalTest;

public class ScheduleTimeEventFunctionTest extends FunctionalTest {

    private long caseId;

    @BeforeEach
    public void createCase() {

        caseId = caseDataFixture.startAppeal();
        caseDataFixture.submitAppeal(caseId);
        caseDataFixture.requestRespondentEvidence(caseId);

    }

    @Test
    public void should_not_allow_unauthenticated_requests_to_schedule_event() {

        Response response = given(requestSpecification)
            .when()
            .post("/timed-event")
            .then()
            .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(403);
    }

    // TODO authenticated by s2s only

    // TODO authenticated by idam

    // TODO wrong request body - missing fields

    // TODO happy path with date in future - assert with get

    // TODO happy path with date in past - immediate run assert with get (NotFound) ?? consider archive endpoint ??

}

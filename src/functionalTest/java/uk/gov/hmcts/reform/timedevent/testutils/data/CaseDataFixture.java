package uk.gov.hmcts.reform.timedevent.testutils.data;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.Event;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.StartEventTrigger;
import uk.gov.hmcts.reform.timedevent.testutils.clients.ExtendedCcdApi;

public class CaseDataFixture {

    private final String jurisdiction = "IA";
    private final String caseType = "Asylum";

    private final ObjectMapper objectMapper;
    private final ExtendedCcdApi ccdApi;
    private final AuthTokenGenerator s2sAuthTokenGenerator;
    private final Resource minimalAppealStarted;
    private final IdamAuthProvider idamAuthProvider;

    public CaseDataFixture(
        ExtendedCcdApi ccdApi,
        ObjectMapper objectMapper,
        AuthTokenGenerator s2sAuthTokenGenerator,
        Resource minimalAppealStarted,
        IdamAuthProvider idamAuthProvider
    ) {
        this.ccdApi = ccdApi;
        this.objectMapper = objectMapper;
        this.s2sAuthTokenGenerator = s2sAuthTokenGenerator;
        this.minimalAppealStarted = minimalAppealStarted;
        this.idamAuthProvider = idamAuthProvider;
    }

    public long startAppeal() {

        String event = "startAppeal";

        String userToken = idamAuthProvider.getLegalRepToken();

        String s2sToken = s2sAuthTokenGenerator.generate();
        String userId = idamAuthProvider.getUserId(userToken);

        StartEventTrigger startEventResponse = ccdApi.startCaseCreation(userToken, s2sToken, userId, jurisdiction, caseType, event);

        Map<String, Object> data = Collections.emptyMap();
        try {
            data = objectMapper.readValue(
                asString(minimalAppealStarted),
                new TypeReference<Map<String, Object>>() {
                }
            );
        } catch (Exception e) {
            // ignore - test will fail
        }

        new MapValueExpander().expandValues(data);
        CaseDataContent content = new CaseDataContent(
            new Event(event, event, event),
            startEventResponse.getToken(),
            true,
            data
        );

        CaseDetails submit = ccdApi.submitCaseCreation(userToken, s2sToken, userId, jurisdiction, caseType, content);

        return submit.getId();
    }

    public String submitAppeal(long caseId) {

        String userToken = idamAuthProvider.getLegalRepToken();

        return triggerEvent(
            userToken,
            s2sAuthTokenGenerator.generate(),
            idamAuthProvider.getUserId(userToken),
            caseId,
            "submitAppeal",
            Collections.emptyMap()
        );
    }

    public String requestRespondentEvidence(long caseId) {

        Map<String, Object> data = new HashMap<>();
        data.put("sendDirectionExplanation", "A notice of appeal has been lodged against this asylum decision.\n"
                                             + "\n"
                                             + "You must now send all documents to the case officer. The case officer will send them to the other party. You have 14 days to supply these documents.\n"
                                             + "\n"
                                             + "You must include:\n"
                                             + "- the notice of decision\n"
                                             + "- any other document provided to the appellant giving reasons for that decision\n"
                                             + "- any statements of evidence\n"
                                             + "- the application form\n"
                                             + "- any record of interview with the appellant in relation to the decision being appealed\n"
                                             + "- any other unpublished documents on which you rely\n"
                                             + "- the notice of any other appealable decision made in relation to the appellant"
        );
        data.put("sendDirectionDateDue", "{$TODAY+14}");
        data.put("sendDirectionParties", "respondent");

        String userToken = idamAuthProvider.getCaseOfficerToken();

        return triggerEvent(
            userToken,
            s2sAuthTokenGenerator.generate(),
            idamAuthProvider.getUserId(userToken),
            caseId,
            "requestRespondentEvidence",
            data
        );
    }

    private String triggerEvent(String userToken, String s2sToken, String userId, long caseId, String event, Map<String, Object> data) {

        StartEventTrigger startEventResponse = ccdApi.startEvent(userToken, s2sToken, userId, jurisdiction, caseType, String.valueOf(caseId), event);

        new MapValueExpander().expandValues(data);

        CaseDataContent content = new CaseDataContent(
            new Event(event, event, event),
            startEventResponse.getToken(),
            true,
            data
        );

        CaseDetails submit = ccdApi.submitEvent(userToken, s2sToken, userId, jurisdiction, caseType, String.valueOf(caseId), content);

        return submit.getState();
    }


    private String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

package uk.gov.hmcts.reform.timedevent.infrastructure.services;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.timedevent.domain.services.EventExecutor;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.CcdApi;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.Event;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd.StartEventTrigger;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemTokenGenerator;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemUserProvider;

@Slf4j
@Service
public class CcdEventExecutor implements EventExecutor {

    private final SystemTokenGenerator systemTokenGenerator;

    private final SystemUserProvider systemUserProvider;

    private final AuthTokenGenerator s2sAuthTokenGenerator;

    private final CcdApi ccdApi;

    public CcdEventExecutor(
        SystemTokenGenerator systemTokenGenerator,
        SystemUserProvider systemUserProvider,
        AuthTokenGenerator s2sAuthTokenGenerator,
        CcdApi ccdApi
    ) {
        this.systemTokenGenerator = systemTokenGenerator;
        this.systemUserProvider = systemUserProvider;
        this.s2sAuthTokenGenerator = s2sAuthTokenGenerator;
        this.ccdApi = ccdApi;
    }

    @Override
    public void execute(String jurisdiction, String caseType, String event, long id) {

        log.info("Execution event: {}, for case id: {} has been started.", event, id);

        String userToken = "Bearer " + systemTokenGenerator.generate();
        String s2sToken = "Bearer " + s2sAuthTokenGenerator.generate();

        String uid = systemUserProvider.getSystemUserId(userToken);

        StartEventTrigger startEventResponse = ccdApi.startEvent(
            userToken,
            s2sToken,
            uid,
            jurisdiction,
            caseType,
            String.valueOf(id),
            event
        );

        log.info("Execution token generated for event: {}, for case id: {}. Token: {}", event, id, startEventResponse.getToken());

        CaseDetails caseDetails = ccdApi.submitEvent(
            userToken,
            s2sToken,
            uid,
            jurisdiction,
            caseType,
            String.valueOf(id),
            new CaseDataContent(
                new Event(event, event, event),
                startEventResponse.getToken(),
                true,
                Collections.emptyMap()
            )
        );

        log.info("Event: {}, for case id: {} has been executed. Case state: {}", event, id, caseDetails.getState());
    }
}

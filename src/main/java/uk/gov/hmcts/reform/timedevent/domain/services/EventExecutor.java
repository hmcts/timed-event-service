package uk.gov.hmcts.reform.timedevent.domain.services;

public interface EventExecutor {

    void execute(String jurisdiction, String caseType, String event, long id);
}

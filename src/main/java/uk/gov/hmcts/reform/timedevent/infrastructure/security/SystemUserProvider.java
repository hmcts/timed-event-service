package uk.gov.hmcts.reform.timedevent.infrastructure.security;

public interface SystemUserProvider {

    String getSystemUserId(String userToken);
}

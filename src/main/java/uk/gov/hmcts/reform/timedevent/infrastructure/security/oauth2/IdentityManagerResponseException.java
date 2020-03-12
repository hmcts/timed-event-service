package uk.gov.hmcts.reform.timedevent.infrastructure.security.oauth2;

public class IdentityManagerResponseException extends RuntimeException {

    public IdentityManagerResponseException(String message, Throwable cause) {
        super(message, cause);
    }

}

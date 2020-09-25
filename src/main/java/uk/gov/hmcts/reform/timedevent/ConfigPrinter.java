package uk.gov.hmcts.reform.timedevent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigPrinter {

    public ConfigPrinter(
        @Value("${spring.security.oauth2.client.provider.oidc.issuer-uri}") String idamUrl,
        @Value("${spring.security.oauth2.client.registration.oidc.client-id") String clientId,
        @Value("${spring.security.oauth2.client.registration.oidc.client-secret") String clientSecret,
        @Value("${idam.redirectUrl}") String redirectUrl,
        @Value("${idam.system.username}") String idamSystemUser,
        @Value("${idam.system.password}") String idamSystemPassword,
        @Value("${idam.s2s-auth.totp_secret}") String s2sSecret,
        @Value("${idam.s2s-auth.microservice}") String microservice

    ) {
        System.out.println("${spring.security.oauth2.client.provider.oidc.issuer-uri} [" + idamUrl + "]\n"
                           + "${spring.security.oauth2.client.registration.oidc.client-id [" + clientId + "]\n"
                           + "${spring.security.oauth2.client.registration.oidc.client-secret [" + clientSecret + "]\n"
                           + "${idam.redirectUrl} [" + redirectUrl + "]\n"
                           + "${idam.system.username} [" + idamSystemUser + "]\n"
                           + "${idam.system.password} [" + idamSystemPassword + "]\n"
                           + "${idam.s2s-auth.totp_secret} [" + s2sSecret + "]\n"
                           + "${idam.s2s-auth.microservice} [" + microservice + "]\n");
    }
}

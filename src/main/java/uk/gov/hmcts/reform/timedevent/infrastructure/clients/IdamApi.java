package uk.gov.hmcts.reform.timedevent.infrastructure.clients;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.IdamToken;
import uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.IdamUserInfo;
import uk.gov.hmcts.reform.timedevent.infrastructure.config.FeignConfiguration;

@FeignClient(
    name = "idam-api",
    url = "${idam.baseUrl}",
    configuration = FeignConfiguration.class
)
public interface IdamApi {

    @GetMapping(value = "/o/userinfo", produces = "application/json", consumes = "application/json")
    IdamUserInfo userInfo(@RequestHeader(AUTHORIZATION) String userToken);

    @PostMapping(value = "/o/token", produces = "application/json", consumes = "application/x-www-form-urlencoded")
    IdamToken token(@RequestBody Map<String, ?> form);

}
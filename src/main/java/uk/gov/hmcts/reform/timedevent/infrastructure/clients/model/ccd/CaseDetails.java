package uk.gov.hmcts.reform.timedevent.infrastructure.clients.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseDetails {

    private Long id;
    private String state;
    private Map<String, Object> data;
}

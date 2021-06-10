package uk.gov.hmcts.reform.timedevent.domain.entities.ccd;


import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum Event {

    REQUEST_RESPONDENT_EVIDENCE("requestRespondentEvidence"),
    EXAMPLE("example"),
    REQUEST_HEARING_REQUIREMENTS_FEATURE("requestHearingRequirementsFeature"),
    PAYMENT_PENDING_FEATURE("paymentPendingFeature"),

    @JsonEnumDefaultValue
    UNKNOWN("unknown");

    @JsonValue
    private final String id;

    Event(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    public static Event fromString(String event) {
        return Arrays.stream(Event.values())
            .filter(e -> e.id.equals(event))
            .findAny()
            .orElseThrow(() -> new EventNotFoundException("cannot find event: " + event));
    }
}

package uk.gov.hmcts.reform.timedevent.domain.entities.ccd;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void has_correct_values() {

        assertEquals("requestRespondentEvidence", Event.REQUEST_RESPONDENT_EVIDENCE.toString());
        assertEquals("example", Event.EXAMPLE.toString());
        assertEquals("requestHearingRequirementsFeature", Event.REQUEST_HEARING_REQUIREMENTS_FEATURE.toString());
        assertEquals("moveToPaymentPending", Event.MOVE_TO_PAYMENT_PENDING.toString());
        assertEquals("rollbackPayment", Event.ROLLBACK_PAYMENT.toString());
    }

    @Test
    void if_this_test_fails_it_is_because_it_needs_updating_with_your_changes() {

        assertEquals(6, Event.values().length);
    }

}

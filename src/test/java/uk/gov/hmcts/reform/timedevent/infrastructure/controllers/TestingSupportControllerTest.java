package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.timedevent.infrastructure.security.SystemTokenGenerator;

@ExtendWith(MockitoExtension.class)
class TestingSupportControllerTest {

    @Mock
    private SystemTokenGenerator systemTokenGenerator;

    @Test
    void should_return_token_from_generator() {

        String token = "someUserTokenHash";
        when(systemTokenGenerator.generate()).thenReturn(token);

        TestingSupportController testingSupportController = new TestingSupportController(systemTokenGenerator);

        ResponseEntity<String> response = testingSupportController.token();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
    }
}
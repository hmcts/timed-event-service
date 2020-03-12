package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RootControllerTest {

    private RootController rootController = new RootController();

    @Test
    void should_return_welcome_message() {

        ResponseEntity<String> response = rootController.welcome();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome to Timed Event Service", response.getBody());
    }

}
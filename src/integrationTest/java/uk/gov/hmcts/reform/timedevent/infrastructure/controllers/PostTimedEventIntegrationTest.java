package uk.gov.hmcts.reform.timedevent.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.timedevent.testutils.SpringBootIntegrationTest;

public class PostTimedEventIntegrationTest extends SpringBootIntegrationTest {

    @Test
    @WithMockUser(authorities = {"caseworker-ia-caseofficer"})
    public void timedEventEndpoint() throws Exception {

        MvcResult response = mockMvc
            .perform(
                post("/timed-event")
                    .content(requestBody())
                    .contentType("application/json")
            )
            .andExpect(status().isOk())
            .andReturn();

        assertEquals("Welcome to timed-event controller", response.getResponse().getContentAsString());
    }

    private String requestBody() {
        return "{  \"event\": \"unknown\"}";
    }
}

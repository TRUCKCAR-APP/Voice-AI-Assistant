package escuelaing.edu.co.truckcar.voice_ai_assistant.controller;

import escuelaing.edu.co.truckcar.voice_ai_assistant.dto.AssistantResponse;
import escuelaing.edu.co.truckcar.voice_ai_assistant.dto.IntentAction;
import escuelaing.edu.co.truckcar.voice_ai_assistant.service.IntentOrchestrator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssistantController.class)
class AssistantControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    IntentOrchestrator orchestrator;

    @Test
    void interpretsForTheGatewayUser() throws Exception {
        when(orchestrator.interpret(eq("u-42"), any())).thenReturn(
                new AssistantResponse(IntentAction.REPORT, "landslide", false, null, null, null, "AZURE_OPENAI"));
        mvc.perform(post("/api/v1/assistant/interpret").header("X-Auth-User", "u-42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"se vino la montaña\",\"context\":{\"hasLoad\":true,\"navigating\":true}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intent").value("REPORT"))
                .andExpect(jsonPath("$.alertType").value("landslide"))
                .andExpect(jsonPath("$.field").doesNotExist());
    }

    @Test
    void withoutGatewayIdentityIs401() throws Exception {
        mvc.perform(post("/api/v1/assistant/interpret").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"hola\"}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(orchestrator);
    }

    @Test
    void blankOrLongTextIs400() throws Exception {
        mvc.perform(post("/api/v1/assistant/interpret").header("X-Auth-User", "u-42")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"  \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/assistant/interpret").header("X-Auth-User", "u-42")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"" + "a".repeat(301) + "\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(orchestrator);
    }
}

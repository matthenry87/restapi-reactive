package com.matthenry87.restapireactive.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = TestController.class)
class GlobalExceptionHandlerTest {

    @MockBean
    private Pojo mock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webClient;

    @Test
    void serverWebInputException() throws JsonProcessingException {
        // Arrange
        Pojo pojo = createPojo();

        String json = objectMapper.writeValueAsString(pojo).replace("OPEN", "FOO");

        // Act/Assert
        webClient.post().uri("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void webExchangeBindException() {
        // Arrange/Act/Assert
        webClient.post().uri("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void notFoundException() throws JsonProcessingException {
        // Arrange
        String json = getJson();

        when(mock.foo()).thenThrow(NotFoundException.class);

        // Act/Assert
        webClient.post().uri("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void alreadyExistsException() throws JsonProcessingException {
        // Arrange
        String json = getJson();

        when(mock.foo()).thenThrow(AlreadyExistsException.class);

        // Act/Assert
        webClient.post().uri("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void unsupportedMediaTypeStatusException() throws JsonProcessingException {
        // Arrange
        String json = getJson();

        when(mock.foo()).thenThrow(AlreadyExistsException.class);

        // Act/Assert
        webClient.post().uri("/test")
                .contentType(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().value(is(415));
    }

    @Test
    void exception() throws JsonProcessingException {
        // Arrange
        String json = getJson();

        when(mock.foo()).thenThrow(new RuntimeException());

        // Act/Assert
        webClient.post().uri("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    private Pojo createPojo() {
        Pojo pojo = new Pojo();
        pojo.setFoo("foo");
        pojo.setStatus(Status.OPEN);
        return pojo;
    }

    private String getJson() throws JsonProcessingException {
        Pojo pojo = createPojo();

        return objectMapper.writeValueAsString(pojo);
    }

}

package com.matthenry87.restapireactive.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Mock
    private Pojo mock;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebTestClient webClient;

    @BeforeEach
    void init() {

        MockitoAnnotations.initMocks(this);

        TestController testController = new TestController(mock);

        webClient = WebTestClient.bindToController(testController)
                .controllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

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

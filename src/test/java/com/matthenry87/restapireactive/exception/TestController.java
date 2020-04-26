package com.matthenry87.restapireactive.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/test")
class TestController {

    private final Pojo mock;

    TestController(Pojo mock) {
        this.mock = mock;
    }

    @PostMapping
    public Mono<Object> post(@RequestBody @Valid Pojo pojo) {

        mock.foo();

        return Mono.just("lalala");
    }

}

@Getter
@Setter
@Component
@NoArgsConstructor
class Pojo {

    @NotEmpty
    private String foo;

    private Status status;

    public Object foo() { return null; }

}

enum Status {

    OPEN
}
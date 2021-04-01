package com.luxoft.webflux;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

public class WebClientApplication {

    @Test
    public void test() {
        WebClient client = WebClient
                .builder()
                .baseUrl("http://localhost:8080")
                .defaultHeader(HttpHeaders.USER_AGENT,
                        "Spring 5 WebClient")
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)
                .build();

        client.get()
                .uri("/person/names")
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofMillis(1000))
                .subscribe(System.out::println,
                        err-> System.out.println("ERROR "+err.getMessage()));

        ParameterizedTypeReference<ServerSentEvent<String>> type =
                new ParameterizedTypeReference<ServerSentEvent<String>>() {};

        client.get()
                .uri("/person/stream")
                .accept(TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(type)
                .timeout(Duration.ofMillis(5000))
                .subscribe(content -> {
                    System.out.println(content.data());
                }, err-> System.out.println("ERROR "+err.getMessage()));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.digitalmoneyhouse.gateway.client;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("lb://user-service").build();
    }

    public Mono<Boolean> isTokenBlacklisted(String token) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/users/blacklist/exists")
                                .queryParam("token", token)
                                .build()
                )
                .retrieve()
                .bodyToMono(Boolean.class);
    }

}

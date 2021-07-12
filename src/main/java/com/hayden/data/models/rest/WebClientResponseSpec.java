package com.hayden.data.models.rest;

import com.hayden.data.models.shared.response.AbstractSpec;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebClientResponseSpec implements AbstractSpec {

    WebClient.ResponseSpec responseSpec;

    public WebClientResponseSpec(WebClient.ResponseSpec responseSpec)
    {
        this.responseSpec = responseSpec;
    }

    @Override
    public <T> Mono<T> single(Class<T> clzz)
    {
        return this.responseSpec.bodyToMono(clzz);
    }

    @Override
    public <T> Flux<T> multiple(Class<T> clzz)
    {
        return this.responseSpec.bodyToFlux(clzz);
    }
}

package com.hayden.data.models.shared.response;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AbstractSpec {

    <T> Mono<T> single(Class<T> clzz);
    <T> Flux<T> multiple(Class<T> clzz);

}

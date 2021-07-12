package com.hayden.data.models.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.response.AbstractSpec;
import com.hayden.data.models.shared.response.ExchangeRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

@Component
@Scope("prototype")
@Data
@NoArgsConstructor
public class SocketSpec implements AbstractSpec {

    ObjectMapper objectMapper;
    ExchangeRequest request;
    Url url;

    public SocketSpec(ExchangeRequest request, Url url, ObjectMapper objectMapper)
    {
        this.request = request;
        this.url = url;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> Mono<T> single(Class<T> clzz)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <TO> Flux<TO> multiple(Class<TO> toClass)
    {
        try {
            var requestMessage = objectMapper.writeValueAsString(request);
            WebSocketClient client = new ReactorNettyWebSocketClient();
            Sinks.Many<TO> sink = Sinks.many().unicast().onBackpressureBuffer();
            client.execute(
                    URI.create(url.build()),
                    session -> session.send(Mono.just(session.textMessage(requestMessage)))
                            .thenMany(session.receive().doOnNext(webSocketMessage -> {
                                          try {
                                              TO toShare = objectMapper.readValue(webSocketMessage.getPayloadAsText(), toClass);
                                              System.out.println(toShare);
                                              sink.tryEmitNext(toShare);
                                          } catch (JsonProcessingException e) {
                                              e.printStackTrace();
                                          }
                                      })
                            ).then()
            ).subscribe();
            return sink.asFlux();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Flux.empty();
    }

}

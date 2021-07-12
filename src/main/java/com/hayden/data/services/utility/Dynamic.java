package com.hayden.data.services.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.services.rest.APIDataService;
import com.hayden.decision.models.asset.Asset;
import com.hayden.dynamicparse.parse.DynamicParseJson;
import com.hayden.dynamicparse.parse.DynamicParsingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class Dynamic {

    @Value("${dynamic.parse.output}")
    String outputPath;

    DynamicParseJson dynamicParseJson;
    ObjectMapper objectMapper;
    WebClient.Builder builder;

    public Dynamic(
            WebClient.Builder builder,
            DynamicParseJson dynamicParseJson,
            ObjectMapper objectMapper
    )
    {
        this.builder = builder.codecs(clientCodecConfigurer -> clientCodecConfigurer
                .defaultCodecs()
                .maxInMemorySize(16 * 1024 * 1024));
        this.dynamicParseJson = dynamicParseJson;
        this.objectMapper = objectMapper;
    }

    public void writeClassFromString(HttpHeaders headers, APIDataService<? extends Asset> dataService, String name, String url)
    {
        builder.baseUrl(url)
                .build()
                .get()
                .headers(http -> dataService.getHeaders())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(str -> {
                    System.out.println(str);
                    writeClassFromString(
                            str,
                            name
                    );
                });
    }

    public void writeClassFromString(
            String data,
            String name
    )
    {
        try {
            dynamicParseJson.dynamicParse(data, name, Optional.empty(), Optional.of(outputPath));
        } catch (DynamicParsingException e) {
            e.printStackTrace();
        }
    }

    public Mono<Optional<DynamicParseJson.ClassInfo>> writeClass(
            String url,
            String name
    )
    {
        return builder.baseUrl(url)
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        return Mono.just(dynamicParseJson.dynamicParse(response, name, Optional.empty(),
                                                                       Optional.of(outputPath)
                        ));
                    } catch (DynamicParsingException e) {
                        e.printStackTrace();
                    }
                    return Mono.empty();
                });
    }
}

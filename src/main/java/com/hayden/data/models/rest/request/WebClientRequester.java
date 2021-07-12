package com.hayden.data.models.rest.request;

import com.hayden.data.models.rest.WebClientResponseSpec;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.response.AbstractSpec;
import com.hayden.data.models.shared.response.ExchangeRequest;
import com.hayden.data.models.shared.response.Requester;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Data
public class WebClientRequester implements Requester {

    WebClient.Builder builder;

    public WebClientRequester(WebClient.Builder builder)
    {
        this.builder = builder.codecs(clientCodecConfigurer -> clientCodecConfigurer
                .defaultCodecs()
                .maxInMemorySize(16 * 1024 * 1024));
    }

    @Override
    public AbstractSpec retrieve(Url url, ExchangeRequest request)
    {
        return retrieve(url);
    }

    public AbstractSpec retrieve(Url urlParam)
    {
        return new WebClientResponseSpec(builder.baseUrl(urlParam.build())
                .build()
                .get()
                .headers(http -> addHeaders(urlParam.getHeaders(), http))
                .retrieve());
    }

    public void addHeaders(
            HttpHeaders headers,
            HttpHeaders http
    )
    {
        http.addAll(headers != null
                            ? headers
                            : new HttpHeaders()
        );
    }

}

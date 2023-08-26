package org.danuja25.cinesurfer.service;

import org.danuja25.cinesurfer.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.danuja25.cinesurfer.MDBListConstants.*;

@Service
public class ImdbService {

    @Autowired
    private Configuration configuration;

    public Mono<String> searchByPhrase(String phrase) {
        return getWebClient().get().uri(uriBuilder -> uriBuilder.path("/").queryParam("s", phrase).build()).retrieve().bodyToMono(String.class);
    }

    private WebClient getWebClient() {
        return WebClient.builder().baseUrl(BASE_URI).defaultHeaders(httpHeaders -> {
            httpHeaders.add("X-RapidAPI-Key", configuration.getRapidApiHost());
            httpHeaders.add("X-RapidAPI-Host", configuration.getRapidApiKey());
        }).build();
    }
}

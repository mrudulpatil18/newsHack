package com.newslatest;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.ExchangeStrategies;

public class GuardianClient {

    private final WebClient webClient;
    private final String apiKey;

    public GuardianClient(String apiKey) {
        this.apiKey = apiKey;

        // Configure to buffer up to 10 MB
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();

        this.webClient = WebClient.builder()
                .baseUrl("https://content.guardianapis.com")
                .exchangeStrategies(strategies)
                .build();
    }

    public Mono<Model.GuardianResponse> fetchArticles(String fromDate, String toDate) {
        return webClient.get()
                .uri(uri -> uri
                        .path("/search")
                        .queryParam("from-date", fromDate)
                        .queryParam("to-date", toDate)
                        .queryParam("order-by", "newest")
                        .queryParam("page-size", "200")  // You can experiment with 50/100/200
                        .queryParam("show-fields", "bodyText")
                        .queryParam("show-tags", "keyword,contributor")
                        .queryParam("api-key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(Model.GuardianResponse.class);
    }

    public Mono<Model.GuardianResponse> fetchArticlesPaged(String fromDate, String toDate, int page) {
        return webClient.get()
                .uri(uri -> uri
                        .path("/search")
                        .queryParam("from-date", fromDate)
                        .queryParam("to-date", toDate)
                        .queryParam("order-by", "newest")
                        .queryParam("page-size", "200")
                        .queryParam("page", page)
                        .queryParam("show-fields", "bodyText")
                        .queryParam("show-tags", "keyword,contributor")
                        .queryParam("api-key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(Model.GuardianResponse.class);
    }

}

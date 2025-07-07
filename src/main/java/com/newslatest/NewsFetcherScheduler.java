package com.newslatest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newslatest.Model.GuardianResponse;
import com.newslatest.Model.Result;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class NewsFetcherScheduler {

    private final GuardianClient guardianClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NewsFetcherScheduler() {
        this.guardianClient = new GuardianClient("f1b1a09b-b4b0-4a8a-9125-c64d71755ae2");
    }

    @PostConstruct
    public void onStartup() {
        String toDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        File outputFile = new File("guardian_articles_" + toDate + ".json");

        if (!outputFile.exists()) {
            System.out.println("🚀 App started, no existing data found. Fetching articles...");
            fetchAndStoreArticles();
        } else {
            System.out.println("🗃️ Data file already exists for today: " + outputFile.getName() + " — skipping startup fetch.");
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Runs at 12:00 AM everyday
    public void scheduledFetch() {
        System.out.println("⏰ Scheduled fetch triggered...");
        fetchAndStoreArticles();
    }

    private void fetchAndStoreArticles() {
        String fromDate = LocalDate.now().minusDays(7).format(DateTimeFormatter.ISO_DATE);
        String toDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        System.out.println("📅 Fetching articles from " + fromDate + " to " + toDate);

        int page = 1;
        int totalPages = 1;
        List<SimplifiedArticle> simplifiedArticles = new ArrayList<>();

        do {
            System.out.println("➡️ Fetching page " + page + "...");
            Mono<GuardianResponse> responseMono = guardianClient.fetchArticlesPaged(fromDate, toDate, page);
            GuardianResponse response = responseMono.block();

            if (response != null && response.getResponse() != null) {
                totalPages = response.getResponse().getPages();
                Result[] results = response.getResponse().getResults();

                if (results != null) {
                    for (Result result : results) {
                        Set<String> keywordSet = new HashSet<>();

                        if (result.getTags() != null) {
                            for (Model.Tag tag : result.getTags()) {
                                if ("keyword".equals(tag.getType())) {
                                    String[] parts = tag.getId().split("/");
                                    for (String part : parts) {
                                        if (!part.isBlank()) {
                                            keywordSet.add(part.trim().toLowerCase());
                                        }
                                    }
                                }
                            }
                        }

                        SimplifiedArticle article = new SimplifiedArticle(
                                result.getId(),
                                result.getWebTitle(),
                                result.getWebUrl(),
                                result.getFields() != null ? result.getFields().getBodyText() : "",
                                new ArrayList<>(keywordSet)
                        );

                        simplifiedArticles.add(article);
                    }

                    System.out.println("✅ Fetched " + results.length + " articles on page " + page);
                }
            } else {
                System.out.println("⚠️ No response or empty response on page " + page);
            }

            page++;
        } while (page <= totalPages);

        try {
            File outputFile = new File("guardian_articles_" + toDate + ".json");
            objectMapper.writeValue(outputFile, simplifiedArticles);
            System.out.println("📦 Saved " + simplifiedArticles.size() + " simplified articles to " + outputFile.getName());
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

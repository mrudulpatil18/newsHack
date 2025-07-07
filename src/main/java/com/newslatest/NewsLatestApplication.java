package com.newslatest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewsLatestApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsLatestApplication.class, args);

        GuardianClient client = new GuardianClient("f1b1a09b-b4b0-4a8a-9125-c64d71755ae2"); // or your real API key
        Model.GuardianResponse resp = client.fetchArticles("2025-06-30", "2025-07-06")
                .block(); // blocking for simplicity in this hackathon

        assert resp != null;
        Model.Result[] articles = resp.getResponse().getResults();
        System.out.println(articles.length);

    }

}

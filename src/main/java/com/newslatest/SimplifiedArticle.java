package com.newslatest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SimplifiedArticle {
    private String id;
    private String webTitle;
    private String webUrl;
    private String bodyText;
    private List<String> tags; // Cleaned keyword IDs
}


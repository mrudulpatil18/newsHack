package com.newslatest;

import lombok.Data;

public class Model {
    // Top-level response model
    @Data
    public static class GuardianResponse {
        private Response response;
    }

    @Data
    public static class Response {
        private Result[] results;
        private int pages;  // This is the total number of pages
        private int currentPage;
        private int total;
        private int pageSize;
    }

    @Data
    public static class Result {
        private String id;
        private String webTitle;
        private String webUrl;
        private String webPublicationDate;
        private Fields fields;
        private Tag[] tags;
    }

    @Data
    public static class Fields {
        private String bodyText;
    }

    @Data
    public static class Tag {
        private String id;
        private String type;
        private String webTitle;
    }
}

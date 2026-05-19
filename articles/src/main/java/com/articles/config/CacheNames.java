package com.articles.config;

public final class CacheNames {

    private CacheNames() {
    }

    public static final String ARTICLES_LIST = "articles:list";
    public static final String ARTICLE_BY_ID = "articles:by-id";
    public static final String SEARCH_BY_TITLE = "articles:search:title";
    public static final String SEARCH_BY_SEMANTIC = "articles:search:semantic";
    public static final String RECOMMENDATIONS_ANON = "articles:recommendations:anon";
}

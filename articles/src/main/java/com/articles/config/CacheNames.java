package com.articles.config;

/**
 * Defines cache names.
 */
public final class CacheNames {

/**
 * Performs cache names.
 * @return the result of the operation
 */
    private CacheNames() {
    }

    public static final String ARTICLES_LIST = "articles:list";
    public static final String ARTICLE_BY_ID = "articles:by-id";
    public static final String SEARCH_BY_TITLE = "articles:search:title";
    public static final String SEARCH_BY_SEMANTIC = "articles:search:semantic";
    public static final String RECOMMENDATIONS_ANON = "articles:recommendations:anon";
}

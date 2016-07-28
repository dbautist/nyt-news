package com.codepath.nytnews.utils;


public class AppConstants {
  public static final String NYT_API_KEY = "97fef03e1639426fb860d1ad4fb4778a";
  public static final String NYT_ARTICLE_SEARCH_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
  public static final String NYT_THUMBNAIL_PREFIX_URL = "http://www.nytimes.com/";

  // NYTIMES QUERY PARAMS KEY
  public static final String QUERY_PARAM = "q";
  public static final String BEGIN_DATE_PARAM = "begin_date";
  public static final String SORT_PARAM = "sort";
  public static final String FILTER_QUERY_PARAM = "fq";
  public static final String NEWS_DESK_VAL = "news_desk:(%s)";

  // INTENT/BUNDLE EXTRAS
  public static final String ARTICLE_EXTRA = "ARTICLE_EXTRA";
  public static final String FILTER_SETTINGS_EXTRA = "FILTER_SETTINGS_EXTRA";
}

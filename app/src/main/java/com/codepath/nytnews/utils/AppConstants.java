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

  // ERROR MESSAGES
  public static final String DEFAULT_ERROR_MESSAGE = "Oops, something went wrong. Please restart the app.";
  public static final String NO_CONNECTION_ERROR_MESSAGE = "Oops, looks like you're not connected to the internet. Please try again later.";

  // INTENT/BUNDLE EXTRAS
  public static final String ARTICLE_EXTRA = "ARTICLE_EXTRA";
  public static final String FILTER_SETTINGS_EXTRA = "FILTER_SETTINGS_EXTRA";
  public static final String YEAR_EXTRA = "YEAR_EXTRA";
  public static final String MONTH_EXTRA = "MONTH_EXTRA";
  public static final String DAY_EXTRA = "DAY_EXTRA";
}

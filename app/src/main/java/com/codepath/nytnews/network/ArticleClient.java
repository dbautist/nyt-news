package com.codepath.nytnews.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.codepath.nytnews.R;
import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.models.JSONSerializable;
import com.codepath.nytnews.utils.AppConstants;
import com.codepath.nytnews.utils.errors.ErrorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleClient {
  private static final String TAG = ArticleClient.class.getSimpleName();

  public interface ArticleCatalogListener<T extends JSONSerializable> {
    void onRequestSuccess(List<T> requestList);

    void onRequestFailure(String errorMessage, boolean isUserError);
  }

  public ArticleClient() { }

  // https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=97fef03e1639426fb860d1ad4fb4778a
  public void getArticleList(Map<String, String> paramsMap, int page, final ArticleCatalogListener listener) {
    if (!NetworkUtil.isOnline()) {
      if (listener != null) {
        listener.onRequestFailure(AppConstants.NO_CONNECTION_ERROR_MESSAGE, true);
      }
      return;
    }

    final String url = AppConstants.NYT_ARTICLE_SEARCH_URL;
    HttpService httpService = new HttpService(new HttpService.HttpResponseListener() {
      @Override
      public void onRequestFinished(HttpResponse response) {
        if (response != null) {
          if (response.getStatus() == HttpResponse.Status.Success) {
            try {
              JSONObject jsonObject = new JSONObject(response.getResponse());
              JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("docs");
              JSONDeserializer<Article> deserializer = new JSONDeserializer<>(Article.class);
              List<Article> articleList = deserializer.fromJSONArrayToList(jsonArray);
              if (listener != null) {
                listener.onRequestSuccess(articleList);
              }
            } catch (JSONException e) {
              String errorMessage = "Error encountered when fetching the article list";
              ErrorHandler.handleAppException(e, "");
              onRequestFailed(listener, errorMessage, false);
            }
          } else {
            ErrorHandler.logAppError("Request failed for url=" + url);
            onRequestFailed(listener, AppConstants.DEFAULT_ERROR_MESSAGE, true);
          }
        } else {
          String errorMessage = "Unexpected error encountered when fetching the article list";
          ErrorHandler.logAppError(errorMessage);
          onRequestFailed(listener, errorMessage, true);
        }
      }
    });

    Map<String, String> requestParams = new HashMap<>();
    requestParams.put("api-key", AppConstants.NYT_API_KEY);
    requestParams.put("page", Integer.toString(page));
    Log.d(TAG, "page="+page);

    if (paramsMap != null) {
      for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        Log.d(TAG, "key=" + key + ";value=" + value);
        if (!TextUtils.isEmpty(value)) {
          requestParams.put(key, value);
        }
      }
    }

    httpService.getResponse(url, requestParams);
  }

  private void onRequestFailed(ArticleCatalogListener listener, String errorMessage, boolean shouldDisplayToUser) {
    if (listener != null) {
      listener.onRequestFailure(errorMessage, shouldDisplayToUser);
    }
  }
}

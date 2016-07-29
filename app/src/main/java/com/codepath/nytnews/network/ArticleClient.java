package com.codepath.nytnews.network;

import android.text.TextUtils;
import android.util.Log;

import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.models.JSONSerializable;
import com.codepath.nytnews.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ArticleClient {
  private static final String TAG = ArticleClient.class.getSimpleName();

  public interface ArticleCatalogListener<T extends JSONSerializable> {
    void onRequestSuccess(List<T> requestList);

    void onRequestFailure();
  }

  // https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=97fef03e1639426fb860d1ad4fb4778a
  public void getArticleList(Map<String, String> paramsMap, int page, final ArticleCatalogListener listener) {
    HttpService httpService = new HttpService(new HttpService.HttpResponseListener() {
      @Override
      public void onRequestFinished(HttpResponse response) {
        if (response != null && response.getStatus() == HttpResponse.Status.Success) {
          try {
            JSONObject jsonObject = new JSONObject(response.getResponse());
            JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("docs");
            JSONDeserializer<Article> deserializer = new JSONDeserializer<>(Article.class);
            List<Article> articleList = deserializer.fromJSONArrayToList(jsonArray);
            if (listener != null) {
              listener.onRequestSuccess(articleList);
            }
          } catch (JSONException e) {
            Log.d(TAG, "JSONException: Error encountered when fetching the now playing movie list");
            onRequestFailed(listener);
          }
        } else {
          Log.d(TAG, "Error encountered when fetching the now playing movie list");
          onRequestFailed(listener);
        }
      }
    });

    String url = AppConstants.NYT_ARTICLE_SEARCH_URL;
    Map<String, String> requestParams = new HashMap<>();
    requestParams.put("api-key", AppConstants.NYT_API_KEY);
    requestParams.put("page", Integer.toString(page));

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

  private void onRequestFailed(ArticleCatalogListener listener) {
    if (listener != null) {
      listener.onRequestFailure();
    }
  }
}

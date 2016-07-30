package com.codepath.nytnews.models;

import android.text.TextUtils;

import com.codepath.nytnews.R;
import com.codepath.nytnews.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Article implements JSONSerializable {
  public String webUrl;
  public String headline;
  public String thumbNail;
  public String snippet;
  public String materialType;
  public boolean isArticlePreview;
  public String newsDesk;
  public int colorId;

  // empty constructor needed by the Parceler library
  public Article() {
  }


  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    webUrl = jsonObject.getString("web_url");
    headline = jsonObject.getJSONObject("headline").getString("main");
    snippet = jsonObject.getString("snippet");
    if (TextUtils.isEmpty(snippet) || snippet.equals("null")) {
      snippet = "";
    }

    materialType = jsonObject.getString("type_of_material");
    if (!TextUtils.isEmpty(materialType) && materialType.equalsIgnoreCase("article")) {
      // clicking on the item only shows the preview of the article; needs NYT subsription to be able to read it
      isArticlePreview = true;
    }

    setThumbNail(jsonObject);
    setNewsDesk(jsonObject);
    setColorId();
  }

  private void setThumbNail(JSONObject jsonObject) throws JSONException {
    thumbNail = "";

    JSONArray multimedia = jsonObject.getJSONArray("multimedia");
    if (multimedia.length() > 0) {
      JSONObject multimediaJson = null;
      for (int i = 0; i < multimedia.length(); i++) {
        multimediaJson = multimedia.getJSONObject(i);
        String subType = multimediaJson.getString("subtype");
        if (subType.equals("thumbnail")) {
          thumbNail = AppConstants.NYT_THUMBNAIL_PREFIX_URL + multimediaJson.getString("url");
          break;
        }
      }
      if (TextUtils.isEmpty(thumbNail)) {
        // no thumbnail specific image; default to the first media available
        multimediaJson = multimedia.getJSONObject(0);
        thumbNail = AppConstants.NYT_THUMBNAIL_PREFIX_URL + multimediaJson.getString("url");
      }
    }
  }

  private void setNewsDesk(JSONObject jsonObject) throws JSONException {
    newsDesk = jsonObject.getString("news_desk");
    if (newsDesk.equalsIgnoreCase("null")) {
      // clean up some data
      newsDesk = "";
    }
  }

  // assign a colorId for the defined news_desk or if it's a preview
  private void setColorId() {
    colorId = R.color.accent;
    if (isArticlePreview) {
      colorId = R.color.paid_subscription;
    } else {
      if (!TextUtils.isEmpty(newsDesk)) {
        if (newsDesk.equalsIgnoreCase("arts")) {
          colorId = R.color.news_desk_art;
        } else if (newsDesk.equalsIgnoreCase("sports")) {
          colorId = R.color.news_desk_sports;
        } else if (newsDesk.equalsIgnoreCase("fashion & style")) {
          colorId = R.color.news_desk_fashion;
        }
      }
    }
  }
}

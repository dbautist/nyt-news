package com.codepath.nytnews.models;

import android.text.TextUtils;

import com.codepath.nytnews.R;
import com.codepath.nytnews.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.w3c.dom.Text;

@Parcel
public class Article implements JSONSerializable {
  public String webUrl;
  public String headline;
  public String thumbNail;
  public String snippet;
  public String newsDesk;
  public int newsDeskColorId;

  // empty constructor needed by the Parceler library
  public Article() {
  }


  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    this.webUrl = jsonObject.getString("web_url");
    this.headline = jsonObject.getJSONObject("headline").getString("main");
    this.snippet = jsonObject.getString("snippet");

    setThumbNail(jsonObject);
    setNewsDesk(jsonObject);
    setNewsDeskColorId();
  }

  private void setThumbNail(JSONObject jsonObject) throws JSONException {
    this.thumbNail = "";

    JSONArray multimedia = jsonObject.getJSONArray("multimedia");
    if (multimedia.length() > 0) {
      JSONObject multimediaJson = null;
      for (int i = 0; i < multimedia.length(); i++) {
        multimediaJson = multimedia.getJSONObject(i);
        String subType = multimediaJson.getString("subtype");
        if (subType.equals("thumbnail")) {
          this.thumbNail = AppConstants.NYT_THUMBNAIL_PREFIX_URL + multimediaJson.getString("url");
          break;
        }
      }
      if (TextUtils.isEmpty(this.thumbNail)) {
        // no thumbnail specific image; default to the first media available
        multimediaJson = multimedia.getJSONObject(0);
        this.thumbNail = AppConstants.NYT_THUMBNAIL_PREFIX_URL + multimediaJson.getString("url");
      }
    }
  }

  private void setNewsDesk(JSONObject jsonObject) throws JSONException {
    this.newsDesk = jsonObject.getString("news_desk");
    if (this.newsDesk.equalsIgnoreCase("null")) {
      // clean up some data
      this.newsDesk = "";
    }
  }

  private void setNewsDeskColorId() {
    this.newsDeskColorId = R.color.news_desk_default;
    if (!TextUtils.isEmpty(this.newsDesk)) {
      if (this.newsDesk.equalsIgnoreCase("arts")) {
        this.newsDeskColorId = R.color.news_desk_art;
      } else if (this.newsDesk.equalsIgnoreCase("sports")) {
        this.newsDeskColorId = R.color.news_desk_sports;
      } else if (this.newsDesk.equalsIgnoreCase("fashion & style")) {
        this.newsDeskColorId = R.color.news_desk_fashion;
      }
    }
  }
}

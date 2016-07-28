package com.codepath.nytnews.models;

import com.codepath.nytnews.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Article implements JSONSerializable{
  public String webUrl;
  public String headline;
  public String thumbNail;

  // empty constructor needed by the Parceler library
  public Article() {
  }


  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    this.webUrl = jsonObject.getString("web_url");
    this.headline = jsonObject.getJSONObject("headline").getString("main");

    JSONArray multimedia = jsonObject.getJSONArray("multimedia");
    if(multimedia.length() > 0) {
      JSONObject multimediaJson = multimedia.getJSONObject(0);
      this.thumbNail = AppConstants.NYT_THUMBNAIL_PREFIX_URL + multimediaJson.getString("url");
    } else {
      this.thumbNail = "";
    }
  }

}

package com.codepath.nytnews.network;

import android.util.Log;

import com.codepath.nytnews.models.JSONSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONDeserializer <T extends JSONSerializable> {
  private static final String TAG = JSONDeserializer.class.getName();
  private Class<T> mClazz;

  public JSONDeserializer(Class<T> clazz) {
    this.mClazz = clazz;
  }

  public List<T> fromJSONArrayToList(JSONArray jsonArray) throws JSONException {
    if (jsonArray == null)
      return null;

    List<T> list = new ArrayList<T>();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObj = jsonArray.getJSONObject(i);
      T obj = configureJSONObject(jsonObj);
      if (obj != null)
        list.add(obj);
    }

    return list;
  }

  private T configureJSONObject(JSONObject json) {
    try {
      if(json == null)
        return null;

      T obj = mClazz.newInstance();
      obj.configureFromJSON(json);
      return obj;
    } catch (InstantiationException e) {
      Log.e(TAG, "InstantiationException", e);
    } catch (IllegalAccessException e) {
      Log.e(TAG, "IllegalAccessException", e);
    } catch (JSONException e) {
      Log.e(TAG, "JSONException", e);
    }

    return null;
  }
}
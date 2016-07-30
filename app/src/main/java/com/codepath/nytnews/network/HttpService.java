package com.codepath.nytnews.network;

import android.util.Log;

import com.codepath.nytnews.utils.AppConstants;
import com.codepath.nytnews.utils.errors.ErrorHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class HttpService {
  private static final String TAG = HttpService.class.getSimpleName();

  public interface HttpResponseListener {
    void onRequestFinished(HttpResponse response);
  }

  private HttpResponseListener listener;

  public HttpService(HttpResponseListener listener) {
    this.listener = listener;
  }

  public void getResponse(String url, Map<String, String> paramsList) {
    Log.d(TAG, "GET request to: " + url);

    AsyncHttpClient client = new AsyncHttpClient();
    client.get(url, new RequestParams(paramsList), new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        if (response == null) {
          fireFailureResponse(0, "Response is NULL");
        } else {
          fireSuccessResponse(response.toString());
        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        fireFailureResponse(statusCode, responseString);
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        String errorMessage = "Unexpected failure";
        if (errorResponse != null) {
          errorMessage = errorResponse.toString();
        }
        fireFailureResponse(statusCode, errorMessage);
      }
    });
  }

  private void fireSuccessResponse(String responseStr) {
    Log.d(TAG, "fireSuccessResponse: " + responseStr);
    if (listener != null) {
      HttpResponse response = new HttpResponse();
      response.setStatus(HttpResponse.Status.Success);
      response.setStatusCode(200);
      response.setResponse(responseStr);
      listener.onRequestFinished(response);
    }
  }

  private void fireFailureResponse(int statusCode, String failureStr) {
    ErrorHandler.logAppError("StatusCode=" + statusCode + "; ErrorMessage=" + failureStr);

    if (listener != null) {
      HttpResponse response = new HttpResponse();
      response.setStatus(HttpResponse.Status.Failed);
      response.setStatusCode(statusCode);
      response.setFailureMessage(failureStr);
      listener.onRequestFinished(response);
    }
  }
}

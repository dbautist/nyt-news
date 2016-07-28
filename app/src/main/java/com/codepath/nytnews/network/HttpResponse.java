package com.codepath.nytnews.network;

public class HttpResponse {
  public enum Status {
    Success,
    Failed
  }

  private Status status;
  private String response;
  private String failureMessage;

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public String getFailureMessage() {
    return failureMessage;
  }

  public void setFailureMessage(String failureMessage) {
    this.failureMessage = failureMessage;
  }
}

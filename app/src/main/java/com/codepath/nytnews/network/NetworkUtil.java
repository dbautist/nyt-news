package com.codepath.nytnews.network;

import com.codepath.nytnews.utils.errors.ErrorHandler;

import java.io.IOException;

public class NetworkUtil {
  public static boolean isOnline() {
    Runtime runtime = Runtime.getRuntime();
    try {
      Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
      int exitValue = ipProcess.waitFor();
      return (exitValue == 0);
    } catch (IOException e) {
      ErrorHandler.handleAppException(e, "NetworkUtil::isOnLine()");
    } catch (InterruptedException e) {
      ErrorHandler.handleAppException(e, "NetworkUtil::isOnLine()");
    }
    return false;
  }
}

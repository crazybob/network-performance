// Copyright 2011 Square, Inc.
package org.crazybob.networkperformance;

import android.util.Log;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/** @author Bob Lee (bob@squareup.com) */
public class PingLog {

  static void logStarted() {
    log("STARTED");
  }

  static void logConnected(long elapsed) {
    log("CONNECTED " + elapsed);
  }

  static void logConnectionError(long elapsed) {
    log("CONNECTION_ERROR " + elapsed);
  }

  static void logResponseTime(long elapsed) {
    log("RESPONSE_TIME " + elapsed);
  }

  static void logIoError(long elapsed) {
    log("IO_ERROR " + elapsed);
  }

  static synchronized void stop() {
    if (out != null) {
      try {
        log("STOPPED");
        out.flush();
        out.close();
        out = null;
      } catch (IOException e) {
        Log.w("PingLog", "Failed to flush log: " + e);
      }
    }
  }

  private static Writer out;

  private static synchronized void log(String message) {
    Log.i("PingLog", message);

    if (out == null) {
      try {
        out = new FileWriter("/sdcard/ping.log", true);
      } catch (IOException e) {
        Log.w("PingLog", "Failed to open log: " + e);
        return;
      }
    }

    try {
      out.write(System.currentTimeMillis() + " " + message + "\n");
    } catch (IOException e) {
      Log.w("PingLog", "Error writing to log: " + e);
      out = null;
    }
  }
}

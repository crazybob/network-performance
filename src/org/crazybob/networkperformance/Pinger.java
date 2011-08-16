// Copyright 2011 Square, Inc.
package org.crazybob.networkperformance;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/** @author Bob Lee (bob@squareup.com) */
public class Pinger extends Service implements Runnable {

  private static int TIMEOUT = 10000;

  @Override public IBinder onBind(Intent intent) { return null; }

  private final AtomicBoolean running = new AtomicBoolean();
  private final Thread thread = new Thread(this);
  private PowerManager.WakeLock wakeLock;

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i("Pinger", "Started " + this);
    if (!running.getAndSet(true)) {
      PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Pinger");
      wakeLock.acquire();
      thread.start();
      PingLog.logStarted();
    }
    return START_STICKY;
  }

  @Override public void onDestroy() {
    Log.i("Pinger", "Stopped " + this);
    running.set(false);
    thread.interrupt();
    Socket socket = this.socket;
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {}
    }
    PingLog.stop();
    wakeLock.release();
    super.onDestroy();
  }

  public void run() {
    while (running.get()) {
      ping();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) { /* Ignore */ }
    }
  }

  private volatile Socket socket;

  private void ping() {
    if (socket == null) {
      long start = System.currentTimeMillis();
      try {
        socket = new Socket();
        socket.setSoTimeout(TIMEOUT);
        socket.connect(new InetSocketAddress("74.122.184.244", 80), TIMEOUT);
        PingLog.logConnected(System.currentTimeMillis() - start);
      } catch (IOException e) {
        socket = null;
        Log.w("Pinger", "Error connectng.", e);
        PingLog.logConnectionError(System.currentTimeMillis() - start);
        return;
      }
    }

    long start = System.currentTimeMillis();
    try {
      PingSquare.ping(socket.getOutputStream(), socket.getInputStream());
      PingLog.logResponseTime(System.currentTimeMillis() - start);
    } catch (IOException e) {
      try {
        socket.close();
      } catch (IOException e1) {}
      socket = null;
      if (running.get()) {
        Log.w("Pinger", "Error pinging.", e);
        PingLog.logIoError(System.currentTimeMillis() - start);
      }
    }
  }
}

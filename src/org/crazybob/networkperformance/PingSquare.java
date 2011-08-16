// Copyright 2011 Square, Inc.
package org.crazybob.networkperformance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

/**
 * Requests headers for www.squareup.com.
 *
 * @author Bob Lee (bob@squareup.com)
 */
public class PingSquare {

  private static final String ASCII = "US-ASCII";

  public static void ping(OutputStream out, InputStream in) throws IOException {
    Writer writer = new OutputStreamWriter(out, ASCII);
    writer.write("HEAD / HTTP/1.1\r\n"
        + "User-Agent: Bob Lee's network performance test\r\n"
        + "Host: www.squareup.com\r\n"
        + "\r\n");
    writer.flush();

    BufferedReader reader = new BufferedReader(new InputStreamReader(in, ASCII));
    for (String line; (line = reader.readLine()) != null;) if (line.equals("")) return;
  }

  public static void main(String[] args) throws IOException {
    Socket socket = new Socket("www.squareup.com", 80);
    ping(socket.getOutputStream(), socket.getInputStream());
    ping(socket.getOutputStream(), socket.getInputStream());
    socket.close();
  }
}

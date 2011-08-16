// Copyright 2011 Square, Inc.
package org.crazybob.networkperformance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** @author Bob Lee (bob@squareup.com) */
public class Parse {

  enum Type {
    STARTED,
    STOPPED,
    CONNECTED,
    CONNECTION_ERROR,
    IO_ERROR,
    RESPONSE_TIME
  }

  static class Record {
    final long timestamp;
    final Type type;
    final long elapsed;

    Record(String line) {
      String[] split = line.split(" ");
      timestamp = Long.parseLong(split[0]);
      type = Type.valueOf(split[1]);
      if (split.length == 3) {
        elapsed = Long.parseLong(split[2]);
      } else {
        elapsed = -1;
      }
    }
  }

  public static void main(String[] args) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader("/Users/crazybob/ping.log"));
    List<Record> records = new ArrayList<Record>();
    String line;
    while ((line = in.readLine()) != null) {
      records.add(new Record(line));
    }

    long max = Long.MIN_VALUE;
    long min = Long.MAX_VALUE;
    for (Record record : records) {
      if (record.type == Type.RESPONSE_TIME) {
        max = Math.max(max, record.elapsed);
        min = Math.min(min, record.elapsed);
      }
    }

    System.out.println("Max\t" + max);
    System.out.println("Min\t" + min);

    int[] buckets = new int[16];
    for (Record record : records) {
      if (record.type == Type.RESPONSE_TIME) {
        int bucket = (int) record.elapsed / 500;
        buckets[bucket]++;
      }
    }

    for (int i = 0; i < buckets.length; i++) {
      System.out.println(i * 500 + "-" + (((i + 1) * 500) - 1) + "\t" + buckets[i]);
    }
  }
}

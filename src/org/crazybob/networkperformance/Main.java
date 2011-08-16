package org.crazybob.networkperformance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Main extends Activity {
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LinearLayout ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);

    Button start = new Button(this);
    start.setText("Start");
    start.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startService(new Intent(Main.this, Pinger.class));
      }
    });

    Button stop = new Button(this);
    stop.setText("Stop");
    stop.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        stopService(new Intent(Main.this, Pinger.class));
      }
    });

    ll.addView(start);
    ll.addView(stop);

    setContentView(ll);
  }

}

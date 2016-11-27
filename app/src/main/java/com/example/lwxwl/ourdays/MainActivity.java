package com.example.lwxwl.ourdays;

import android.app.Activity;
import android.app.Notification;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final int SHOW_RESPONSE = 0;
    private EditText edt;
    private Button btn;
    private TextView rtv;

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    rtv.setText(response);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt = (EditText) findViewById(R.id.edt);
        btn = (Button) findViewById(R.id.btn);
        rtv = (TextView) findViewById(R.id.rtv);
        btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
            sendRequestWithHttpURLConnection();
            String inputText = edt.getText().toString();
            Toast.makeText(MainActivity.this, inputText, Toast.LENGTH_SHORT).show();
        }
    }


    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection huc = null;
                try {
                    URL url = new URL("https://api.douban.com/v2/book/search?q=" + edt.getText().toString());
                    huc = (HttpURLConnection) url.openConnection();
                    huc.setRequestMethod("GET");
                    InputStream ins = huc.getInputStream();
                    BufferedReader brd = new BufferedReader(new InputStreamReader(ins));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = brd.readLine()) != null) {
                        response.append(line);
                    }
                    Message msg = new Message();
                    msg.what = SHOW_RESPONSE;
                    msg.obj = response.toString();
                    handler.sendMessage(msg);
                    parseJSONWithJSONObject(edt.getText().toString());
                } catch (Exception exc) {
                    exc.printStackTrace();
                } finally {
                    if (huc != null) {
                        huc.disconnect();
                    }
                }
            }
        }).start();
    }


    public void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonQbject = jsonArray.getJSONObject(i);
                String count = jsonQbject.getString("count");
                String name = jsonQbject.getString("name");
                String title = jsonQbject.getString("title");
                Log.d("MainActivity", "count is " + count);
                Log.d("MainActivity", "name is " + name);
                Log.d("MainActivity", "title is " + title);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}










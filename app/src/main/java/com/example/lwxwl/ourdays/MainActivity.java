package com.example.lwxwl.ourdays;

import android.app.Activity;;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {


    public static final int SHOW_RESPONSE = 0;
    private EditText edt;
    private Button btn;
    private TextView rtv;
    private TextView rtv2;
    private List<Book> bookList  = new ArrayList<Book>();

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    rtv.setText(response);
                    parseJSONWithJSONObject(response.toString());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rtv = (TextView) findViewById(R.id.rtv);
        rtv2 = (TextView) findViewById(R.id.rtv2);
        edt = (EditText) findViewById(R.id.edt);
        btn = (Button) findViewById(R.id.btn);
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
            JSONObject object = new JSONObject(jsonData);
            int total = object.getInt("total");
            JSONArray array = object.getJSONArray("books");
            bookList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                Book book = new Book();
                book.title = ((JSONObject)array.get(i)).getString("title");
                book.summary = ((JSONObject)array.get(i)).getString("summary");
                bookList.add(book);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}










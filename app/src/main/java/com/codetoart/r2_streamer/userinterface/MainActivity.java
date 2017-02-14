package com.codetoart.r2_streamer.userinterface;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codetoart.r2_streamer.R;
import com.codetoart.r2_streamer.model.container.DirectoryContainer;
import com.codetoart.r2_streamer.model.container.EpubContainer;
import com.codetoart.r2_streamer.parser.EpubParserException;
import com.codetoart.r2_streamer.server.EpubServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String TAG = "MainActivity";
    private EpubServer mEpubServer;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> manifestItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.spineList);
        try {
            mEpubServer = new EpubServer();
            mEpubServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Server is running. Point your browser at http://localhost:8080/");
    }

    public void unzip(View view) throws IOException {
        String path = Environment.getExternalStorageDirectory().getPath();
        EpubContainer container = new EpubContainer(path + "/Download/demoFile.epub");
        container.rawData("chapter21.html");            //instead hardcoding path, use webview url path

        int size = container.rawDataSize("chapter21.html");
        Log.d(TAG, "Epub File Size: " + size);

        /*DirectoryContainer dc = new DirectoryContainer(path + "/Download/demoTest/");
        dc.rawData("chapter10.html");
        int size = dc.rawDataSize("chapter10.html");
        Log.d(TAG, "File Size: " + size);*/
    }

    public void test(View view) throws IOException, EpubParserException {
        String path = Environment.getExternalStorageDirectory().getPath();
        DirectoryContainer dc = new DirectoryContainer(path + "/Download/moby-dick/");
        //EpubContainer ec = new EpubContainer(path + "/Download/pageblanche.epub");
        mEpubServer.addEpub(dc, "/moby-dick");

        String urlString = "http://127.0.0.1:8080/moby-dick/spineHandle";
        new SpineListTask().execute(urlString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEpubServer != null && mEpubServer.isAlive()) {
            mEpubServer.stop();
        }

        Log.d(TAG, "Server has been stopped");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String urlString = "http://127.0.0.1:8080/moby-dick/" + manifestItemList.get(position);
        Uri uri = Uri.parse(urlString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    class SpineListTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... urls) {
            String strUrl = urls[0];

            try {
                URL url = new URL(strUrl);
                URLConnection conn = url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                return new JSONArray(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    manifestItemList.add(jsonObject.getString("href"));
                }
                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, manifestItemList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(MainActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
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
import android.widget.EditText;
import android.widget.ListView;

import com.codetoart.r2_streamer.R;
import com.codetoart.r2_streamer.adapters.ListAdapter;
import com.codetoart.r2_streamer.model.container.DirectoryContainer;
import com.codetoart.r2_streamer.model.container.EpubContainer;
import com.codetoart.r2_streamer.model.searcher.SearchResult;
import com.codetoart.r2_streamer.parser.EpubParserException;
import com.codetoart.r2_streamer.server.EpubServer;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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

import static com.codetoart.r2_streamer.util.Constants.HREF;
import static com.codetoart.r2_streamer.util.Constants.JSON_STRING;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String TAG = "MainActivity";
    private EpubServer mEpubServer;

    private EditText searchBar;
    private ListView listView;
    private List<String> manifestItemList = new ArrayList<>();
    private List<SearchResult> searchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBar = (EditText) findViewById(R.id.searchBar);
        listView = (ListView) findViewById(R.id.spineList);

        try {
            mEpubServer = new EpubServer();
            mEpubServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Server is running. Point your browser at http://localhost:8080/");
    }

    public void search(View view) throws IOException {
        String path = Environment.getExternalStorageDirectory().getPath();
        //DirectoryContainer directoryContainer = new DirectoryContainer(path + "/Download/moby-dick/");
        EpubContainer epubContainer = new EpubContainer(path + "/Download/TheSilverChair.epub");
        mEpubServer.addEpub(epubContainer, "/TheSilverChair.epub");

        searchList.clear();
        String searchQuery = searchBar.getText().toString();
        if (searchQuery.length() != 0) {
            String urlString = "http://127.0.0.1:8080/TheSilverChair.epub/query=" + searchQuery;
            new SearchListTask().execute(urlString);
        }
    }

    public void test(View view) throws IOException, EpubParserException {
        String path = Environment.getExternalStorageDirectory().getPath();
        //DirectoryContainer directoryContainer = new DirectoryContainer(path + "/Download/moby-dick/");
        EpubContainer epubContainer = new EpubContainer(path + "/Download/TheSilverChair.epub");
        mEpubServer.addEpub(epubContainer, "/TheSilverChair.epub");

        manifestItemList.clear();
        String urlString = "http://127.0.0.1:8080/TheSilverChair.epub/spineHandle";
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
        //String urlString = "http://127.0.0.1:8080/moby-dick/" + manifestItemList.get(position);
        String urlString = "http://127.0.0.1:8080/TheSilverChair.epub/" + searchList.get(position).getResource();
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
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return new JSONArray(stringBuilder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    manifestItemList.add(jsonObject.getString(HREF));
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, manifestItemList);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(MainActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class SearchListTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... urls) {
            String strUrl = urls[0];
            try {
                URL url = new URL(strUrl);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return new JSONArray(stringBuilder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    Object object = jsonObject.get(JSON_STRING);

                    ObjectMapper objectMapper = new ObjectMapper();
                    SearchResult searchResult = objectMapper.readValue(object.toString(), SearchResult.class);
                    searchList.add(searchResult);
                }
                ListAdapter adapter = new ListAdapter(MainActivity.this, searchList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(MainActivity.this);
            } catch (JSONException | JsonParseException | JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
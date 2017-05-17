package com.readium.sample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readium.r2_streamer.model.container.Container;
import com.readium.r2_streamer.model.container.EpubContainer;
import com.readium.r2_streamer.model.publication.EpubPublication;
import com.readium.r2_streamer.model.publication.link.Link;
import com.readium.r2_streamer.model.searcher.SearchQueryResults;
import com.readium.r2_streamer.model.searcher.SearchResult;
import com.readium.r2_streamer.server.EpubServer;
import com.readium.r2_streamer.server.EpubServerSingleton;
import com.readium.sample.adapters.SearchListAdapter;
import com.readium.sample.adapters.SpineListAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String EPUBTITLE = "TheSilverChair.epub";
    private static final String ROOT_EPUB_PATH = Environment.getExternalStorageDirectory().getPath() + "/R2StreamerSample/";
    private static final int WRITE_EXST = 100;
    private final String TAG = "TestActivity";
    private EpubServer mEpubServer;

    private int portNumber = 3000;

    private EditText searchBar;
    private ListView listView;
    private List<Link> manifestItemList = new ArrayList<>();
    private List<SearchResult> searchList = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading.... ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST);
        setContentView(R.layout.activity_sample_main);
        searchBar = (EditText) findViewById(R.id.searchField);
        listView = (ListView) findViewById(R.id.list);

        copyEpubFromAssetsToSdCard(EPUBTITLE);
        startServer();

        Log.d(TAG, "Server is running. Point your browser at http://localhost:" + portNumber + "/");
    }

    private void startServer() {
        try {
            mEpubServer = EpubServerSingleton.getEpubServerInstance(portNumber);
            mEpubServer.start();
        } catch (IOException e) {
            Log.e(TAG, "startServer IOException " + e.toString());
        }
    }

    public void find(View view) throws IOException {
        addEpub();
        searchList.clear();
        String searchQuery = searchBar.getText().toString();
        if (!searchQuery.isEmpty()) {
            progressDialog.show();
            if (searchQuery.contains(" ")) {
                searchQuery = searchQuery.replaceAll(" ", "%20");
            }
            if (searchQuery.length() != 0) {
                String urlString = "http://127.0.0.1:" + portNumber + "/" + EPUBTITLE + "/search?query=" + searchQuery;
                new SearchListTask().execute(urlString);
            }
        } else {
            searchBar.requestFocus();
            searchBar.setError("Enter search query");
        }
    }

    private void addEpub() throws IOException {
        String path = ROOT_EPUB_PATH + EPUBTITLE;
        //DirectoryContainer directoryContainer = new DirectoryContainer(path);
        Container epubContainer = new EpubContainer(path);
        mEpubServer.addEpub(epubContainer, "/" + EPUBTITLE);
    }

    public void show(View view) throws IOException {
        progressDialog.show();
        addEpub();
        manifestItemList.clear();
        String urlString = "http://127.0.0.1:" + portNumber + "/" + EPUBTITLE + "/manifest";
        new SpineListTask().execute(urlString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEpubServer != null && mEpubServer.isAlive()) {
            mEpubServer.stop();
            EpubServerSingleton.resetServerInstance();
        }

        Log.d(TAG, "Server has been stopped");
    }

    public void copyEpubFromAssetsToSdCard(String epubFileName) {
        try {
            File dir = new File(ROOT_EPUB_PATH);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, epubFileName);
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            InputStream fis = getAssets().open(EPUBTITLE);
            byte[] b = new byte[1024];
            int i;
            while ((i = fis.read(b)) != -1) {
                fos.write(b, 0, i);
            }
            fos.flush();
            fos.close();
            fis.close();
        } catch (IOException e) {
            Log.e(TAG, "copyEpubFromAssetsToSdCard IOException " + e.toString());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String urlString = "http://127.0.0.1:" + portNumber + "/" + EPUBTITLE + "/" + manifestItemList.get(position).getHref();
        //String urlString = "http://127.0.0.1:8080/BARRETT_GUIDE.epub/" + searchList.get(position).getResource();
        Uri uri = Uri.parse(urlString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    class SpineListTask extends AsyncTask<String, Void, EpubPublication> {
        @Override
        protected EpubPublication doInBackground(String... urls) {
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

                Log.d("TestActivity", "EpubPublication => " + stringBuilder.toString());

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                return objectMapper.readValue(stringBuilder.toString(), EpubPublication.class);
            } catch (IOException e) {
                Log.e(TAG, "SpineListTask error " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(EpubPublication epubPublication) {
            manifestItemList = epubPublication.spines;
            SpineListAdapter arrayAdapter = new SpineListAdapter(TestActivity.this, manifestItemList);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(TestActivity.this);
            cancel(true);
            progressDialog.dismiss();
        }
    }

    class SearchListTask extends AsyncTask<String, Void, SearchQueryResults> {

        @Override
        protected SearchQueryResults doInBackground(String... urls) {
            String strUrl = urls[0];
            try {
                URL url = new URL(strUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return objectMapper.readValue(stringBuilder.toString(), SearchQueryResults.class);
            } catch (IOException e) {
                Log.e(TAG, "SearchListTask IOException " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(SearchQueryResults results) {
            searchList = results.searchResultList;
            SearchListAdapter adapter = new SearchListAdapter(TestActivity.this, searchList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(TestActivity.this);
            cancel(true);
            progressDialog.dismiss();
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == WRITE_EXST) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
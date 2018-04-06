package com.example.shailesh.dashboard;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class NewsFeed extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    public JSONArray articlesobj;
    public Boolean fetched = false;

    private static final String TAG = "newsfeed";
    OnlineActivity o;

    public Article article;
    public static ArrayList<Article> articles;

    private ProgressBar progressBar;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        o = new OnlineActivity();
        o.execute("http://knogle.xyz/scraper/news.php");
        setContentView(R.layout.activity_newsfeed);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        text = (TextView) findViewById(R.id.pleaseWait);

        progressBar.setVisibility(View.VISIBLE);
        text.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

/**
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
*/
    private class OnlineActivity extends AsyncTask<String, String, JSONArray> {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        SharedPreferences sharedpreferences;
        private boolean running = true;

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            if (isConnected()) {
                try {
                    if (running) {
                        URL url = null;
                        try {
                            url = new URL(strings[0]);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                    }

                    if (running) {
                        InputStream stream = connection.getInputStream();

                        reader = new BufferedReader(new InputStreamReader(stream));

                        StringBuffer buffer = new StringBuffer();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                            Log.v("JSON Article", line);
                        }
                        try {
                            articlesobj = new JSONArray(buffer.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sharedpreferences = getSharedPreferences("MYPREFERENCES", 0);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("news", articlesobj.toString());
                        editor.apply();
                        return articlesobj;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray response) {
            try {
                progressBar.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                Gson gson = new Gson();
                Article a[] = gson.fromJson(response.toString(),Article[].class);
                articles = new ArrayList<>(Arrays.asList(a));
                adapter = new NewsAdapter(articles, getApplicationContext());
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

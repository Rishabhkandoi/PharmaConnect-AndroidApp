package com.example.shailesh.dashboard.DrugWiki;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.shailesh.dashboard.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchForNames extends AppCompatActivity implements SearchView.OnQueryTextListener{

    public final String url = "http://knogle.xyz/drugs/nameSender.php";
    final ArrayList<String> list = new ArrayList<>();
    String userNames[];
    ArrayAdapter<String> adapter;
    ListView listview;
    SearchView sv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugwiki);
        sv = (SearchView) findViewById(R.id.search_view);


        HttpGetRequest hgr = new HttpGetRequest();
        hgr.execute();
    }

    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listview.clearTextFilter();
        } else {
            listview.setFilterText(newText.toString());
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }



    public class HttpGetRequest extends AsyncTask<String, Void, String>  {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        @Override
        protected String doInBackground(String... params){
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                System.out.println("Entered doInBackground");
                URL myUrl = new URL(url);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                System.out.println("Connection is Ok !");
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null

                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                //Toast.makeText(getApplicationContext(),"Get Request executed Successfully !", Toast.LENGTH_SHORT).show();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
                //Toast.makeText(getApplicationContext(),"Sorry could not process the get request !", Toast.LENGTH_SHORT).show();
            }

            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //ed = (TextView) findViewById(R.id.textView2);
            //ed.setText("  ");
            //ed.setText(result);

            System.out.println(result);
            try {
                //JSONObject dummy = new JSONObject(result);
                JSONArray arr = new JSONArray(result);
                int count = 1;
                for (int i = 0; i < arr.length();i++, count++)
                {
                    JSONObject obj = arr.getJSONObject(i);

                    list.add(obj.getString("Brand_name"));

                }
                System.out.println(list);

                listview = (ListView)findViewById(R.id.listview3);
                userNames = list.toArray(new String[0]);

                adapter = new ArrayAdapter<>(SearchForNames.this,android.R.layout.simple_list_item_1,userNames);

                //final StableArrayAdapter adapter = new StableArrayAdapter(SearchForNames.this,
                //            android.R.layout.simple_list_item_1, list);

                listview.setAdapter(adapter);
                listview.setTextFilterEnabled(true);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        //System.out.println(position);
                        Intent i = new Intent(SearchForNames.this, ViewProfile.class);
                        //i.putExtra("position", position + 5);
                        i.putExtra("name",list.get(position));
                        startActivity(i);
                    }
                });






            }
            catch(JSONException e){
                e.printStackTrace();
                Toast.makeText(SearchForNames.this, "Oops !", Toast.LENGTH_LONG).show();
            }





        }
    }

}

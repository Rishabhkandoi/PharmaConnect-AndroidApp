package com.example.shailesh.dashboard.DrugWiki;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shailesh.dashboard.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class ViewProfile extends AppCompatActivity {

    public String SERVER_URL = "http://knogle.xyz/drugs/returnFullProfile.php";
    String name;
    TextView brand_name, generic_name, category, dosage_form, molecular_formula, uses, advantages, sideEffects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugwiki_view_profile);
        Intent i = getIntent();
        name = i.getStringExtra("name");
        //name = "renuka";
        SendData s = new SendData();
        s.execute(name);
    }

    public class SendData extends AsyncTask<String, Void, String> {

        HttpURLConnection client;


        @Override
        protected String doInBackground(String... params) {

            try {

                URL url = new URL(SERVER_URL);
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("name", name);
                //postDataParams.put("details", details);

                client = (HttpURLConnection) url.openConnection();
                client.setReadTimeout(15000 /* milliseconds */);
                client.setConnectTimeout(15000 /* milliseconds */);
                client.setRequestMethod("POST");
                client.setDoInput(true);
                client.setDoOutput(true);

                OutputStream os = client.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=client.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            client.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if(client != null) // Make sure the connection is not null.
                    client.disconnect();
            }
            return "Default Return Statement";
        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            brand_name = (TextView) findViewById(R.id.brand_name);
            generic_name = (TextView) findViewById(R.id.generic_name);
            category = (TextView) findViewById(R.id.category);
            dosage_form = (TextView) findViewById(R.id.dosage_form);
            molecular_formula = (TextView) findViewById(R.id.molecular_formula);
            uses = (TextView) findViewById(R.id.uses);
            advantages = (TextView) findViewById(R.id.advantages);
            sideEffects = (TextView) findViewById(R.id.sideEffects);

            try {
                //JSONObject dummy = new JSONObject(result);
                JSONArray arr = new JSONArray(result);
                int count = 1;
                for (int i = 0; i < arr.length();i++, count++)
                {
                    JSONObject obj = arr.getJSONObject(i);
                    brand_name.setText("Brand Name : " + obj.getString("Brand_name"));
                    generic_name.setText("Generic Name : " + obj.getString("Generic_name"));
                    category.setText("Category : " + obj.getString("Category"));
                    dosage_form.setText("Dosage Form : " + obj.getString("Dosage_Form"));
                    molecular_formula.setText("Molecular Formula : " + obj.getString("Molecular_Formula"));
                    uses.setText("Uses : " + obj.getString("Use"));
                    advantages.setText ("Advantages: " +obj.getString("advantages"));
                    sideEffects.setText ("Side Effects: " +obj.getString("side_effects"));

                }
            }
            catch(JSONException e){
                e.printStackTrace();
                Toast.makeText(ViewProfile.this, "Oops !", Toast.LENGTH_LONG).show();
            }

        }

    }


}

package com.example.shailesh.dashboard;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.shailesh.dashboard.Survey.SurveyMainActivity;

public class MainActivity extends AppCompatActivity {

    CardView aboutus,pharmacyLocator,drugWiki,newsFeed,myProfile,logout,survey,contactUs,policies;

    Intent i1,i2,i3,i4,i5,i6,i7,i8,i9;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll = (LinearLayout) findViewById(R.id.ll);


        aboutus = (CardView)findViewById(R.id.about_us);
        i1 = new Intent(this,AboutUs.class);
        aboutus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(i1);
            }
        });

        pharmacyLocator = (CardView)findViewById(R.id.pharmacy_locator);
        i2 = new Intent(this,com.example.shailesh.dashboard.PharmacyLocator.PharmacyLocator.class);
        pharmacyLocator.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(i2);
            }
        });

        drugWiki = (CardView)findViewById(R.id.drug_wiki);
        i3 = new Intent(this,com.example.shailesh.dashboard.DrugWiki.SearchForNames.class);
        drugWiki.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(i3);
            }
        });


        newsFeed = (CardView)findViewById(R.id.news_feed);
        i4 = new Intent(this,NewsFeed.class);
        newsFeed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(i4);
            }
        });


        myProfile = (CardView)findViewById(R.id.my_profile);
        i5 = new Intent(this,MyProfile.class);
        myProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(i5);
            }
        });

        logout = (CardView)findViewById(R.id.logout);
        i6 = new Intent(this,Logout.class);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(i6);
            }
        });

        survey = (CardView)findViewById(R.id.survey);
        i7 = new Intent(this,com.example.shailesh.dashboard.Survey.SurveyMainActivity.class);
        survey.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(i7);
            }
        });

        contactUs = (CardView)findViewById(R.id.contact_us);
        i8 = new Intent(this,ContactUs.class);
        contactUs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(i8);
            }
        });

        policies = (CardView)findViewById(R.id.policies);
        i9 = new Intent(this,Policies.class);
        policies.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(i9);
            }
        });

    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.nottification,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.notification) {
            Intent intent = new Intent(MainActivity.this, Notification.class);
            startActivity(intent);
            //finish();
        }

        return true;
    }
}
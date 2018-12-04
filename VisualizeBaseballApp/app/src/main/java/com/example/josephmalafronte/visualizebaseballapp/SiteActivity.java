package com.example.josephmalafronte.visualizebaseballapp;


//Import Android Content
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

//Import Firebase Content
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;



public class SiteActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();
    DatabaseReference siteRef;


    //ViewModes define what type of experience is currently happening
    // 1 = Tour
    int viewMode = 0;

    int siteNumber = 1;
    int numberOfSites = 100;
    String siteString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);


        //Make text not editable
        EditText mEdit = (EditText) findViewById(R.id.editText);
        mEdit.setEnabled(false);

        getSiteNumber();
    }

    //This function will get overwritten by the overarching activity.
    public void setMode() {
        viewMode = 0;
    }

    //Function that sets up activity for tour
    private void tourSetUp() {

        Button btnTopRight = findViewById(R.id.btnTopRight);
        if(numberOfSites == siteNumber){
            btnTopRight.setVisibility(View.INVISIBLE);
        }
        else{
            btnTopRight.setText("Next Site");
            btnTopRight.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    setSiteNumber(siteNumber+1);
                    resetActivity();
                }
            });
        }

        Button btnTopLeft = findViewById(R.id.btnTopLeft);
        if(siteNumber == 1){
            btnTopLeft.setVisibility(View.INVISIBLE);
        }
        else{
            btnTopLeft.setText("Previous Site");
            btnTopLeft.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    setSiteNumber(siteNumber-1);
                    resetActivity();
                }
            });
        }

    }

    public void afterSiteNumber(){
        setMode();

        if(viewMode == 1){
            tourSetUp();
        }


        siteString = "Site" + Integer.toString(siteNumber);

        siteRef = mDatabase.child("BaseballApp").child("Sites").child(siteString);


        //Set Title
        setTitle();
    }

    public void getSiteNumber(){
        DatabaseReference numRef = mDatabase.child("BaseballApp").child("Sites").child("CurrentSite");
        numRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                siteNumber = Integer.parseInt(dataSnapshot.getValue().toString());
                getTotalSites();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    public void getTotalSites() {
        DatabaseReference totalRef = mDatabase.child("BaseballApp").child("Sites").child("NumberOfSites");
        totalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberOfSites = Integer.parseInt(dataSnapshot.getValue().toString());
                afterSiteNumber();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void setSiteNumber(int num){
        DatabaseReference numRef = mDatabase.child("BaseballApp").child("Sites").child("CurrentSite");
        numRef.setValue(num);
    }

    public void resetActivity() {
        finish();
        startActivity(getIntent());
    }

    public void setTitle() {
        DatabaseReference refText = siteRef.child("Name");

        refText.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView tv1 = (TextView)findViewById(R.id.editText);
                tv1.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

}
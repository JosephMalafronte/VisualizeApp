package com.example.josephmalafronte.visualizebaseballapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import android.view.View.OnClickListener;

import android.widget.ImageView;

//Import Firebase Content
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    //Button[] buttons;
    final List<String> sites = new ArrayList<String>();
    int count = 0;
    int numberOfSites = 0;

    boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getNumSites();
    }

    public void getNumSites() {
        DatabaseReference totalRef = mDatabase.child("BaseballApp").child("Sites").child("NumberOfSites");
        totalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberOfSites = Integer.parseInt(dataSnapshot.getValue().toString());
                buildArray();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void buildArray () {
        int i = 1;
        for(i = 1; i<=numberOfSites; i++) {
            String siteString = "Site" + i;
            DatabaseReference ref = mDatabase.child("BaseballApp").child("Sites").child(siteString).child("Name");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String siteName = dataSnapshot.getValue().toString();
                    sites.add(siteName);
                    incCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }



    }

    public void incCount() {
        count++;
        if (count==numberOfSites){
            Spinner areaSpinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(MapActivity.this, android.R.layout.simple_spinner_item, sites);
            areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            areaSpinner.setAdapter(areasAdapter);

            areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                   if(firstTime == false){
                       String value = parent.getItemAtPosition(pos).toString();
                       getSiteString(value);
                   }
                   else firstTime = false;
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    public void getSiteString (final String siteName){
        int i = 1;
        for(i = 1; i<=numberOfSites; i++) {
            final String siteString = "Site" + i;
            DatabaseReference ref = mDatabase.child("BaseballApp").child("Sites").child(siteString).child("Name");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String siteNow = dataSnapshot.getValue().toString();
                    if(siteName.equals(siteNow)){
                        getSiteNumberAndExcute(siteString);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }

    public void getSiteNumberAndExcute(final String siteName){
        DatabaseReference ref = mDatabase.child("BaseballApp").child("Sites").child(siteName).child("SiteNumber");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int num = Integer.parseInt(dataSnapshot.getValue().toString());
                DatabaseReference numRef = mDatabase.child("BaseballApp").child("Sites").child("CurrentSite");
                numRef.setValue(num);
                startActivity(new Intent(MapActivity.this, ScanSiteActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void setImage() {


        DatabaseReference UpperLevel = mDatabase.child("BaseballApp");

        //Set Map Image
        DatabaseReference mapRef = UpperLevel.child("Map");
        mapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new DownloadImageTask((ImageView) findViewById(R.id.mapimage))
                        .execute(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    //function for downloading image by url from firebase
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}

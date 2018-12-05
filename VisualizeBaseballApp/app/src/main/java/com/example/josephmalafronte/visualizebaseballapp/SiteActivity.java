package com.example.josephmalafronte.visualizebaseballapp;


//Import Android Content

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

//Import Firebase Content
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;




public class SiteActivity extends YouTubeBaseActivity {

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
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



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

    //Function that sets up activity for scan
    private void scanSetUp() {

        Button btnTopRight = findViewById(R.id.btnTopRight);
        btnTopRight.setVisibility(View.INVISIBLE);


        Button btnTopLeft = findViewById(R.id.btnTopLeft);


        btnTopLeft.setText("Return To Scanner");
        btnTopLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(SiteActivity.this, ScanActivity.class));
            }
        });


    }

    public void afterSiteNumber(){
        setMode();

        if(viewMode == 1){
            tourSetUp();
        }
        else if(viewMode == 2){
            scanSetUp();
        }


        siteString = "Site" + Integer.toString(siteNumber);

        siteRef = mDatabase.child("BaseballApp").child("Sites").child(siteString);


        //Set Title
        setTitle();
        setVideo();
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

                //Error check
                if(siteNumber > numberOfSites || siteNumber < 1) {
                    siteNumber = 1;
                }

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


    public void setVideo() {
        DatabaseReference refText = siteRef.child("VideoId");

        refText.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String videoId = dataSnapshot.getValue().toString();
                final YouTubePlayerView youtubePlayerView = findViewById(R.id.youtubePlayerView);
                playVideo(videoId, youtubePlayerView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    public void playVideo(final String videoId, YouTubePlayerView youTubePlayerView) {
        //initialize youtube player view
        youTubePlayerView.initialize("AIzaSyAmTYxKiikRefIQBnyPYov6-eu45ngCW40",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(videoId);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

}
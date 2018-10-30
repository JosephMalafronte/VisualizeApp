package com.example.josephmalafronte.visualizebaseballapp;


//Import Android Content
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

//Import QR Scanning Content
//import com.google.android.gms.vision.barcode;

//Import Firebase Content
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;



public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setTitle();

        setImages();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //Test write to database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mDatabase = database.getReference();

                DatabaseReference test = mDatabase.child("TestData").child("Value1");

                test.setValue("Taco Tuesday");

                test.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String help = dataSnapshot.getValue().toString();
                        Log.d("mytag", help);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }

        });



        Button btnTour = (Button) findViewById(R.id.tourButton);

        btnTour.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TourActivity.class));
            }
        });

        Button btnScan = (Button) findViewById(R.id.scanButton);

        btnScan.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScanActivity.class));

                //Where we will start the Scan activity:
                //val intent = Intent(applicationContext, ScanActivity::class.java);
                //startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        Button btnMap = (Button) findViewById(R.id.mapButton);

        btnMap.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setTitle() {
        DatabaseReference refText = mDatabase.child("BaseballApp").child("Homepage").child("Title");

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



    public void setImages() {


        DatabaseReference UpperLevel = mDatabase.child("BaseballApp").child("Homepage").child("Images");

        //Set Left Image
        DatabaseReference leftRef = UpperLevel.child("LeftImage");
        leftRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new DownloadImageTask((ImageView) findViewById(R.id.image1))
                        .execute(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        //Set Middle Image
        DatabaseReference middleRef = UpperLevel.child("MiddleImage");
        middleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new DownloadImageTask((ImageView) findViewById(R.id.image2))
                        .execute(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        //Set Right Image
        DatabaseReference rightRef = UpperLevel.child("RightImage");
        rightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new DownloadImageTask((ImageView) findViewById(R.id.image3))
                        .execute(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }


    //Complex function for downloading image by url
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

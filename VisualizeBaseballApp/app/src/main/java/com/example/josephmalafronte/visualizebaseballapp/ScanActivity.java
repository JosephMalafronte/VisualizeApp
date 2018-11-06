package com.example.josephmalafronte.visualizebaseballapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//Import QR Scanning Content
import com.google.android.gms.vision.barcode.Barcode;
import barcodereadersample.barcode.BarcodeCaptureActivity;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
    }
}

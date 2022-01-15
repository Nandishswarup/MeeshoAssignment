package com.example.meeshoassignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;

import com.example.meeshoassignment.model.SessionDetailsModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.Result;

import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class SimpleScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private static final String TAG = "Checkpoint";
    private ZXingScannerView mScannerView;
    private int SCANNER_RESULT = 202;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {


        setResult(SCANNER_RESULT,new Intent().putExtra("scan_result",rawResult.getText()));
        finish();
    }

}

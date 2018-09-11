package com.hci.dv.estimoteindoorjavatest;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.estimote.indoorsdk.EstimoteCloudCredentials;
import com.estimote.indoorsdk_module.algorithm.ScanningIndoorLocationManager;
import com.estimote.indoorsdk_module.cloud.CloudCallback;
import com.estimote.indoorsdk_module.cloud.EstimoteCloudException;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManager;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManagerFactory;
import com.estimote.indoorsdk_module.cloud.Location;
import com.estimote.indoorsdk_module.cloud.LocationPosition;
import com.estimote.indoorsdk_module.view.IndoorLocationView;

public class FirstActivity extends AppCompatActivity {

    private static final String TAG = "__FirstActivity__";
    private static String _LocationId;
    private EstimoteCloudCredentials _CloudCredentials;
    private IndoorCloudManager _CloudManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        _LocationId = EstimoteIndoorJavaTestApplication.locationId;
        _CloudCredentials = EstimoteIndoorJavaTestApplication.cloudCredentials;

        _CloudManager = new IndoorCloudManagerFactory().create(getApplicationContext(), _CloudCredentials);
        _CloudManager.getLocation(_LocationId, new CloudCallback<Location>() {
            @Override
            public void success(Location location) {
                Log.d(TAG, "IndoorCloudManager success!!");
                EstimoteIndoorJavaTestApplication.location = location;
                startMainActivity();
            }

            @Override
            public void failure(EstimoteCloudException e) {
                Log.d(TAG, "IndoorCloudManager failure!!");
                Toast.makeText(FirstActivity.this, "IndoorCloudManager failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMainActivity() {
        startActivity(new Intent(FirstActivity.this, MainActivity.class));
        finish();
    }
}

package com.hci.dv.estimoteindoorjavatest;

import android.app.Activity;
import android.app.Notification;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.estimote.indoorsdk.EstimoteCloudCredentials;
import com.estimote.indoorsdk.IndoorLocationManagerBuilder;
import com.estimote.indoorsdk_module.algorithm.OnPositionUpdateListener;
import com.estimote.indoorsdk_module.algorithm.ScanningIndoorLocationManager;
import com.estimote.indoorsdk_module.cloud.CloudCallback;
import com.estimote.indoorsdk_module.cloud.EstimoteCloudException;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManager;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManagerFactory;
import com.estimote.indoorsdk_module.cloud.Location;
import com.estimote.indoorsdk_module.cloud.LocationPosition;
import com.estimote.indoorsdk_module.view.IndoorLocationView;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Random;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "__MainActivity__";

    // Member data
    // Estimote
    private EstimoteCloudCredentials _CloudCredentials;
    private IndoorLocationView _IndoorLocationView;
    private ScanningIndoorLocationManager _IndoorLocationManager;
    private Location _Location;

    private Notification notification;

    // Firebase
    private FirebaseDatabase _Database;
    private DatabaseReference _Reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _Location = EstimoteIndoorJavaTestApplication.location;
        _CloudCredentials = EstimoteIndoorJavaTestApplication.cloudCredentials;
        setContentView(R.layout.activity_main);

        _IndoorLocationView = (IndoorLocationView)findViewById(R.id.indoor_view);
        _Database = FirebaseDatabase.getInstance();
        _Reference = _Database.getReference("locationPosition");

        _IndoorLocationView.setLocation(_Location);

        _IndoorLocationManager = new IndoorLocationManagerBuilder(getApplicationContext(), _Location, _CloudCredentials)
                .withDefaultScanner()
                .build();

        _IndoorLocationManager.setOnPositionUpdateListener(new OnPositionUpdateListener() {
            @Override
            public void onPositionUpdate(LocationPosition locationPosition) {
                Log.d(TAG, "OnPositionUpdateListener onPositionUpdate");
                _IndoorLocationView.updatePosition(locationPosition);
                Log.d("__DATA__", locationPosition.toString());
                _UpdateCloud(locationPosition, true);
            }

            @Override
            public void onPositionOutsideLocation() {
                Log.d(TAG, "OnPositionUpdateListener onPositionOutsideLocation");
                _IndoorLocationView.hidePosition();
                _UpdateCloud(null, false);
            }
        });

        RequirementsWizardFactory.createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                _IndoorLocationManager.startPositioning();
                                return null;
                            }
                        },

                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override
                            public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                Toast.makeText(MainActivity.this, "requirements missing: " + requirements, Toast.LENGTH_SHORT).show();
                                return null;
                            }
                        },

                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                Toast.makeText(MainActivity.this, "requirements error: " + throwable, Toast.LENGTH_SHORT).show();
                                return null;
                            }
                        }
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _IndoorLocationManager.stopPositioning();
    }

    private void _UpdateCloud(LocationPosition locationPosition, boolean inside) {
        if (inside) {
            _Reference.setValue(locationPosition);
            _Reference.child("orientation").setValue(Math.random());
        } else {
            // reset location
            _Reference.setValue(new LocationPosition(0,0,0));
        }
    }

    private double _GetDistance(LocationPosition lp1, LocationPosition lp2){
        return Math.sqrt( Math.pow( lp2.getX() - lp1.getX(), 2 ) + Math.pow( lp2.getY() - lp1.getY(), 2 ) );
    }
}

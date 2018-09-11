package com.hci.dv.estimoteindoorjavatest;

import android.app.Application;
import com.estimote.indoorsdk_module.cloud.Location;

import com.estimote.indoorsdk.EstimoteCloudCredentials;

public class EstimoteIndoorJavaTestApplication extends Application {

    public static Location location;
    public static String locationId = "ris3";
    public static EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("dv-hci-2018-ci6", "4c1efaa690cf52a861dc8ba8aa472cdc");
}

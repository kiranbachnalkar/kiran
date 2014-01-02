package com.snapbizz.snapbilling.activity;
import com.testflightapp.lib.TestFlight;

import android.app.Application;


public class SnapBillingApplication extends Application {
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		TestFlight.takeOff(this, "289e1627-e4a2-4a5d-a771-45309373f427");
	}
	
}

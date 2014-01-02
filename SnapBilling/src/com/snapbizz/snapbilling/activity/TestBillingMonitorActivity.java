package com.snapbizz.snapbilling.activity;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.fragments.BillingMonitorFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class TestBillingMonitorActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_billing);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		BillingMonitorFragment billingMonitorFragment = new BillingMonitorFragment();
		ft.add(R.id.content_framelayout, billingMonitorFragment);
		ft.commit();
	}

}

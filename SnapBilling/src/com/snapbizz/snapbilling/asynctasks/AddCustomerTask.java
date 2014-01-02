package com.snapbizz.snapbilling.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class AddCustomerTask extends AsyncTask<Customer, Void, List<Customer>> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Customer could not be added";

	public AddCustomerTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<Customer> doInBackground(Customer... customers) {
		try {
			Log.d(AddCustomerTask.class.getName(),
					customers[0].getCustomerName());
			SnapBizzDatabaseHelper databaseHelper = SnapBillingUtils
					.getDatabaseHelper(context);
			List<Customer> customerList = databaseHelper
					.getCustomerDao()
					.queryBuilder()
					.where()
					.eq("customer_phone", customers[0].getCustomerPhoneNumber())
					.query();
			if (customerList.size() > 0) {
				databaseHelper.getCustomerDao().update(customerList.get(0));
			} else {
				databaseHelper.getCustomerDao().create(customers[0]);
			}
			List<Customer> result = new ArrayList<Customer>();
			result.add(customers[0]);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<Customer> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}

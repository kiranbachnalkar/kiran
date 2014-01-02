package com.snapbizz.snapbilling.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class QueryCustomerInfoTask extends
		AsyncTask<String, Void, List<Customer>> {
	private static SnapBizzDatabaseHelper databaseHelper;
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Customer Found";

	public QueryCustomerInfoTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<Customer> doInBackground(String... params) {
		System.out.println("params " + params[0]); 
		try {
			if(!params[0].equals(""))
			return getHelper(context).getCustomerDao().queryBuilder().where()
					.like("customer_phone", "%" + params[0] + "%").query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<Customer> result) {
		// TODO Auto-generated method stub
		if (result != null ) {
			System.out.println("got result success");
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

	private SnapBizzDatabaseHelper getHelper(Context context) {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(context,
					SnapBizzDatabaseHelper.class);
		}
		return databaseHelper;
	}

}

package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class QueryInventorySkuTask extends
		AsyncTask<String, Void, List<InventorySku>> {
	private static SnapBizzDatabaseHelper databaseHelper;
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Inventory Found";

	public QueryInventorySkuTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<InventorySku> doInBackground(String... params) {
		try {
			return getHelper(context).getInventorySkuDao().queryBuilder()
					.offset(Integer.parseInt(params[0])).limit(20).query();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<InventorySku> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() != 0) {
			System.out.println("got result success");
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			System.out.println(" result.size() " + result.size());
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

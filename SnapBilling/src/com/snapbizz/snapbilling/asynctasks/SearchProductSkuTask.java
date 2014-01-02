package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class SearchProductSkuTask extends AsyncTask<String, Void, List<ProductSku>>{
	
	private static SnapBizzDatabaseHelper databaseHelper;
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Matching Products Found";
	
	public SearchProductSkuTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected List<ProductSku> doInBackground(String... keyword) {
		// TODO Auto-generated method stub
		try {
			return getHelper(context).getProductSkuDao().queryBuilder().where().like("sku_id", keyword[0]+"%").or().like("sku_name", "%"+keyword[0]+"%").query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<ProductSku> result) {
		// TODO Auto-generated method stub
		if(result != null && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}
	
	private SnapBizzDatabaseHelper getHelper(Context context) {
		if(databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(context, SnapBizzDatabaseHelper.class);
		}
		return databaseHelper;
	}

}

package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class GetSubCategoryProductsTask extends AsyncTask<Integer, Void, List<ProductSku>>{
	
	private static SnapBizzDatabaseHelper databaseHelper;
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "";
	
	public GetSubCategoryProductsTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected List<ProductSku> doInBackground(Integer... subcategoryId) {
		// TODO Auto-generated method stub
		try {
			QueryBuilder<ProductSku, Integer> productSkuQueryBuilder = getHelper(context).getProductSkuDao().queryBuilder();
			productSkuQueryBuilder.where().eq("sku_subcategory_id", subcategoryId[0]);
			return productSkuQueryBuilder.join(getHelper(context).getInventorySkuDao().queryBuilder()).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<ProductSku> result) {
		// TODO Auto-generated method stub
		if(result != null) {
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

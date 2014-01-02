package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class QueryProductSubCategories extends AsyncTask<Integer, Void, List<ProductCategory>>{
	
	private static SnapBizzDatabaseHelper databaseHelper;
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Categories Found";
	
	public QueryProductSubCategories(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected List<ProductCategory> doInBackground(Integer... categories) {
		// TODO Auto-generated method stub
		try {
//			QueryBuilder<ProductCategory, Integer> productCategoryQueryBuilder = getHelper(context).getProductCategoryDao().queryBuilder();
//			productCategoryQueryBuilder.where().eq("product_parentcategory_id", categories[0]);
//			return productCategoryQueryBuilder.join(getHelper(context).getProductSkuDao().queryBuilder().join(getHelper(context).getInventorySkuDao().queryBuilder())).query();
			return getHelper(context).getProductCategoryDao().queryForEq("product_parentcategory_id", categories[0]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<ProductCategory> result) {
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

package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class GetQuickAddProductsTask extends AsyncTask<Void, Void, List<ProductSku>>{
	
	private static SnapBizzDatabaseHelper databaseHelper;
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Products Found";
	private final int GROCERY_PARENT_ID = 76;
	
	public GetQuickAddProductsTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected List<ProductSku> doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {
			List<ProductCategory> productCategoryList = getHelper(context).getProductCategoryDao().queryForEq("product_parentcategory_id", GROCERY_PARENT_ID);
			List<ProductSku> productSkuList = new ArrayList<ProductSku>();
			for(ProductCategory productCategory : productCategoryList) {
				ProductSku productSku = new ProductSku();
				productSku.setProductSkuCode("snapbizz"+productCategory.getCategoryId());
				productSku.setProductSkuName(productCategory.getCategoryName());
				productSkuList.add(productSku);
			}
			return productSkuList;
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
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
	
	private SnapBizzDatabaseHelper getHelper(Context context) {
		if(databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(context, SnapBizzDatabaseHelper.class);
		}
		return databaseHelper;
	}

}

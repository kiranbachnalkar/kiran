package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.Calendar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.snapbizz.snapbilling.domains.ShoppingCart;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.TransactionType;

public class SaveTrasnsactionTask extends AsyncTask<ShoppingCart, Void, Boolean>{
	
	private static SnapBizzDatabaseHelper databaseHelper;
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "";
	private TransactionType transactionType;
	
	public SaveTrasnsactionTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode, TransactionType transactionType) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.transactionType = transactionType;
	}
	
	@Override
	protected Boolean doInBackground(ShoppingCart... shoppingCarts) {
		// TODO Auto-generated method stub
		try {
			Transaction transaction = new Transaction();
			transaction.setCustomer(shoppingCarts[0].getCustomer());
			transaction.setTransactionType(transactionType);
			transaction.setTransactionTimeStamp(Calendar.getInstance().getTime());
			transaction.setTransactionAmount(shoppingCarts[0].getTotalPayableValue() - shoppingCarts[0].getTotalDiscount());
			getHelper(context).getTransactionDao().create(transaction);
			Log.d("Saving Transaction", "created transaction for cart with items "+shoppingCarts[0].getProductSkuList().size());
			for(InventorySku productSku : shoppingCarts[0].getProductSkuList()) {
				BillItem billItem = new BillItem();
				billItem.setProductSku(productSku.getProductSku());
				billItem.setProductSkuQuantity(productSku.getQuantity());
				billItem.setProductSkuSalePrice(productSku.getProductSku().getProductSkuSalePrice());
				billItem.setTransaction(transaction);
				getHelper(context).getBillItemDao().create(billItem);
				Log.d("Saving Transaction", "creating bill item");
			}
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		if(result) {
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

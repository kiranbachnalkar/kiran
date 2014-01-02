package com.snapbizz.snapbilling.activity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ProductSkuContainer;
import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.services.ServiceRequest;
import com.snapbizz.snaptoolkit.services.ServiceThread;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.RequestMethod;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapDBUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class TestActivity extends OrmLiteBaseActivity<SnapBizzDatabaseHelper> implements OnServiceCompleteListener, OnQueryCompleteListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);


		findViewById(R.id.create_db_button).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SnapDBUtils db = new SnapDBUtils(TestActivity.this, SnapToolkitConstants.DB_NAME, SnapToolkitConstants.DB_VERSION);
				try {
					db.createDataBaseFromAssets();
					Toast.makeText(getApplicationContext(), "created db", Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		findViewById(R.id.create_prod_button).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				callDummyServiceCall();
			}
		});

		findViewById(R.id.create_inventory_button).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					for(ProductSku productSku : getHelper().getProductSkuDao().queryForAll()) {
						getHelper().getInventorySkuDao().create(new InventorySku(productSku));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	public void callDummyServiceCall() {
		Request req = new Request();
		req.setRequestMethod(RequestMethod.GET);
		HashMap<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("as", "asd");
		req.setRequestParams(reqMap);
		ServiceRequest serviceRequest = new ServiceRequest(req, this);
		serviceRequest.setUrl("https://apasdi.passslot.com/");
		serviceRequest.setResponsibleClass(ProductSkuContainer.class);
		serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ONE);
		ServiceThread serviceThread = new ServiceThread(this, this, false);
		serviceThread.execute(serviceRequest);
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(ResponseContainer response) {
		// TODO Auto-generated method stub
		if(response.getRequestCode().equals(RequestCodes.REQUEST_CODE_ONE)) {
			ProductSkuContainer productSkuContainer = (ProductSkuContainer) response;
			for(ProductSku productSku : productSkuContainer.getProductSkuList()) {
				try {
					System.out.println("creating");
					//productSku.setProductCategory(getHelper().getProductCategoryDao().queryForEq("product_category_name", productSku.subCategory).get(0));
					getHelper().getProductSkuDao().create(productSku);
					System.out.println("created");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(this, "creating", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onError(ResponseContainer response, RequestCodes requestCode) {
		// TODO Auto-generated method stub

	}

}

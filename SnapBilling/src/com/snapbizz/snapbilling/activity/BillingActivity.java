package com.snapbizz.snapbilling.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.BillingMonitorAdapter.CartActionListener;
import com.snapbizz.snapbilling.adapters.ShoppingCartAdapter.OnShoppingCartChangeListener;
import com.snapbizz.snapbilling.asynctasks.GetScannedProductSkuTask;
import com.snapbizz.snapbilling.domains.ShoppingCart;
import com.snapbizz.snapbilling.fragments.ActionbarFragment;
import com.snapbizz.snapbilling.fragments.ActionbarFragment.OnNavigationListener;
import com.snapbizz.snapbilling.fragments.BillCheckoutFragment;
import com.snapbizz.snapbilling.fragments.BillingMonitorFragment;
import com.snapbizz.snapbilling.fragments.EditProductDialogFragment;
import com.snapbizz.snapbilling.fragments.EditProductDialogFragment.EditProductDialogListener;
import com.snapbizz.snapbilling.fragments.VirtualStoreFragment;
import com.snapbizz.snapbilling.interfaces.FragmentLoadCompleteListener;
import com.snapbizz.snapbilling.interfaces.SearchedProductClickListener;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.SnapDBUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class BillingActivity extends Activity implements OnQueryCompleteListener, OnServiceCompleteListener, OnShoppingCartChangeListener, 
EditProductDialogListener, SearchedProductClickListener, OnNavigationListener, FragmentLoadCompleteListener, CartActionListener {

	private final String TAG = BillingActivity.class.getName();
	private final int GET_SCANNEDSKU_TASKCODE = 0;
	private final int BARCODE_DELIMITER_KEYCODE = 61;
	private final String BASKET_SWITCH_PREFIX = "#SB#";
	private final String BASKET_DEFAULT_CODE = "#SB#Cart1";
	private String barCode="";
	private HashMap<String, Integer> activeShoppingCartMap;
	private SparseArray<ShoppingCart> shoppingCartList;
	private HashMap<String, Integer> shoppingCartCodeMap;
	private LinkedBlockingQueue<Integer> shoppingCartQueue;		
	private VirtualStoreFragment virtualStoreFragment;	
	private ActionbarFragment actionbarFragment;
	private BillCheckoutFragment billCheckoutFragment;
	private BillingMonitorFragment billingMonitorFragment;
	private int activeShoppingCartId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SnapDBUtils db = new SnapDBUtils(this, SnapToolkitConstants.DB_NAME, SnapToolkitConstants.DB_VERSION);
		try {
			db.createDataBaseFromAssets();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setContentView(R.layout.activity_billing);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		actionbarFragment = new ActionbarFragment();
		ft.add(R.id.actionbar_framelayout, actionbarFragment);
		ft.commit();
		ft = getFragmentManager().beginTransaction();
		//		virtualStoreFragment = new VirtualStoreFragment();
		//		ft.add(R.id.content_framelayout, virtualStoreFragment, getString(R.string.storefragment_tag));
		//		ft.commit();
		billingMonitorFragment = new BillingMonitorFragment();
		ft.add(R.id.content_framelayout, billingMonitorFragment, getString(R.string.monitorfragment_tag));
		ft.commit();
		((ListView) findViewById(R.id.product_search_listview)).setOnItemClickListener(onSearchedProductClickListener);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return super.onKeyDown(keyCode, event);
		}
		if(keyCode == BARCODE_DELIMITER_KEYCODE) {
			Log.d(TAG, "scanned barcode "+barCode + "keycode "+keyCode);
			if(barCode.length() < 5) {
				barCode = "";
				return false;
			}

			if(SnapBillingUtils.getBarCode(barCode).substring(0, 4).equalsIgnoreCase(BASKET_SWITCH_PREFIX)) {
				switchScannerShoppingCart(SnapBillingUtils.getScannerId(barCode), SnapBillingUtils.getBarCode(barCode));
			} else {
				if(shoppingCartQueue == null) {
					shoppingCartQueue = new LinkedBlockingQueue<Integer>();
				}
				shoppingCartQueue.add(getScannerShoppingCart(SnapBillingUtils.getScannerId(barCode)).getShoppingCartId());
				new GetScannedProductSkuTask(this, this, GET_SCANNEDSKU_TASKCODE).execute(SnapBillingUtils.getBarCode(barCode));
			}
			barCode = "";
			return true;
		} else if(keyCode != 59) {
			barCode += ((char) event.getUnicodeChar());
			return true;
		}
		return true;
	}

	public ShoppingCart getScannerShoppingCart(String scannerId) {
		if(activeShoppingCartMap == null) {
			activeShoppingCartMap = new HashMap<String, Integer>();
			shoppingCartList = new SparseArray<ShoppingCart>();
			shoppingCartCodeMap = new HashMap<String, Integer>();
			ShoppingCart shoppingCart = new ShoppingCart();
			activeShoppingCartMap.put(scannerId, shoppingCart.getShoppingCartId());
			shoppingCartList.put(shoppingCart.getShoppingCartId(), shoppingCart);
			activeShoppingCartId = shoppingCart.getShoppingCartId();
			shoppingCartCodeMap.put(BASKET_DEFAULT_CODE, shoppingCart.getShoppingCartId());
			actionbarFragment.addShoppingCart(shoppingCart);
			return shoppingCart;
		} else {
			ShoppingCart shoppingCart = shoppingCartList.get(activeShoppingCartMap.get(scannerId));
			if(shoppingCart == null) {
				shoppingCart = new ShoppingCart();
				activeShoppingCartMap.put(scannerId, shoppingCart.getShoppingCartId());
				shoppingCartList.put(shoppingCart.getShoppingCartId(), shoppingCart);
				actionbarFragment.addShoppingCart(shoppingCart);
				return shoppingCart;
			} else
				return shoppingCart;
		}
	}

	/**
	 * Need to add reusability of shopping carts for optimization
	 * Also need to fix issue regarding default cart scanning and switching back
	 * @param scannerId
	 * @param shoppingCartId
	 */
	public void switchScannerShoppingCart(String scannerId, String shoppingCartCode) {
		Log.d(TAG, "switching carts "+" scannerId : "+scannerId+" newCartCode : "+shoppingCartCode);
		if(activeShoppingCartMap == null) {
			activeShoppingCartMap = new HashMap<String, Integer>();
			shoppingCartList = new SparseArray<ShoppingCart>();
			shoppingCartCodeMap = new HashMap<String, Integer>();
			ShoppingCart shoppingCart = new ShoppingCart();
			activeShoppingCartMap.put(scannerId, shoppingCart.getShoppingCartId());
			shoppingCartCodeMap.put(shoppingCartCode, shoppingCart.getShoppingCartId());
			shoppingCartList.put(shoppingCart.getShoppingCartId(), shoppingCart);
			activeShoppingCartId = shoppingCart.getShoppingCartId();
			actionbarFragment.addShoppingCart(shoppingCart);
		} else {
			Integer shoppingCartId = shoppingCartCodeMap.get(shoppingCartCode);
			ShoppingCart shoppingCart;
			if(shoppingCartId != null) {
				shoppingCart = shoppingCartList.get(shoppingCartId);
			} else {
				shoppingCart = new ShoppingCart();
				shoppingCartCodeMap.put(shoppingCartCode, shoppingCart.getShoppingCartId());
				shoppingCartList.put(shoppingCart.getShoppingCartId(), shoppingCart);
				actionbarFragment.addShoppingCart(shoppingCart);
			}
			activeShoppingCartMap.put(scannerId, shoppingCart.getShoppingCartId());
		}
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if(taskCode == GET_SCANNEDSKU_TASKCODE) {
			try {
				Log.d(TAG," found "+((List<ProductSku>) responseList).size()+" items ");
				Log.d(TAG," product : "+((List<ProductSku>) responseList).get(0).getProductSkuName() + " added to shopping cart "+shoppingCartQueue.peek());
				List<ProductSku> productSkuList = (List<ProductSku>) responseList;
				shoppingCartList.get(shoppingCartQueue.poll()).addItemsToCart(productSkuList);
			} catch(ClassCastException e) {
				e.printStackTrace();
			}
		}
	}

	AdapterView.OnItemClickListener onSearchedProductClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			onSearchedProductClick((ProductSku) adapter.getItemAtPosition(position));
			actionbarFragment.resetSearchViews();
		}
	};

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		if(taskCode == GET_SCANNEDSKU_TASKCODE) {
			shoppingCartQueue.poll();
			Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSuccess(ResponseContainer response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(ResponseContainer response, RequestCodes requestCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShoppingCartSwitch(Integer shoppingCartId) {
		// TODO Auto-generated method stub
		activeShoppingCartId = shoppingCartId;
		if(billCheckoutFragment != null) {
			billCheckoutFragment.setShoppingCart(shoppingCartList.get(shoppingCartId));
			billCheckoutFragment.updateTotalBill();
		}
	}

	@Override
	public void onAddProduct(ProductSku productSku, int quantity) {
		// TODO Auto-generated method stub
		if(shoppingCartList == null) {
			shoppingCartList = new SparseArray<ShoppingCart>();
			ShoppingCart shoppingCart = new ShoppingCart();
			activeShoppingCartId = shoppingCart.getShoppingCartId();
			shoppingCartList.put(activeShoppingCartId, shoppingCart);
			actionbarFragment.addShoppingCart(shoppingCart);
			if(billCheckoutFragment != null)
				billCheckoutFragment.setShoppingCart(shoppingCart);
		}
		shoppingCartList.get(activeShoppingCartId).addItemToCart(productSku, quantity);
		if(billCheckoutFragment != null)
			billCheckoutFragment.updateTotalBill();
	}

	@Override
	public void onSearchedProductClick(ProductSku productSku) {
		// TODO Auto-generated method stub
		EditProductDialogFragment editProductDialogFragment = new EditProductDialogFragment();
		editProductDialogFragment.setProductSku(productSku);
		editProductDialogFragment.show(getFragmentManager(), "editProductSku");
	}

	@Override
	public void onNavigationListener(int id) {
		// TODO Auto-generated method stub
		if(id == R.id.bill_navigation_button) {
			Log.d(TAG, "adding bill fragment");
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if(virtualStoreFragment != null)
				ft.detach(virtualStoreFragment);
			if(billCheckoutFragment == null) {
				billCheckoutFragment = new BillCheckoutFragment();
				ft.add(R.id.content_framelayout, billCheckoutFragment, getString(R.string.billfragment_tag));
				Log.d(TAG,"replacing bill fragment");
			} else {
				Log.d(TAG,"attaching bill fragment");
				ft.attach(billCheckoutFragment);
			}
			ft.commit();
			if(shoppingCartList != null)
				billCheckoutFragment.setShoppingCart(shoppingCartList.get(activeShoppingCartId));
			billCheckoutFragment.updateTotalBill();
			//ft.addToBackStack("checkout");
		} else if(id == R.id.store_navigation_button) {
			Log.d(TAG, "adding store fragment");
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if(billCheckoutFragment != null)
				ft.detach(billCheckoutFragment);
			if(virtualStoreFragment == null) {
				virtualStoreFragment = new VirtualStoreFragment();
				ft.add(R.id.content_framelayout, virtualStoreFragment, getString(R.string.storefragment_tag));
			} else
				ft.attach(virtualStoreFragment);
			//ft.addToBackStack("checkout");
			ft.commit();
		} else if(id == R.id.back_navigation_button) {
			Log.d(TAG, "adding monitor fragment");
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if(billCheckoutFragment != null)
				ft.detach(billCheckoutFragment);
			ft.attach(billingMonitorFragment);
			//ft.addToBackStack("checkout");
			ft.commit();
		}
	}

	@Override
	public void onFragmentLoadComplete(Fragment fragment) {
		// TODO Auto-generated method stub
		Log.d(TAG, "fragment loaded "+fragment.getTag());
		if(fragment.getTag().equals(getString(R.string.storefragment_tag))) {
			actionbarFragment.billNaviationButton.setVisibility(View.VISIBLE);
			actionbarFragment.storeNavigationButton.setVisibility(View.GONE);
			actionbarFragment.backNavigationButton.setVisibility(View.GONE);
			actionbarFragment.shoppingCartGridView.setVisibility(View.VISIBLE);
			actionbarFragment.productSearchButton.setVisibility(View.VISIBLE);
		} else if(fragment.getTag().equals(getString(R.string.billfragment_tag))) {
			actionbarFragment.storeNavigationButton.setVisibility(View.VISIBLE);
			actionbarFragment.billNaviationButton.setVisibility(View.GONE);
			actionbarFragment.backNavigationButton.setVisibility(View.VISIBLE);
			actionbarFragment.shoppingCartGridView.setVisibility(View.VISIBLE);
			actionbarFragment.productSearchButton.setVisibility(View.VISIBLE);
		} else if(fragment.getTag().equals(getString(R.string.monitorfragment_tag))) {
			actionbarFragment.storeNavigationButton.setVisibility(View.GONE);
			actionbarFragment.billNaviationButton.setVisibility(View.GONE);
			actionbarFragment.backNavigationButton.setVisibility(View.GONE);
			actionbarFragment.shoppingCartGridView.setVisibility(View.GONE);
			actionbarFragment.productSearchEditText.setVisibility(View.GONE);
			actionbarFragment.productSearchButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onAddCart(int position, boolean isNewCart) {
		// TODO Auto-generated method stub
		if(isNewCart) {
			if(shoppingCartList == null) {
				shoppingCartList = new SparseArray<ShoppingCart>();
			}
			ShoppingCart shoppingCart = new ShoppingCart();
			shoppingCart.setProductSkuList(new ArrayList<InventorySku>());
			shoppingCart.setProductSkuMap(new HashMap<String, Integer>());
			activeShoppingCartId = shoppingCart.getShoppingCartId();
			shoppingCartList.put(activeShoppingCartId, shoppingCart);
			if(billCheckoutFragment != null)
				billCheckoutFragment.setShoppingCart(shoppingCart);
			if(billingMonitorFragment != null)
				billingMonitorFragment.onAddCart(shoppingCart, position);
		}
		actionbarFragment.addShoppingCart(billingMonitorFragment.billingMonitorAdapter.getItem(position), position);
	}

	@Override
	public void onRemoveCart(int shoppingCartId) {
		// TODO Auto-generated method stub
		shoppingCartList.get(shoppingCartId).deleteCart();
		if(billingMonitorFragment != null)
			billingMonitorFragment.billingMonitorAdapter.notifyDataSetChanged();
		actionbarFragment.removeShoppingCart(shoppingCartId);
	}

	@Override
	public void onOpenCart(int id) {
		// TODO Auto-generated method stub
		this.activeShoppingCartId = id;
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.detach(billingMonitorFragment);
		if(billCheckoutFragment == null) {
			billCheckoutFragment = new BillCheckoutFragment();
			ft.add(R.id.content_framelayout, billCheckoutFragment, getString(R.string.billfragment_tag));
			Log.d(TAG,"replacing bill fragment");
			if(shoppingCartList != null) {
				billCheckoutFragment.setShoppingCart(shoppingCartList.get(id));
			}
		} else {
			if(shoppingCartList != null) {
				billCheckoutFragment.setShoppingCart(shoppingCartList.get(id));
			}
			billCheckoutFragment.updateTotalBill();
			Log.d(TAG,"attaching bill fragment");
			ft.attach(billCheckoutFragment);
		}
		//ft.addToBackStack("checkout");
		ft.commit();

	}

	@Override
	public void onCustomerSearch(String customerNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelSearch(int position) {
		// TODO Auto-generated method stub

	}	

}

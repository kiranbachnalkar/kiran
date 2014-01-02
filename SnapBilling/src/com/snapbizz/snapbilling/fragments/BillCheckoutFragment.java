package com.snapbizz.snapbilling.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.BillListAdapter;
import com.snapbizz.snapbilling.adapters.BillListAdapter.BillItemEditListener;
import com.snapbizz.snapbilling.adapters.VirtualStoreProductAdapter;
import com.snapbizz.snapbilling.asynctasks.GetQuickAddProductsTask;
import com.snapbizz.snapbilling.asynctasks.SaveTrasnsactionTask;
import com.snapbizz.snapbilling.domains.ShoppingCart;
import com.snapbizz.snapbilling.fragments.KeypadFragment.KeyboardEnterListener;
import com.snapbizz.snapbilling.interfaces.FragmentLoadCompleteListener;
import com.snapbizz.snapbilling.interfaces.SearchedProductClickListener;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.TransactionType;

public class BillCheckoutFragment extends Fragment implements BillItemEditListener, KeyboardEnterListener, OnQueryCompleteListener {

	private ShoppingCart shoppingCart;
	private BillListAdapter billListAdapter;
	private FragmentLoadCompleteListener fragmentLoadCompleteListener;
	private VirtualStoreProductAdapter quickAddAdapter;
	private KeypadFragment keypadFragment;
	private int position;
	private int EDIT_QTY_CONTEXT = 0;
	private int EDIT_PRICE_CONTEXT = 1;
	private int EDIT_TOTALPRICE_CONTEXT = 2;
	private int ENTER_CASH_CONTEXT = 3;
	private View deleteReturnLayout;
	private TextView billTotalTextView;
	private TextView amountTextView;
	private TextView savingsTextView;
	private TextView totalTextView; 
	private TextView payableTextView;
	private TextView changeTextView;
	private Button discountButton;
	private Button cashButton;
	private SearchedProductClickListener searchedProductClickListener;
	private final String MISC_PRODUCT_CODE = "snapbizzmisc9182";
	private final String DAIRY_PRODUCT_CODE = "snapbizzdairy1234";
	private final String FRESH_PRODUCT_CODE = "snapbizzfrest122182";
	private final String FOOD_PRODUCT_CODE = "snapbizzfood9182";
	private final int GET_QUICKADD_TASKCODE = 0;
	private final int SAVE_TRANSACTION_TASKCODE = 1;

	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
		if(billListAdapter != null) {
			billListAdapter.setProductList(shoppingCart.getProductSkuList());
			billListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bill_checkout, null);
		billTotalTextView = (TextView) view.findViewById(R.id.bill_total_textview);
		deleteReturnLayout = view.findViewById(R.id.delete_return_layout);
		view.findViewById(R.id.billitem_delete_button).setOnClickListener(onProductDeleteReturnClickListener);
		view.findViewById(R.id.billitem_return_button).setOnClickListener(onProductDeleteReturnClickListener);
		amountTextView = (TextView) view.findViewById(R.id.amount_value_textview);
		savingsTextView = (TextView) view.findViewById(R.id.savings_value_textview);
		totalTextView = (TextView) view.findViewById(R.id.total_value_textview);
		payableTextView = (TextView) view.findViewById(R.id.pay_value_textview);
		changeTextView = (TextView) view.findViewById(R.id.change_value_textview);
		discountButton = (Button) view.findViewById(R.id.discount_value_button);
		discountButton.setOnClickListener(onTotalDiscountClickListener);
		cashButton = (Button) view.findViewById(R.id.cash_value_button);
		cashButton.setOnClickListener(onCashReceivedClickListener);
		view.findViewById(R.id.cancel_bill_button).setOnClickListener(onBillCompleteClickListener);
		view.findViewById(R.id.pay_bill_button).setOnClickListener(onBillCompleteClickListener);
		((TwoWayGridView) view.findViewById(R.id.checkout_quickadd_gridview)).setOnItemClickListener(onProductClickListener);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			fragmentLoadCompleteListener = (FragmentLoadCompleteListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+"must implement FragmentLoadCompleteListener");
		}
		try {
			searchedProductClickListener = (SearchedProductClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+"must implement SearchedProductClickListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if(shoppingCart != null && shoppingCart.getProductSkuList() != null) {
			if(billListAdapter == null)
				billListAdapter = new BillListAdapter(getActivity(), shoppingCart.getProductSkuList(), this);
			((ListView) getView().findViewById(R.id.bill_listview)).setAdapter(billListAdapter);
			shoppingCart.setTotalCartValue(SnapBillingUtils.totalShoppingCartPrice(shoppingCart));
			shoppingCart.setTotalPayableValue(shoppingCart.getTotalCartValue());
			billTotalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalCartValue(), getActivity()));
			amountTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalCartValue(), getActivity()));
			totalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalCartValue(), getActivity()));
			getView().findViewById(R.id.complete_bill_layout).setVisibility(View.VISIBLE);
		}

		TabHost tabHost = (TabHost) getView().findViewById(R.id.tabhost);
		tabHost.setup();

		TabSpec spec1=tabHost.newTabSpec(getString(R.string.offers));
		spec1.setContent(R.id.offers_tab);
		spec1.setIndicator(getString(R.string.offers));

		TabSpec spec2=tabHost.newTabSpec(getString(R.string.quick_add));
		spec2.setContent(R.id.quickadd_tab);
		spec2.setIndicator(getString(R.string.quick_add));

		TabSpec spec3=tabHost.newTabSpec(getString(R.string.complete));
		spec3.setIndicator(getString(R.string.complete));
		spec3.setContent(R.id.checkout_tab);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		if(quickAddAdapter != null) {
			((TwoWayGridView) getView().findViewById(R.id.checkout_quickadd_gridview)).setAdapter(quickAddAdapter);
		} else
			new GetQuickAddProductsTask(getActivity(), this, GET_QUICKADD_TASKCODE).execute();
		fragmentLoadCompleteListener.onFragmentLoadComplete(this);
	}

	TwoWayAdapterView.OnItemClickListener onProductClickListener = new TwoWayAdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(TwoWayAdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			searchedProductClickListener.onSearchedProductClick(quickAddAdapter.getItem(position));
		}

	};

	View.OnClickListener onTotalDiscountClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if(keypadFragment == null) {
				keypadFragment = new KeypadFragment();
				keypadFragment.setKeypadEnterListener(BillCheckoutFragment.this);
				ft.add(R.id.overlay_framelayout, keypadFragment);
			} else
				ft.attach(keypadFragment);
			ft.commit();
			keypadFragment.enableDiscountMode();
			keypadFragment.showDiscount();
			keypadFragment.setTotalValue(shoppingCart.getTotalPayableValue());
			keypadFragment.setContext(EDIT_TOTALPRICE_CONTEXT);
			keypadFragment.setCurrentValue(0);
			getView().findViewById(R.id.overlay_framelayout).setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			rlParams.setMargins(700, 100, 0, 0);
			getActivity().findViewById(R.id.overlay_framelayout).setLayoutParams(rlParams);
			deleteReturnLayout.setVisibility(View.GONE);
		}
	};

	View.OnClickListener onCashReceivedClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if(keypadFragment == null) {
				keypadFragment = new KeypadFragment();
				keypadFragment.setKeypadEnterListener(BillCheckoutFragment.this);
				ft.add(R.id.overlay_framelayout, keypadFragment);
			} else
				ft.attach(keypadFragment);
			ft.commit();
			keypadFragment.setContext(ENTER_CASH_CONTEXT);
			keypadFragment.setCurrentValue(0);
			getView().findViewById(R.id.overlay_framelayout).setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			rlParams.setMargins(700, 200, 0, 0);
			getActivity().findViewById(R.id.overlay_framelayout).setLayoutParams(rlParams);
			deleteReturnLayout.setVisibility(View.GONE);
		}
	};

	View.OnClickListener onBillCompleteClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder
			.setCancelable(true).setNegativeButton("Cancel", null);
			View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_completebill, null);
			if(v.getId() == R.id.cancel_bill_button) {
				view.findViewById(R.id.ty_textview).setVisibility(View.GONE);
				builder.setPositiveButton("Done", onCancelBillClickListener);
			} else if(v.getId() == R.id.pay_bill_button) {
				view.findViewById(R.id.cancel_bill_textview).setVisibility(View.GONE);
				builder.setPositiveButton("Done", onCompleteBillClickListener);
			}
			((TextView) view.findViewById(R.id.lineitems_value_textview)).setText(billListAdapter.getCount()+"");
			((TextView) view.findViewById(R.id.saved_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getTotalCartValue() - shoppingCart.getTotalPayableValue() + shoppingCart.getTotalDiscount(), getActivity()));
			((TextView) view.findViewById(R.id.total_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getTotalPayableValue() - shoppingCart.getTotalDiscount(),  getActivity()));
			int qty = 0;
			for(int i = 0; i < billListAdapter.getCount(); i++) {
				qty += billListAdapter.getItem(i).getQuantity();
			}
			((TextView) view.findViewById(R.id.total_qty_value_textview)).setText(qty+"");
			builder.setView(view);
			builder.create().show();
		}
	};

	DialogInterface.OnClickListener onCancelBillClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			shoppingCart.deleteCart();
			billListAdapter.notifyDataSetChanged();
			resetCheckoutViews();
			billTotalTextView.setText("");
		}
	};

	DialogInterface.OnClickListener onCompleteBillClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			new SaveTrasnsactionTask(getActivity(), BillCheckoutFragment.this, SAVE_TRANSACTION_TASKCODE, TransactionType.BILL).execute(shoppingCart);
		}
	};

	View.OnClickListener onProductDeleteReturnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			InventorySku billItem = billListAdapter.getItem(position);
			if(v.getId() == R.id.billitem_delete_button) {
				billListAdapter.remove(position);
				billListAdapter.notifyDataSetChanged();
				shoppingCart.setTotalPayableValue(shoppingCart.getTotalPayableValue() - (billItem.getQuantity() * billItem.getProductSku().getProductSkuSalePrice()));
				billTotalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalPayableValue(), getActivity()));
			} else if(v.getId() == R.id.billitem_return_button) {
				billItem.setQuantity(-billItem.getQuantity());
				billListAdapter.notifyDataSetChanged();
				shoppingCart.setTotalPayableValue(shoppingCart.getTotalPayableValue() +  (billItem.getQuantity() * 2 * billItem.getProductSku().getProductSkuSalePrice()));
				billTotalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalPayableValue(), getActivity()));
			}
			deleteReturnLayout.setVisibility(View.GONE);
		}
	};

	@Override
	public void onProductEdit(int position) {
		// TODO Auto-generated method stub
		resetCheckoutViews();
		this.position = position;
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(70, (position * 80) + 150, 0, 0);
		deleteReturnLayout.setLayoutParams(rlParams);
		deleteReturnLayout.setVisibility(View.VISIBLE);
		getView().findViewById(R.id.overlay_framelayout).setVisibility(View.GONE);
	}

	@Override
	public void onQuantityEdit(int position) {
		// TODO Auto-generated method stub
		resetCheckoutViews();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if(keypadFragment == null) {
			keypadFragment = new KeypadFragment();
			keypadFragment.setKeypadEnterListener(this);
			ft.add(R.id.overlay_framelayout, keypadFragment);
		} else
			ft.attach(keypadFragment);
		ft.commit();
		keypadFragment.hideDiscount();
		keypadFragment.setContext(EDIT_QTY_CONTEXT);
		keypadFragment.setCurrentValue(billListAdapter.getItem(position).getQuantity());
		getView().findViewById(R.id.overlay_framelayout).setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(440, (position * 80) + 120, 0, 0);
		getActivity().findViewById(R.id.overlay_framelayout).setLayoutParams(rlParams);
		this.position = position;
		deleteReturnLayout.setVisibility(View.GONE);
	}

	@Override
	public void onPriceEdit(int position) {
		// TODO Auto-generated method stub
		resetCheckoutViews();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if(keypadFragment == null) {
			keypadFragment = new KeypadFragment();
			keypadFragment.setKeypadEnterListener(this);
			ft.add(R.id.overlay_framelayout, keypadFragment);
		} else
			ft.attach(keypadFragment);
		ft.commit();
		keypadFragment.showDiscount();
		keypadFragment.setContext(EDIT_PRICE_CONTEXT);
		keypadFragment.setCurrentValue(billListAdapter.getItem(position).getProductSku().getProductSkuMrp());
		getView().findViewById(R.id.overlay_framelayout).setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(440, (position * 100) + 120, 0, 0);
		getActivity().findViewById(R.id.overlay_framelayout).setLayoutParams(rlParams);
		this.position = position;
		deleteReturnLayout.setVisibility(View.GONE);
	}

	public void updateTotalBill() {
		if(shoppingCart != null) {
			Log.d("billcheckout fragment", "updating bill");
			if(billListAdapter == null) {
				billListAdapter = new BillListAdapter(getActivity(), shoppingCart.getProductSkuList(), this);
				((ListView) getView().findViewById(R.id.bill_listview)).setAdapter(billListAdapter);
			}
			billListAdapter.notifyDataSetChanged();
			shoppingCart.setTotalCartValue(SnapBillingUtils.totalShoppingCartPrice(shoppingCart));
			shoppingCart.setTotalPayableValue(shoppingCart.getTotalCartValue());
			billTotalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalCartValue(), getActivity()));
			amountTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalCartValue(), getActivity()));
			totalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalCartValue(), getActivity()));
		}
	}

	@Override
	public void onKeyBoardEnter(float value, int context) {
		// TODO Auto-generated method stub
		Log.d("keypad", "value "+value);
		float totalPayableValue = shoppingCart.getTotalPayableValue();
		if(context == EDIT_QTY_CONTEXT) {
			InventorySku billItem = billListAdapter.getItem(position);
			totalPayableValue = (totalPayableValue + ((((int) value) - billItem.getQuantity()) * billItem.getProductSku().getProductSkuSalePrice()));
			shoppingCart.setTotalPayableValue(totalPayableValue);
			billTotalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(totalPayableValue, getActivity()));
			totalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(totalPayableValue, getActivity()));
			billItem.setQuantity((int)value);
			billListAdapter.notifyDataSetChanged();
			shoppingCart.setTotalPayableValue(totalPayableValue);
		} else if(context == EDIT_PRICE_CONTEXT) {
			InventorySku billItem = billListAdapter.getItem(position);
			totalPayableValue = (totalPayableValue + ((value - billItem.getProductSku().getProductSkuSalePrice()) * billItem.getQuantity()));
			billTotalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(totalPayableValue, getActivity()));
			totalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(totalPayableValue, getActivity()));
			if(shoppingCart.getTotalCartValue() > totalPayableValue)
				savingsTextView.setText(SnapBillingTextFormatter.formatPriceText((shoppingCart.getTotalCartValue() - totalPayableValue), getActivity()));
			else {
				amountTextView.setText(SnapBillingTextFormatter.formatPriceText(totalPayableValue, getActivity()));
				savingsTextView.setText(SnapBillingTextFormatter.formatPriceText(0, getActivity()));
			}
			billItem.getProductSku().setProductSkuSalePrice(value);
			billListAdapter.notifyDataSetChanged();
			shoppingCart.setTotalPayableValue(totalPayableValue);
		} else if(context == EDIT_TOTALPRICE_CONTEXT) {
			shoppingCart.setTotalDiscount(value);
			discountButton.setText(SnapBillingTextFormatter.formatPriceText(value, getActivity()));
			payableTextView.setText(SnapBillingTextFormatter.formatPriceText(totalPayableValue - shoppingCart.getTotalDiscount(), getActivity()));
		} else if(context == ENTER_CASH_CONTEXT) {
			cashButton.setText(SnapBillingTextFormatter.formatPriceText(value, getActivity()));
			value -= (totalPayableValue - shoppingCart.getTotalDiscount());
			if(value < 0)
				value = 0;
			changeTextView.setText(SnapBillingTextFormatter.formatPriceText(value, getActivity()));
		}
		getView().findViewById(R.id.overlay_framelayout).setVisibility(View.GONE);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.detach(keypadFragment);
		ft.commit();
	}

	public void resetCheckoutViews() {
		discountButton.setText("00");
		payableTextView.setText("");
		amountTextView.setText("");
		savingsTextView.setText("");
		cashButton.setText("00");
		changeTextView.setText("");
		totalTextView.setText("");
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if(taskCode == SAVE_TRANSACTION_TASKCODE) {
			shoppingCart.getProductSkuList().clear();
			billListAdapter.notifyDataSetChanged();
			resetCheckoutViews();
			billTotalTextView.setText("");
		} else if(taskCode == GET_QUICKADD_TASKCODE) {
			if(quickAddAdapter == null) {			
				quickAddAdapter = new VirtualStoreProductAdapter(getActivity(), (ArrayList<ProductSku>) responseList);
				((TwoWayGridView) getView().findViewById(R.id.checkout_quickadd_gridview)).setAdapter(quickAddAdapter);
			}
			ProductSku productSku = new ProductSku();
			productSku.setProductSkuName("Food");
			productSku.setProductSkuCode(FOOD_PRODUCT_CODE);
			quickAddAdapter.insert(productSku, 0);
			productSku = new ProductSku();
			productSku.setProductSkuCode(DAIRY_PRODUCT_CODE);
			productSku.setProductSkuName("Dairy");
			quickAddAdapter.insert(productSku, 1);
			productSku = new ProductSku();
			productSku.setProductSkuCode(FRESH_PRODUCT_CODE);
			productSku.setProductSkuName("Fresh");
			quickAddAdapter.insert(productSku, 2);
			productSku = new ProductSku();
			productSku.setProductSkuCode(MISC_PRODUCT_CODE);
			productSku.setProductSkuName("Misc");
			quickAddAdapter.insert(productSku, 3);
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub

	}

}

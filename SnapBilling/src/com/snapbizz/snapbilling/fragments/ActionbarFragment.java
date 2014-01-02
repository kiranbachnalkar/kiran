package com.snapbizz.snapbilling.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.ProductSearchAdapter;
import com.snapbizz.snapbilling.adapters.ShoppingCartAdapter;
import com.snapbizz.snapbilling.adapters.ShoppingCartAdapter.OnShoppingCartChangeListener;
import com.snapbizz.snapbilling.asynctasks.SearchProductSkuTask;
import com.snapbizz.snapbilling.domains.ShoppingCart;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class ActionbarFragment extends Fragment implements OnQueryCompleteListener {

	public Button productSearchButton;
	public Button billNaviationButton;
	public Button storeNavigationButton;
	public Button backNavigationButton;;
	public EditText productSearchEditText;
	private ListView productSearchListView;
	private LinearLayout productSearchLinearLayout;
	private final int SEARCH_PRODUCTS_TASKCODE = 3;
	private SearchProductSkuTask searchProductSkuTask;
	private ProductSearchAdapter productSearchAdapter;
	private ShoppingCartAdapter shoppingCartAdapter;
	public TwoWayGridView shoppingCartGridView;
	private OnShoppingCartChangeListener onShoppingCartChangeListener;
	private OnNavigationListener onNavigationListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.layout_actionbar, null);
		productSearchButton = (Button) view.findViewById(R.id.product_search_button);
		productSearchButton.setOnClickListener(onSearchClickListener);
		productSearchEditText = (EditText) view.findViewById(R.id.product_search_edittext);
		productSearchEditText = (EditText) view.findViewById(R.id.product_search_edittext);
		productSearchEditText.addTextChangedListener(productSearchTextWatcher);
		shoppingCartGridView = (TwoWayGridView) view.findViewById(R.id.store_shoppingcart_gridview);
		shoppingCartGridView.setOnItemClickListener(onShoppingCartClickListener);
		billNaviationButton = (Button) view.findViewById(R.id.bill_navigation_button);
		billNaviationButton.setOnClickListener(onNavigationClickListener);
		storeNavigationButton = (Button) view.findViewById(R.id.store_navigation_button);
		storeNavigationButton.setOnClickListener(onNavigationClickListener);
		backNavigationButton = (Button) view.findViewById(R.id.back_navigation_button);
		backNavigationButton.setOnClickListener(onNavigationClickListener);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			onShoppingCartChangeListener = (OnShoppingCartChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+"must implement onShoppingCartChangeListener");
		}
		try {
			onNavigationListener = (OnNavigationListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+"must implement OnBillNavigationClickListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		productSearchListView = (ListView) getActivity().findViewById(R.id.product_search_listview);
		productSearchLinearLayout = (LinearLayout) getActivity().findViewById(R.id.product_search_linearlayout);
		productSearchLinearLayout.setOnClickListener(onProductSearchLinearLayoutClickListener);
		if(shoppingCartAdapter != null)
			shoppingCartGridView.setAdapter(shoppingCartAdapter);
	}

	View.OnClickListener onProductSearchLinearLayoutClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			resetSearchViews();
		}
	};

	CountDownTimer keyStrokeTimer = new CountDownTimer(SnapBillingConstants.KEY_STROKE_TIMEOUT, SnapBillingConstants.KEY_STROKE_TIMEOUT) {

		@Override
		public void onTick(long arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			String keyword = productSearchEditText.getText().toString();
			if(searchProductSkuTask != null)
				searchProductSkuTask.cancel(true);
			if(keyword.length() > 0) {
				searchProductSkuTask = new SearchProductSkuTask(getActivity(), ActionbarFragment.this, SEARCH_PRODUCTS_TASKCODE);
				searchProductSkuTask.execute(keyword);
			} else {
				if(productSearchAdapter != null)
					productSearchAdapter.clear();
				productSearchLinearLayout.setVisibility(View.GONE);
			}
		}
	};
	
	public void resetSearchViews() {
		productSearchLinearLayout.setVisibility(View.GONE);
		productSearchEditText.setVisibility(View.GONE);
		productSearchButton.setVisibility(View.VISIBLE);
	}

	TextWatcher productSearchTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence charseq, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			keyStrokeTimer.cancel();
			keyStrokeTimer.start();
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}
	};

	View.OnClickListener onSearchClickListener =  new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			productSearchButton.setVisibility(View.GONE);
			productSearchEditText.setText("");
			productSearchEditText.setVisibility(View.VISIBLE);
		}
	};

	View.OnClickListener onNavigationClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.bill_navigation_button) {
				v.setVisibility(View.GONE);
			} else if(v.getId() == R.id.store_navigation_button) {
				v.setVisibility(View.GONE);
			}
			onNavigationListener.onNavigationListener(v.getId());
		}
	};

	TwoWayGridView.OnItemClickListener onShoppingCartClickListener = new TwoWayGridView.OnItemClickListener() {

		@Override
		public void onItemClick(TwoWayAdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			onShoppingCartChangeListener.onShoppingCartSwitch(shoppingCartAdapter.getItem(position));
		}

	};
	
	public void removeShoppingCart(int shoppingCartId) {
		shoppingCartAdapter.remove(shoppingCartId);
	}
	
	public void addShoppingCart(ShoppingCart shoppingCart) {
		if(shoppingCartAdapter == null) {
			shoppingCartAdapter = new ShoppingCartAdapter(getActivity(), new ArrayList<Integer>());
			shoppingCartAdapter.add(shoppingCart.getShoppingCartId());
			shoppingCartGridView.setAdapter(shoppingCartAdapter);
		} else {
			shoppingCartAdapter.add(shoppingCart.getShoppingCartId());
			shoppingCartAdapter.notifyDataSetChanged();
		}
	}
	
	public void addShoppingCart(ShoppingCart shoppingCart, int position) {
		if(shoppingCartAdapter == null) {
			shoppingCartAdapter = new ShoppingCartAdapter(getActivity(), new ArrayList<Integer>());
			shoppingCartAdapter.add(shoppingCart.getShoppingCartId());
			shoppingCartGridView.setAdapter(shoppingCartAdapter);
		} else {
			shoppingCartAdapter.insert(shoppingCart.getShoppingCartId(), position);
			shoppingCartAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if(taskCode == SEARCH_PRODUCTS_TASKCODE) {
			if(productSearchLinearLayout.getVisibility() == View.GONE) {
				productSearchLinearLayout.setVisibility(View.VISIBLE);
			}
			if(productSearchAdapter == null) {
				productSearchAdapter = new ProductSearchAdapter(getActivity(), (List<ProductSku>) responseList);
				productSearchListView.setAdapter(productSearchAdapter);
			} else {
				productSearchAdapter.clear();
				productSearchAdapter.addAll((Collection<? extends ProductSku>) responseList);
				productSearchAdapter.notifyDataSetChanged();
			}
		}

	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub

	}

	public interface OnNavigationListener {
		public void onNavigationListener(int viewId);
	}

}

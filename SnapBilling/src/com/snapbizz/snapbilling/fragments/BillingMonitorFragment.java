package com.snapbizz.snapbilling.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.BillingMonitorAdapter;
import com.snapbizz.snapbilling.adapters.BillingMonitorAdapter.CartActionListener;
import com.snapbizz.snapbilling.adapters.CustomerListAdapter;
import com.snapbizz.snapbilling.asynctasks.AddCustomerTask;
import com.snapbizz.snapbilling.asynctasks.QueryCustomerInfoTask;
import com.snapbizz.snapbilling.domains.ShoppingCart;
import com.snapbizz.snapbilling.interfaces.FragmentLoadCompleteListener;
import com.snapbizz.snaptolkit.customviews.HorizontalListView;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class BillingMonitorFragment extends Fragment implements OnQueryCompleteListener {

	private final int GET_INVENTORYSKU_TASKCODE = 0;
	private final int GET_CUSTOMERINFO_TASKCODE = 1;
	private final int ADD_CUSTOMERINFO_TASKCODE = 2;
	private HorizontalListView billingMonitorList;
	public BillingMonitorAdapter billingMonitorAdapter;
	private QueryCustomerInfoTask getCustomerInfoTask;
	private CustomerListAdapter customerAdap;
	private RelativeLayout addCustomerLayout;
	private LinearLayout customerSearchLayout;
	private CartActionListener cartActionListener;
	private int savedCustomerCartPosition = -1;
	private FragmentLoadCompleteListener fragmentLoadCompleteListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_billing_monitor_layout,
				null);
		billingMonitorList = (HorizontalListView) view
				.findViewById(R.id.billing_monitor_list);
		return view;
	}

	public void inflateAddCustomerLayout() {
		addCustomerLayout.setVisibility(View.VISIBLE);
		((Button) getView().findViewById(R.id.save_button))
		.setOnClickListener(customerAddDelListener);
		((Button) getView().findViewById(R.id.cancel_button))
		.setOnClickListener(customerAddDelListener);
	}

	OnClickListener customerAddDelListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.save_button) {
				Customer newCustomer = new Customer();
				newCustomer.setCustomerName(((EditText) getActivity()
						.findViewById(R.id.customer_name_edit_text)).getText()
						.toString());
				newCustomer.setCustomerPhoneNumber(((EditText) getActivity()
						.findViewById(R.id.customer_number_edit_text))
						.getText().toString());
				newCustomer.setCustomerAddress(((EditText) getActivity()
						.findViewById(R.id.customer_address_edit_text))
						.getText().toString());
				AddCustomerTask addNewCustomerTask = new AddCustomerTask(
						getActivity(), BillingMonitorFragment.this,
						ADD_CUSTOMERINFO_TASKCODE);
				addNewCustomerTask.execute(newCustomer);
				addCustomerLayout.setVisibility(View.GONE);
			} else if (v.getId() == R.id.cancel_button) {
				addCustomerLayout.setVisibility(View.GONE);
			}
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(billingMonitorAdapter == null)
			billingMonitorAdapter = new BillingMonitorAdapter(getActivity(),
					android.R.id.text1, new ArrayList<ShoppingCart>(), cartActionListener);
		billingMonitorList.setAdapter(billingMonitorAdapter);
		addCustomerLayout = (RelativeLayout) getView().findViewById(
				R.id.add_customer_layout);
		customerSearchLayout = (LinearLayout) getView().findViewById(
				R.id.customer_search_linearlayout);
		billingMonitorList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});
		((ListView) getView().findViewById(R.id.customer_search_result_list))
		.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				billingMonitorAdapter.getItem((Integer) parent.getTag())
				.setCustomer(customerAdap.getItem(pos));
				billingMonitorAdapter.notifyDataSetChanged();
				customerSearchLayout.setVisibility(View.GONE);
			}
		});

		customerSearchLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				customerSearchLayout.setVisibility(View.GONE);
			}
		});

		fragmentLoadCompleteListener.onFragmentLoadComplete(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			cartActionListener = (CartActionListener) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException("activity "+activity.toString()+" must implement callback CartActionListener ");
		}
		try {
			fragmentLoadCompleteListener = (FragmentLoadCompleteListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+"must implement FragmentLoadCompleteListener");
		}
	}

	public void onAddCart(ShoppingCart shoppingCart, int position) {
		billingMonitorAdapter.add(shoppingCart);
		billingMonitorAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if (taskCode == GET_INVENTORYSKU_TASKCODE) {
			//			ArrayList<InventorySku> inventoryList = (ArrayList<InventorySku>) responseList;
			//			billingMonitorAdapter.getItem(position).setProductSkuList(inventoryList);
			//			billingMonitorAdapter.notifyDataSetChanged();
		} else if (taskCode == GET_CUSTOMERINFO_TASKCODE) {
			ArrayList<Customer> customerListItems = (ArrayList<Customer>) responseList;
			if (customerListItems.size() != 0) {
				addCustomerLayout.setVisibility(View.GONE);
				ListView customerList = (ListView) getView().findViewById(
						R.id.customer_search_result_list);
				LayoutParams customerListLayoutParams = (LayoutParams) customerSearchLayout
						.getLayoutParams();
				//				customerListLayoutParams.leftMargin = (position * 320);
				//				customerList.setTag(position);
				customerAdap = new CustomerListAdapter(getActivity(),
						android.R.id.text1, customerListItems);
				customerSearchLayout.setLayoutParams(customerListLayoutParams);
				customerList.setAdapter(customerAdap);
				customerSearchLayout.setVisibility(View.VISIBLE);
			} else {
				inflateAddCustomerLayout();
				customerSearchLayout.setVisibility(View.GONE);
			}
		} else if (taskCode == ADD_CUSTOMERINFO_TASKCODE) {
			ArrayList<Customer> customerListItems = (ArrayList<Customer>) responseList;
			billingMonitorAdapter.getItem(savedCustomerCartPosition).setCustomer(
					customerListItems.get(0));
			billingMonitorAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		if (taskCode == GET_CUSTOMERINFO_TASKCODE) {
			customerSearchLayout.setVisibility(View.GONE);
		}
	}

	public void onCustomerSearch(String customerNumber) {
		if (addCustomerLayout.getVisibility() == View.VISIBLE) {
			((EditText) getActivity().findViewById(
					R.id.customer_number_edit_text)).setText(customerNumber);
		}
		getCustomerInfoTask = new QueryCustomerInfoTask(getActivity(), this,
				GET_CUSTOMERINFO_TASKCODE);
		getCustomerInfoTask.execute(customerNumber);
	}

	public void onCancelSearch(int position) {
		// TODO Auto-generated method stub
		if (getCustomerInfoTask != null)
			getCustomerInfoTask.cancel(true);
		customerSearchLayout.setVisibility(View.GONE);
	}

}

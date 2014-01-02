package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.Customer;

public class CustomerListAdapter extends ArrayAdapter<Customer> {

	private Context context;
	private LayoutInflater layoutInflater;

	public CustomerListAdapter(Context context, int textViewResourceId,
			List<Customer> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CustomerWrapper customerWrapper;
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.listitem_customer_search, null);
			customerWrapper = new CustomerWrapper();
			customerWrapper.customerImageView = (ImageView) convertView
					.findViewById(R.id.search_customer_imageview);
			customerWrapper.customerNameTextView = (TextView) convertView
					.findViewById(R.id.search_customername_textview);
			customerWrapper.customerNumberTextView = (TextView) convertView
					.findViewById(R.id.search_customernumber_textview);
			convertView.setTag(customerWrapper);
		} else {
			customerWrapper = (CustomerWrapper) convertView.getTag();
		}
		Customer customer=getItem(position);
		customerWrapper.customerNameTextView.setText(customer.getCustomerName());
		customerWrapper.customerNumberTextView.setText(customer.getCustomerPhoneNumber());
		return convertView;
	}

	private static class CustomerWrapper {
		public ImageView customerImageView;
		public TextView customerNameTextView;
		public TextView customerNumberTextView;

	}

}

package com.snapbizz.snapbilling.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domains.ShoppingCart;

public class BillingMonitorAdapter extends ArrayAdapter<ShoppingCart> {

	private LayoutInflater layoutInflater;
	private Context context;
	private CartActionListener cartActionListener;
	private ArrayList<ShoppingCart> shoppingCartList;
	private BillingMonitorAdapterWrapper billingMonitorAdapterWrapper;
	private String customerNumberText;

	public BillingMonitorAdapter(Context context, int textViewResourceId,
			ArrayList<ShoppingCart> objects, CartActionListener cartActionListener) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.shoppingCartList = objects;
		this.cartActionListener = cartActionListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.billing_monitor_item,
					null);
			billingMonitorAdapterWrapper = new BillingMonitorAdapterWrapper();
			billingMonitorAdapterWrapper.addCartImageView = (ImageView) convertView
					.findViewById(R.id.billing_monitor_add_cart);
			billingMonitorAdapterWrapper.billingListView = (ListView) convertView
					.findViewById(R.id.billing_list);
			billingMonitorAdapterWrapper.removeCartButton = (Button) convertView
					.findViewById(R.id.remove_cart_button);
			billingMonitorAdapterWrapper.searchCustomerEditText = (EditText) convertView
					.findViewById(R.id.billing_monitor_search_edittext);

			convertView.setTag(billingMonitorAdapterWrapper);
		} else {
			billingMonitorAdapterWrapper = (BillingMonitorAdapterWrapper) convertView
					.getTag();
		}

		billingMonitorAdapterWrapper.searchCustomerEditText.setTag(position);
		billingMonitorAdapterWrapper.searchCustomerEditText
		.setOnFocusChangeListener(customerSearchEditTextFocusChangeListener);
		billingMonitorAdapterWrapper.addCartImageView
		.setOnClickListener(addCartClickListener);
		billingMonitorAdapterWrapper.addCartImageView.setTag(position);
		billingMonitorAdapterWrapper.removeCartButton
		.setOnClickListener(removeCartClickListener);
		billingMonitorAdapterWrapper.searchCustomerEditText
		.addTextChangedListener(textChangeWatcher);

		/*billingMonitorAdapterWrapper.billingListView
				.setOnItemClickListener(openCartListener);*/
		billingMonitorAdapterWrapper.billingListView.setOnTouchListener(onListTouchListener);

		ShoppingCart shoppingCart = null;
		if(position < shoppingCartList.size())
			shoppingCart = getItem(position);

		if (shoppingCart == null || !shoppingCart.isCartCreated()) {
			billingMonitorAdapterWrapper.addCartImageView
			.setVisibility(View.VISIBLE);
			billingMonitorAdapterWrapper.billingListView
			.setVisibility(View.GONE);
			if(shoppingCart == null)
				return convertView;
		} else {
			billingMonitorAdapterWrapper.addCartImageView
			.setVisibility(View.GONE);
			billingMonitorAdapterWrapper.billingListView
			.setVisibility(View.VISIBLE);
			if (shoppingCart.getProductSkuList() != null) {
				BillingMonitorListAdapter listadap = new BillingMonitorListAdapter(context,
						shoppingCart.getProductSkuList());
				billingMonitorAdapterWrapper.billingListView
				.setAdapter(listadap);
			}
		}

		billingMonitorAdapterWrapper.removeCartButton.setTag(shoppingCart.getShoppingCartId());
		billingMonitorAdapterWrapper.billingListView.setTag(shoppingCart.getShoppingCartId());
		if (shoppingCart.getCustomer() != null) {
			billingMonitorAdapterWrapper.searchCustomerEditText
			.setText(shoppingCart.getCustomer()
					.getCustomerPhoneNumber());
		} else {
			billingMonitorAdapterWrapper.searchCustomerEditText.setText("");
		}


		return convertView;
	}

	OnTouchListener onListTouchListener = new OnTouchListener() {
		long downtime = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			boolean cancelTouch=false;

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				downtime=event.getEventTime();
				// Do what you want
			}
			// TODO Auto-generated method stub
			else if (event.getAction() == MotionEvent.ACTION_UP) {
				if(event.getEventTime()-downtime>250 && !cancelTouch){
					cartActionListener.onOpenCart((Integer) v.getTag());

				}
				// Do what you want
			} else if (event.getAction() == MotionEvent.ACTION_SCROLL) {
				cancelTouch=true;
				Toast.makeText(context, "scrolling",0).show();
				// Do what you want
			}
			return false;
		}
	};

	OnItemClickListener openCartListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long arg3) {
			// TODO Auto-generated method stub
			cartActionListener.onOpenCart((Integer) parent.getTag());
		}
	};

	CountDownTimer keyStrokeTimer = new CountDownTimer(300, 300) {
		@Override
		public void onTick(long arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			cartActionListener.onCustomerSearch(customerNumberText);
		}
	};

	OnFocusChangeListener customerSearchEditTextFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if (hasFocus)
				cartActionListener.onCancelSearch((Integer) v.getTag());
		}
	};

	TextWatcher textChangeWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			customerNumberText = s.toString();
			keyStrokeTimer.cancel();
			keyStrokeTimer.start();

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

	};

	private OnClickListener addCartClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position  = (Integer) v.getTag();
			if(position < shoppingCartList.size()) {
				shoppingCartList.get(position).setCartCreated(true);
				notifyDataSetChanged();
				cartActionListener.onAddCart(position, false);
			} else
				cartActionListener.onAddCart(position, true);
		}
	};

	private OnClickListener removeCartClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			cartActionListener.onRemoveCart((Integer) v.getTag());
		}
	};

	@Override
	public int getCount() {
		if(shoppingCartList.size() < 4)
			return shoppingCartList.size() + 1;
		else
			return shoppingCartList.size();
	};

	private static class BillingMonitorAdapterWrapper {
		public ImageView addCartImageView;
		public ListView billingListView;
		public EditText searchCustomerEditText;
		public Button removeCartButton;
	}

	public interface CartActionListener {
		public void onAddCart(int position, boolean isNewCart);

		public void onRemoveCart(int position);

		public void onOpenCart(int position);

		public void onCustomerSearch(String customerNumber);

		public void onCancelSearch(int position);

	}

}

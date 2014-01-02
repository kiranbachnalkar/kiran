package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domains.ShoppingCart;

public class ShoppingCartAdapter extends ArrayAdapter<Integer> {

	private LayoutInflater layoutInflater;

	public ShoppingCartAdapter(Context context, List<Integer> shoppingCartList) {
		super(context, android.R.id.text1, shoppingCartList);
		// TODO Auto-generated constructor stub
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ShoppingCartAdapterWrapper shoppingCartAdapterWrapper;
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.listitem_shoppingcart, null);
			shoppingCartAdapterWrapper = new ShoppingCartAdapterWrapper();
			shoppingCartAdapterWrapper.shoppingCartTextView = (TextView) convertView.findViewById(R.id.shoppingcart_textview);
			convertView.setTag(shoppingCartAdapterWrapper);
		} else {
			shoppingCartAdapterWrapper = (ShoppingCartAdapterWrapper) convertView.getTag();
		}
		shoppingCartAdapterWrapper.shoppingCartTextView.setText(getItem(position)+"");
		return convertView;
	}
 
	public static class ShoppingCartAdapterWrapper {
		public TextView shoppingCartTextView;
	}

	public interface OnShoppingCartChangeListener {
		public void onShoppingCartSwitch(Integer shoppingCartId);
	}

}

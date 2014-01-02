package com.snapbizz.snapbilling.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.ProductSku;

public class VirtualStoreProductAdapter extends ArrayAdapter<ProductSku> {

	private LayoutInflater layoutInflater;
	private Context context;

	public VirtualStoreProductAdapter(Context context, ArrayList<ProductSku> productList) {
		super(context, android.R.id.text1, productList);
		// TODO Auto-generated constructor stub
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ProductAdapterWrapper productAdapterWrapper;
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.griditem_store_product, null);
			productAdapterWrapper = new ProductAdapterWrapper();
			productAdapterWrapper.productImageView = (ImageView) convertView.findViewById(R.id.product_imageview);
			productAdapterWrapper.productNameTextView = (TextView) convertView.findViewById(R.id.product_name_textview);
			convertView.setTag(productAdapterWrapper);
		} else {
			productAdapterWrapper = (ProductAdapterWrapper) convertView.getTag();
		}
		productAdapterWrapper.productImageView.setImageDrawable(SnapBillingUtils.getProductDrawable(getItem(position).getProductSkuCode(), context));
		productAdapterWrapper.productNameTextView.setText(SnapBillingTextFormatter.formatProductNameText(getItem(position).getProductSkuName()));
		return convertView;
	}

	private static class ProductAdapterWrapper {
		public ImageView productImageView;
		public TextView productNameTextView;
	}

}

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
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.ProductSku;

public class ProductSearchAdapter extends ArrayAdapter<ProductSku> {

	private LayoutInflater inflater;
	private Context context;

	public ProductSearchAdapter(Context context,
			List<ProductSku> objects) {
		super(context, android.R.id.text1, objects);
		// TODO Auto-generated constructor stub
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ProductSearchAdapterWrapper productSearchAdapterWrapper;
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_product_search, null);
			productSearchAdapterWrapper = new ProductSearchAdapterWrapper();
			productSearchAdapterWrapper.produtNameTextView = (TextView) convertView.findViewById(R.id.search_productname_textview);
			productSearchAdapterWrapper.productImageView = (ImageView) convertView.findViewById(R.id.search_product_imageview);
			convertView.setTag(productSearchAdapterWrapper);
		} else {
			productSearchAdapterWrapper = (ProductSearchAdapterWrapper) convertView.getTag();
		}
		productSearchAdapterWrapper.produtNameTextView.setText(getItem(position).getProductSkuName());
		productSearchAdapterWrapper.productImageView.setImageDrawable(SnapBillingUtils.getProductDrawable(getItem(position).getProductSkuCode(), context));
		
		return convertView;
	}

	public static class ProductSearchAdapterWrapper {
		public TextView produtNameTextView;
		public ImageView productImageView;
	}

}

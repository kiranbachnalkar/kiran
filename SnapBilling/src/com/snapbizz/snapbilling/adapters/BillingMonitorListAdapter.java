package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.domains.InventorySku;

public class BillingMonitorListAdapter extends ArrayAdapter<InventorySku> {

	private LayoutInflater layoutInflater;
	private Context context;
	
	public BillingMonitorListAdapter(Context context, List<InventorySku> productList) {
		super(context, android.R.id.text1, productList);
		// TODO Auto-generated constructor stub
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		BillListItemWrapper billListItemWrapper;
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.listitem_bill, null);
			billListItemWrapper = new BillListItemWrapper();
			billListItemWrapper.productNameTextView = (TextView) convertView.findViewById(R.id.bill_productname_textview);
			billListItemWrapper.productQuantityTextView = (TextView) convertView.findViewById(R.id.bill_productquantity_textview);
			billListItemWrapper.productMrpTextView = (TextView) convertView.findViewById(R.id.bill_productmrp_textview);
			convertView.setTag(billListItemWrapper);
		} else {
			billListItemWrapper = (BillListItemWrapper) convertView.getTag();
		}
		InventorySku productSku = getItem(position);
		billListItemWrapper.productNameTextView.setText(productSku.getProductSku().getProductSkuName());
		billListItemWrapper.productQuantityTextView.setText(productSku.getQuantity()+"");
		billListItemWrapper.productMrpTextView.setText(SnapBillingTextFormatter.formatPriceText((productSku.getQuantity() / productSku.getProductSku().getProductSkuBaseQuantity()) * productSku.getProductSku().getProductSkuMrp(), context));
		return convertView;
	}

	private static class BillListItemWrapper {
		public TextView productNameTextView;
		public TextView productQuantityTextView;
		public TextView productMrpTextView;
	}
	
}

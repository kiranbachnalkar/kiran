package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.InventorySku;

public class BillListAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private Context context;
	private BillItemEditListener billItemEditListener;
	private List<InventorySku> productList;
	
	public BillListAdapter(Context context, List<InventorySku> productList, BillItemEditListener billItemEditListener) {
		// TODO Auto-generated constructor stub
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.billItemEditListener = billItemEditListener;
		this.productList = productList;
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
			billListItemWrapper.productTotalTextView = (TextView) convertView.findViewById(R.id.bill_producttotal_textview);
			billListItemWrapper.productSlnoTextView = (TextView) convertView.findViewById(R.id.bill_productslno_textview);
			convertView.setTag(billListItemWrapper);
		} else {
			billListItemWrapper = (BillListItemWrapper) convertView.getTag();
		}
		InventorySku billItem = getItem(position);
		billListItemWrapper.productSlnoTextView.setText((position+1)+".");
		billListItemWrapper.productNameTextView.setText(SnapBillingTextFormatter.formatProductNameText(billItem.getProductSku().getProductSkuName()));
		billListItemWrapper.productQuantityTextView.setText(billItem.getQuantity()+"");
		billListItemWrapper.productMrpTextView.setText(SnapBillingUtils.calculateandFormatProductPrice(billItem.getProductSku(), context));
		billListItemWrapper.productTotalTextView.setText(SnapBillingTextFormatter.formatPriceText((float)((float)billItem.getQuantity() * billItem.getProductSku().getProductSkuSalePrice()), context));
		billListItemWrapper.productNameTextView.setTag(position);
		billListItemWrapper.productMrpTextView.setTag(position);
		billListItemWrapper.productQuantityTextView.setTag(position);
		billListItemWrapper.productTotalTextView.setTag(position);
		billListItemWrapper.productNameTextView.setOnClickListener(onEditProductClickListener);
		billListItemWrapper.productMrpTextView.setOnClickListener(onEditProductClickListener);
		billListItemWrapper.productTotalTextView.setOnClickListener(onEditProductClickListener);
		billListItemWrapper.productQuantityTextView.setOnClickListener(onEditProductClickListener);
		return convertView;
	}

	View.OnClickListener onEditProductClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.bill_productname_textview) {
				billItemEditListener.onProductEdit((Integer) v.getTag());
			} else if(v.getId() == R.id.bill_productquantity_textview) {
				billItemEditListener.onQuantityEdit((Integer) v.getTag());
			} else if(v.getId() == R.id.bill_productmrp_textview) {
				billItemEditListener.onPriceEdit((Integer) v.getTag());
			} else if(v.getId() == R.id.bill_producttotal_textview) {
				billItemEditListener.onPriceEdit((Integer) v.getTag());
			}
		}
	};
	
	private static class BillListItemWrapper {
		public TextView productSlnoTextView;
		public TextView productNameTextView;
		public TextView productQuantityTextView;
		public TextView productMrpTextView;
		public TextView productTotalTextView;
	}
	
	public interface BillItemEditListener {
		public void onProductEdit(int position);
		public void onQuantityEdit(int position);
		public void onPriceEdit(int position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return productList.size();
	}

	@Override
	public InventorySku getItem(int position) {
		// TODO Auto-generated method stub
		return productList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setProductList(List<InventorySku> productSkuList) {
		this.productList = productSkuList;
	}
	
	public void remove(int position) {
		// TODO Auto-generated method stub
		productList.remove(position);
	}
	
}

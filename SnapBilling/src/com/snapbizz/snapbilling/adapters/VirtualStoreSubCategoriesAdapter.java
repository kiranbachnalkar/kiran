package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.ProductCategory;

public class VirtualStoreSubCategoriesAdapter extends ArrayAdapter<ProductCategory> {

	private LayoutInflater layoutInflater;
	private OnSubCategoryClickListener onSubCategoryClickListener;

	public VirtualStoreSubCategoriesAdapter(Context context, List<ProductCategory> productCategoryList, OnSubCategoryClickListener onSubCategoryClickListener) {
		super(context, android.R.id.text1, productCategoryList);
		// TODO Auto-generated constructor stub
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onSubCategoryClickListener = onSubCategoryClickListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView categoryTextView;
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.griditem_store_category, null);
		}
		categoryTextView = (TextView) convertView;
		categoryTextView.setTag(getItem(position));
		categoryTextView.setOnClickListener(onSubCategoryTextViewClickListener);
		categoryTextView.setText(getItem(position).getCategoryName());
		return convertView;
	}

	View.OnClickListener onSubCategoryTextViewClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onSubCategoryClickListener.onSubCategoryClick((ProductCategory) v.getTag());
		}
	};

	public interface OnSubCategoryClickListener {
		public void onSubCategoryClick(ProductCategory productCategory);
	}

}

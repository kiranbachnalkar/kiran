package com.snapbizz.snapbilling.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.domains.ProductSku;

public class EditProductDialogFragment extends DialogFragment {

	private ProductSku productSku;
	private EditProductDialogListener editProductDialogListener;
	private EditText qtyEditText;

	public void setProductSku(ProductSku productSku) {
		this.productSku = productSku;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			editProductDialogListener = (EditProductDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnEditProductListener");
		}
	}

	View.OnClickListener onQtyEditClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.decrease_qty_button) {
				int qty = Integer.parseInt(qtyEditText.getText().toString());
				if(qty != 0)
					qtyEditText.setText((qty - 1)+"");
			} else {
				qtyEditText.setText((Integer.parseInt(qtyEditText.getText().toString()) + 1)+"");
			}
		}
	};

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final View view = inflater.inflate(R.layout.layout_addproduct, null);
		((TextView) view.findViewById(R.id.productsku_name_textview)).setText(productSku.getProductSkuName());
		((TextView) view.findViewById(R.id.productsku_mrp_textview)).setText(SnapBillingTextFormatter.formatPriceText(productSku.getProductSkuMrp(), getActivity()));
		qtyEditText = (EditText) view.findViewById(R.id.qty_edittext);
		view.findViewById(R.id.increase_qty_button).setOnClickListener(onQtyEditClickListener);
		view.findViewById(R.id.decrease_qty_button).setOnClickListener(onQtyEditClickListener);
		builder.setView(view);
		builder
		.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				int qty = Integer.parseInt(qtyEditText.getText().toString());
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(qtyEditText.getWindowToken(), 0);
				if(qty > 0) {
					editProductDialogListener.onAddProduct(productSku, qty);
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(qtyEditText.getWindowToken(), 0);
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	public interface EditProductDialogListener {
		public void onAddProduct(ProductSku productSku, int quantity);
	}

}

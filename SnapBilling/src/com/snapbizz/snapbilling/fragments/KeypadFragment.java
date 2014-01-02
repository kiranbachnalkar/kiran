package com.snapbizz.snapbilling.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;

public class KeypadFragment extends Fragment {

	private TextView mValueTextView;
	private float startingValue = 0;
	private int context;
	private KeyboardEnterListener keyboardEnterListener;
	private boolean isNewEdit = true;
	private boolean isDecimalUsed = false;
	private boolean isShowDiscountEnabled = false;
	private boolean isDiscountModeEnabled = false;;
	private View discountView;
	private float totalValue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.layout_editquantity, null);
		view.findViewById(R.id.button0).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button1).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button2).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button3).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button4).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button5).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button6).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button7).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button8).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button9).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.buttondot).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.buttonenter).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button_discount1).setOnClickListener(onDiscountClickListener);
		view.findViewById(R.id.button_discount2).setOnClickListener(onDiscountClickListener);
		view.findViewById(R.id.button_discount3).setOnClickListener(onDiscountClickListener);
		view.findViewById(R.id.button_increase).setOnClickListener(onValueChangeClickListener);
		view.findViewById(R.id.button_decrease).setOnClickListener(onValueChangeClickListener);
		discountView = view.findViewById(R.id.discount_linearlayout);
		if(isShowDiscountEnabled)
			discountView.setVisibility(View.VISIBLE);
		else
			discountView.setVisibility(View.GONE);
		mValueTextView = (TextView) view.findViewById(R.id.value_textview);
		mValueTextView.setText(startingValue+"");
		return view;
	}

	public void enableDiscountMode() {
		isDiscountModeEnabled = true;
	}

	public void setContext(int context) {
		this.context = context;
	}

	public void setKeypadEnterListener(KeyboardEnterListener keyboardEnterListener) {
		this.keyboardEnterListener = keyboardEnterListener;
	}

	public void setCurrentValue(float value) {
		if(mValueTextView != null) {
			Log.d("keypad", "value "+value);
			mValueTextView.setText(value+"");
		}
		startingValue = value;
	}

	public void setTotalValue(float totalValue) {
		this.totalValue = totalValue;
	}

	public void showDiscount() {
		if(discountView != null)
			discountView.setVisibility(View.VISIBLE);
		isShowDiscountEnabled = true;
	}

	public void hideDiscount() {
		if(discountView != null)
			discountView.setVisibility(View.GONE);
		isShowDiscountEnabled = false;
	}

	View.OnClickListener onDiscountClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isNewEdit = true;
			isDecimalUsed = false;
			float value = startingValue;
			if(isDiscountModeEnabled) {
				value = totalValue;
				value *= Integer.parseInt((String) v.getTag());
			} else {
				value *= 100 - Integer.parseInt((String) v.getTag());
			}

			mValueTextView.setText(((float)value / 100)+"");
			Log.d("keypad", "final value "+value+ "discount "+(Integer.parseInt((String) v.getTag())));
		}
	};

	View.OnClickListener onValueChangeClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isNewEdit = true;
			isDecimalUsed = false;
			float value = Float.parseFloat(mValueTextView.getText().toString());
			if(v.getId() == R.id.button_increase) {
				mValueTextView.setText((value+1)+"");
			} else if(v.getId() ==R.id.button_decrease) {
				if(value >= 1)
					mValueTextView.setText((value-1)+"");
			}
		}
	};

	View.OnClickListener onKeyClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int value = Integer.parseInt((String) v.getTag());
			if(value == getResources().getInteger(R.integer.button_enter)) {
				isNewEdit = true;
				isDecimalUsed = false;
				isDiscountModeEnabled = false;
				float val;
				if(mValueTextView.getText().charAt(mValueTextView.length() - 1) == '.')
					mValueTextView.append("0");
				if(mValueTextView.length() == 0)
					val = 0;
				else 
					val = Float.parseFloat(mValueTextView.getText().toString());
				keyboardEnterListener.onKeyBoardEnter(val, context);
			} else {
				if(isNewEdit) {
					mValueTextView.setText("");
					isNewEdit = false;
				}
				String val;
				if(value == getResources().getInteger(R.integer.button_dot)) {
					if(!isDecimalUsed) {
						val = ".";
						isDecimalUsed = true;
						if(mValueTextView.length() == 0)
							mValueTextView.setText("0");
						mValueTextView.append(val);
					}
				} else {
					val = ""+value;
					mValueTextView.append(val);
				}
			}
		}
	};

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	public interface KeyboardEnterListener {
		public void onKeyBoardEnter(float value, int context);
	}

}

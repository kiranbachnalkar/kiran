package com.snapbizz.snapbilling.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.VirtualStoreCategoriesAdapter;
import com.snapbizz.snapbilling.adapters.VirtualStoreCategoriesAdapter.OnCategoryClickListener;
import com.snapbizz.snapbilling.adapters.VirtualStoreProductAdapter;
import com.snapbizz.snapbilling.adapters.VirtualStoreSubCategoriesAdapter;
import com.snapbizz.snapbilling.adapters.VirtualStoreSubCategoriesAdapter.OnSubCategoryClickListener;
import com.snapbizz.snapbilling.asynctasks.GetSubCategoryProductsTask;
import com.snapbizz.snapbilling.asynctasks.QueryProductCategories;
import com.snapbizz.snapbilling.asynctasks.QueryProductSubCategories;
import com.snapbizz.snapbilling.interfaces.FragmentLoadCompleteListener;
import com.snapbizz.snapbilling.interfaces.SearchedProductClickListener;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class VirtualStoreFragment extends Fragment implements OnSubCategoryClickListener, OnCategoryClickListener, OnQueryCompleteListener {

	private final int GET_CATEGORIES_TASKCODE = 0;
	private final int GET_SUBCATEGORIES_TASKCODE = 1;
	private final int GET_PRODUCTS_TASKCODE = 2;

	private VirtualStoreProductAdapter productAdapter;
	private VirtualStoreCategoriesAdapter categoryAdapter;
	private VirtualStoreSubCategoriesAdapter subCategoryAdapter;
	private TwoWayGridView subcategoryGridView;
	private TwoWayGridView productGridView;
	private SearchedProductClickListener searchedProductClickListener;
	private FragmentLoadCompleteListener fragmentLoadCompleteListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_virtual_store, null);
		subcategoryGridView = (TwoWayGridView) view.findViewById(R.id.store_subcategories_gridview);
		productGridView = (TwoWayGridView) view.findViewById(R.id.store_products_gridview);
		productGridView.setOnItemClickListener(onProductClickListener);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			searchedProductClickListener = (SearchedProductClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+"must implement SearchedProductClickListener");
		}
		try {
			fragmentLoadCompleteListener = (FragmentLoadCompleteListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+"must implement FragmentLoadCompleteListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if(productAdapter != null)
			productGridView.setAdapter(productAdapter);
		if(subCategoryAdapter != null)
			subcategoryGridView.setAdapter(subCategoryAdapter);
		new QueryProductCategories(getActivity(), this, GET_CATEGORIES_TASKCODE).execute();
		fragmentLoadCompleteListener.onFragmentLoadComplete(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCategoryClick(ProductCategory productCategory) {
		// TODO Auto-generated method stub
		new QueryProductSubCategories(getActivity(), this, GET_SUBCATEGORIES_TASKCODE).execute(productCategory.getCategoryId());
	}

	@Override
	public void onSubCategoryClick(ProductCategory productSubCategory) {
		// TODO Auto-generated method stub
		new GetSubCategoryProductsTask(getActivity(), this, GET_PRODUCTS_TASKCODE).execute(productSubCategory.getCategoryId());
	}

	TwoWayAdapterView.OnItemClickListener onProductClickListener = new TwoWayAdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(TwoWayAdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			searchedProductClickListener.onSearchedProductClick(productAdapter.getItem(position));
		}

	};
	
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if(taskCode == GET_CATEGORIES_TASKCODE) {
			Log.d(VirtualStoreFragment.class.getName(), "category response list size "+((List<ProductCategory>) responseList).size());
			categoryAdapter = new VirtualStoreCategoriesAdapter(getActivity(), (List<ProductCategory>) responseList, this);
			((TwoWayGridView) getView().findViewById(R.id.store_categories_gridview)).setAdapter(categoryAdapter);
		} else if(taskCode == GET_SUBCATEGORIES_TASKCODE) {
			Log.d(VirtualStoreFragment.class.getName(), "subcategory response list size "+((List<ProductCategory>) responseList).size());
			if(subCategoryAdapter == null) {
				subCategoryAdapter = new VirtualStoreSubCategoriesAdapter(getActivity(), ((List<ProductCategory>) responseList), this);
				subcategoryGridView.setAdapter(subCategoryAdapter);
			} else {
				subCategoryAdapter.clear();
				if(productAdapter != null)
					productAdapter.clear();
				subCategoryAdapter.addAll(((List<ProductCategory>) responseList));
				subCategoryAdapter.notifyDataSetChanged();
			}
		} else if(taskCode == GET_PRODUCTS_TASKCODE) {
			if(productAdapter == null) {
				productAdapter = new VirtualStoreProductAdapter(getActivity(), (ArrayList<ProductSku>) responseList);
				productGridView.setAdapter(productAdapter);
			} else {
				productAdapter.clear();
				productAdapter.addAll((Collection<? extends ProductSku>) responseList);
				productAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		if(taskCode == GET_SUBCATEGORIES_TASKCODE) {
			if(subCategoryAdapter != null) {
				subCategoryAdapter.clear();
				subCategoryAdapter.notifyDataSetChanged();
			}
		} else if(taskCode == GET_PRODUCTS_TASKCODE) {
			if(productAdapter != null) {
				productAdapter.clear();
				productAdapter.notifyDataSetChanged();
			}
		}
	}

}

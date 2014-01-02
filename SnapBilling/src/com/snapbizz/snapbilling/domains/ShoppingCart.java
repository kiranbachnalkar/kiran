package com.snapbizz.snapbilling.domains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;

public class ShoppingCart {

	private static int shoppingCartListSize = 0;
	private int shoppingCartId;
	private List<InventorySku> productSkuList;
	private HashMap<String, Integer> productSkuMap;
	private Customer customer;
	private float totalCartValue;
	private float totalPayableValue;
	private float totalDiscount;
	
	public ShoppingCart() {
		shoppingCartId = shoppingCartListSize++;
	}
	public HashMap<String, Integer> getProductSkuMap() {
		return productSkuMap;
	}
	public void setProductSkuMap(HashMap<String, Integer> productSkuMap) {
		this.productSkuMap = productSkuMap;
	}
	public float getTotalCartValue() {
		return totalCartValue;
	}
	public void setTotalCartValue(float totalCartValue) {
		this.totalCartValue = totalCartValue;
	}
	public float getTotalPayableValue() {
		return totalPayableValue;
	}
	public void setTotalPayableValue(float totalPayableValue) {
		this.totalPayableValue = totalPayableValue;
	}
	public float getTotalDiscount() {
		return totalDiscount;
	}
	public void setTotalDiscount(float totalDiscount) {
		this.totalDiscount = totalDiscount;
	}
	private boolean isCartCreated = true;

	public boolean isCartCreated() {
		return isCartCreated;
	}
	public void setCartCreated(boolean isCartCreated) {
		this.isCartCreated = isCartCreated;
	}
	public int getShoppingCartId() {
		return shoppingCartId;
	}
	public void setShoppingCartId(int shoppingCartId) {
		this.shoppingCartId = shoppingCartId;
	}
	public List<InventorySku> getProductSkuList() {
		return productSkuList;
	}
	public void setProductSkuList(List<InventorySku> inventorySkuList) {
		this.productSkuList = inventorySkuList;
	}
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void deleteCart() {
		isCartCreated = false;
		this.productSkuList.clear();
		this.productSkuMap.clear();
		customer = null;
		totalCartValue = 0;
		totalPayableValue = 0;
		totalDiscount = 0;
	}
	
	public void addItemsToCart(List<ProductSku> productSkuList) {
		if (this.productSkuList == null) {
			this.productSkuList = new ArrayList<InventorySku>();
			this.productSkuMap = new HashMap<String, Integer>();
		}
		for(ProductSku productSku : productSkuList) {
			if(this.productSkuMap.containsKey(productSku.getProductSkuCode())) {
				this.productSkuList.get(productSkuMap.get(productSku.getProductSkuCode())).incrementSkuQuantity();
			} else {
				this.productSkuMap.put(productSku.getProductSkuCode(),
						this.productSkuList.size());
				this.productSkuList.add(new InventorySku(productSku));
			}
		}
	}

	public void addItemToCart(ProductSku productSku, int quantity) {
		if (this.productSkuList == null) {
			this.productSkuList = new ArrayList<InventorySku>();
			this.productSkuMap = new HashMap<String, Integer>();
		}
		
		if(this.productSkuMap.containsKey(productSku.getProductSkuCode())) {
			this.productSkuList.get(productSkuMap.get(productSku.getProductSkuCode())).addSkuQuantity(quantity);
		} else {
			this.productSkuMap.put(productSku.getProductSkuCode(),
					this.productSkuList.size());
			this.productSkuList.add(new InventorySku(productSku, quantity));
		}

	}

}

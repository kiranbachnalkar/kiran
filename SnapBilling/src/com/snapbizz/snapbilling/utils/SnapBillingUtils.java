package com.snapbizz.snapbilling.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.snapbizz.snapbilling.domains.ShoppingCart;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class SnapBillingUtils extends SnapCommonUtils {

	private static Context snapdataContext;

	public static Drawable getProductDrawable(String productCode, Context context) {
		productCode = "snapbizz_"+productCode.replaceAll("-", "_");
		try {
			if(snapdataContext == null)
				snapdataContext = context.createPackageContext( "com.snapbizz.snapbizzdata",Context.CONTEXT_IGNORE_SECURITY);
			return snapdataContext.getResources().getDrawable(snapdataContext.getResources()
					.getIdentifier(productCode.toLowerCase(), "drawable", snapdataContext.getPackageName()));
		} catch(Exception e) {
			e.printStackTrace();
			return context.getResources().getDrawable(R.drawable.ic_launcher);
		}
	}

	public static float totalShoppingCartPrice(ShoppingCart shoppingCart) {
		float totalPrice = 0;
		for(InventorySku inventorySku : shoppingCart.getProductSkuList()) {
			totalPrice += inventorySku.getProductSku().getProductSkuSalePrice() * inventorySku.getQuantity();
		}
		return totalPrice;
	}

	public static String calculateandFormatProductPrice(ProductSku productSku, Context context) {
		if(productSku.getProductSkuSalePrice() == productSku.getProductSkuMrp()) {
			return SnapBillingTextFormatter.formatPriceText(productSku.getProductSkuMrp(), context);
		} else {
			if(productSku.getProductSkuSalePrice() > productSku.getProductSkuMrp())
				return SnapBillingTextFormatter.formatPriceText(productSku.getProductSkuSalePrice(), context);
			else {
				return SnapBillingTextFormatter.formatPriceText(productSku.getProductSkuMrp(), context) + " (-"+SnapBillingTextFormatter.formatPriceText((productSku.getProductSkuMrp() - productSku.getProductSkuSalePrice()), context).substring(1)+")";
			}
		}
	}

}

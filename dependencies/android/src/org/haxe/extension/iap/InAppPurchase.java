package org.haxe.extension.iap;

import java.util.Arrays;
import java.util.List;
import android.content.Intent;
import android.util.Log;
import org.haxe.extension.iap.util.*;
import org.haxe.extension.Extension;
import org.haxe.lime.HaxeObject;
import org.json.JSONException;
import com.android.vending.billing.IInAppBillingService;

public class InAppPurchase extends Extension {
	private static HaxeObject callback = null;
	private static IabHelper inAppPurchaseHelper;
	private static IInAppBillingService service = null;
	private static String publicKey = "";

	public static void buy(final String productID, final String devPayload) {
		// IabHelper.launchPurchaseFlow() must be called from the main activity's UI thread
		Extension.mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				InAppPurchase.inAppPurchaseHelper.launchPurchaseFlow(Extension.mainActivity, productID, 1001, mPurchaseFinishedListener, devPayload);
			}
		});
	}

	public static void consume(final String purchaseJson, final String itemType, final String signature) {
		Extension.callbackHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					final Purchase purchase = new Purchase(itemType, purchaseJson, signature);
					InAppPurchase.inAppPurchaseHelper.consumeAsync(purchase, mConsumeFinishedListener);
				} catch (JSONException e) {
					// This is not a normal consume failure, just a Json parsing error
					Extension.callbackHandler.post(new Runnable() {
						@Override
						public void run() {
							String resultJson = "{\"response\": -999, \"message\":\"Json Parse Error \"}";
							InAppPurchase.callback.call("onFailedConsume", new Object[]{("{\"result\":" + resultJson + ", \"product\":" + null + "}")});
						}
					});
				}
			}
		});

	}

	public static void queryInventory(final boolean querySkuDetails, String[] moreSkusArr) {
		final List<String> moreSkus = Arrays.asList(moreSkusArr);
		Extension.mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					InAppPurchase.inAppPurchaseHelper.queryInventoryAsync(querySkuDetails, moreSkus, mGotInventoryListener);
				} catch (Exception e) {
					Log.d("IAP", e.getMessage());
				}
			}
		});
	}

	public static void initialize(String publicKey, HaxeObject callback) {
		Log.i("IAP", "Initializing billing service");

		InAppPurchase.publicKey = publicKey;
		InAppPurchase.callback = callback;

		if (InAppPurchase.inAppPurchaseHelper != null) {
			InAppPurchase.inAppPurchaseHelper.dispose();
		}

		InAppPurchase.inAppPurchaseHelper = new IabHelper(Extension.mainContext, publicKey);
		InAppPurchase.inAppPurchaseHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(final IabResult result) {
				Extension.callbackHandler.post(new Runnable() {
					@Override
					public void run() {
						InAppPurchase.callback.call("onStarted", new Object[]{(result.isSuccess()) ? "Success" : "Failure"});
					}
				});
			}
		});
	}


	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (inAppPurchaseHelper != null) {
			return !inAppPurchaseHelper.handleActivityResult(requestCode, resultCode, data);
		}
		return super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onDestroy() {
		if (InAppPurchase.inAppPurchaseHelper != null) {
			InAppPurchase.inAppPurchaseHelper.dispose();
			InAppPurchase.inAppPurchaseHelper = null;
		}
	}


	static String resultAndPurchaseToJson(IabResult result, Purchase purchase) {
		return "{" +
				"\"result\":" + result.toJsonString() +
				"\"product\":" + ((purchase != null) ? purchase.getOriginalJson() : "null") +
				"\"type\":" + ((purchase != null) ? purchase.getItemType() : "null") +
				"\"signature\":" + ((purchase != null) ? purchase.getSignature() : "null") +
				"}";
	}


	static IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(final IabResult result, final Inventory inventory) {
			Extension.callbackHandler.post(new Runnable() {
				@Override
				public void run() {
					String target = (result.isFailure()) ? "onQueryInventoryFailed" : "onQueryInventoryComplete";
					String inventoryJSON = (inventory != null) ? inventory.toJsonString() : "";
					String json = "{\"result\":" + result.toJsonString() + ", \"inventory\":" + inventoryJSON + "}";
					Log.d("IAP", "query inventory finished: " + json);
					InAppPurchase.callback.call(target, new Object[]{json});
				}
			});
		}
	};


	static IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(final IabResult result, final Purchase purchase) {
			Extension.callbackHandler.post(new Runnable() {
				@Override
				public void run() {
					String target;
					if (!result.isFailure()) {
						target = "onPurchase";
					} else if (result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED) {
						target = "onCanceledPurchase";
					} else {
						target = "onFailedPurchase";
					}
					String json = resultAndPurchaseToJson(result, purchase);
					Log.d("IAP", "purchase finished: " + json);
					InAppPurchase.callback.call(target, new Object[]{json});
				}
			});
		}
	};


	static IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(final Purchase purchase, final IabResult result) {
			Extension.callbackHandler.post(new Runnable() {
				@Override
				public void run() {
					String target = (result.isFailure()) ? "onFailedConsume" : "onConsume";
					String json = resultAndPurchaseToJson(result, purchase);
					Log.d("IAP", "consume finished: " + json);
					InAppPurchase.callback.call(target, new Object[]{(json)});
				}
			});
		}
	};
}

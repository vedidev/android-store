/*
 * Copyright (C) 2012 Soomla Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soomla.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.soomla.store.billing.Consts;
import com.soomla.store.billing.GooglePlayIabService;
import com.soomla.store.billing.IabCallbacks;
import com.soomla.store.billing.IabException;
import com.soomla.store.billing.IabResult;
import com.soomla.store.billing.Purchase;
import com.soomla.store.data.ObscuredSharedPreferences;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.MarketItem;
import com.soomla.store.domain.NonConsumableItem;
import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.events.BillingNotSupportedEvent;
import com.soomla.store.events.BillingSupportedEvent;
import com.soomla.store.events.IabServiceStartedEvent;
import com.soomla.store.events.IabServiceStoppedEvent;
import com.soomla.store.events.ItemPurchasedEvent;
import com.soomla.store.events.PlayPurchaseCancelledEvent;
import com.soomla.store.events.PlayPurchaseEvent;
import com.soomla.store.events.PlayPurchaseStartedEvent;
import com.soomla.store.events.PlayRefundEvent;
import com.soomla.store.events.RestoreTransactionsEvent;
import com.soomla.store.events.RestoreTransactionsStartedEvent;
import com.soomla.store.events.StoreControllerInitializedEvent;
import com.soomla.store.events.UnexpectedStoreErrorEvent;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

/**
 * This class holds the basic assets needed to operate the Store.
 * You can use it to purchase products from Google Play.
 *
 * This is the only class you need to initialize in order to use the SOOMLA SDK.
 *
 * To properly work with this class you must initialize it with the @{link #initialize} method.
 */
public class StoreController {

    /**
     * This initializer also initializes {@link StoreInfo}.
     * @param storeAssets is the definition of your application specific assets.
     * @param publicKey is the public key given to you from Google.
     * @param customSecret is your encryption secret (it's used to encrypt your data in the database)
     */
    public boolean initialize(IStoreAssets storeAssets, String publicKey, String customSecret) {
        if (mInitialized) {
            String err = "StoreController is already initialized. You can't initialize it twice!";
            StoreUtils.LogError(TAG, err);
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(err));
            return false;
        }

        StoreUtils.LogDebug(TAG, "StoreController Initializing ...");

        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        SharedPreferences.Editor edit = prefs.edit();

        if (publicKey != null && publicKey.length() != 0) {
            edit.putString(StoreConfig.PUBLIC_KEY, publicKey);
        } else if (prefs.getString(StoreConfig.PUBLIC_KEY, "").length() == 0) {
        	String err = "publicKey is null or empty. Can't initialize store!!";
        	StoreUtils.LogError(TAG, err);
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(err));
            return false;
        }

        if (customSecret != null && customSecret.length() != 0) {
            edit.putString(StoreConfig.CUSTOM_SEC, customSecret);
        } else if (prefs.getString(StoreConfig.CUSTOM_SEC, "").length() == 0) {
        	String err = "customSecret is null or empty. Can't initialize store!!";
            StoreUtils.LogError(TAG, err);
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(err));
            return false;
        }
        edit.putInt("SA_VER_NEW", storeAssets.getVersion());
        edit.commit();

        if (storeAssets != null) {
            StoreInfo.setStoreAssets(storeAssets);
        }

        // Update SOOMLA store from DB
        StoreInfo.initializeFromDB();

        // Set up helper for the first time, querying and synchronizing inventory
        mIabService = new GooglePlayIabService();
        mIabService.initializeBillingService(new IabCallbacks.Listener() {

            @Override
            public void callback() {
                notifyIabServiceStarted();
                StoreUtils.LogDebug(TAG, "Setup successful, consuming unconsumed items and handling refunds");
                mIabService.queryInventoryAsync(false, null, mPurchaseSuccessListener, mPurchaseUnexpectedResultListener, null);
            }
        }, mIabFailureListener);

        mInitialized = true;
        BusProvider.getInstance().post(new StoreControllerInitializedEvent());
        return true;
    }


    public void startIabServiceInBg() {
        mIabService.startIabServiceInBg();
    }

    public void stopIabServiceInBg() {
        mIabService.stopIabServiceInBg();
    }

    /**
     * Initiate the restoreTransactions process
     */
    public void restoreTransactions() {
        mIabService.initializeBillingService(new IabCallbacks.Listener() {

            @Override
            public void callback() {
                notifyIabServiceStarted();
                StoreUtils.LogDebug(TAG, "Setup successful, consuming unconsumed items and handling refunds");
                mIabService.queryInventoryAsync(false, null, mPurchaseSuccessListener, mPurchaseUnexpectedResultListener, queryInventoryFinishedListener);
                BusProvider.getInstance().post(new RestoreTransactionsStartedEvent());
            }


        }, mIabFailureListener);
    }

    /**
     * Start a purchase process with Google Play.
     *
     * @param marketItem is the item to purchase. This item has to be defined EXACTLY the same in Google Play.
     * @param payload a payload to get back when this purchase is finished.
     * @throws IllegalStateException
     */
    public void buyWithMarket(MarketItem marketItem, String payload) throws IllegalStateException {
        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        String publicKey = prefs.getString(StoreConfig.PUBLIC_KEY, "");
        if (publicKey.length() == 0 || publicKey.equals("[YOUR PUBLIC KEY FROM GOOGLE PLAY]")) {
            StoreUtils.LogError(TAG, "You didn't provide a public key! You can't make purchases.");
            throw new IllegalStateException();
        }

        try {
            final Intent intent = new Intent(SoomlaApp.getAppContext(), IabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(PROD_ID, marketItem.getProductId());
            intent.putExtra(EXTRA_DATA, payload);

            mIabService.initializeBillingService(new IabCallbacks.Listener() {

                @Override
                public void callback() {
                    notifyIabServiceStarted();
                    SoomlaApp.getAppContext().startActivity(intent);
                }
            }, mIabFailureListener);

        } catch(Exception e){
            StoreUtils.LogError(TAG, "Error purchasing item " + e.getMessage());
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(e.getMessage()));
        }
    }

    /*====================   Common callbacks for success \ failure \ finish   ====================*/

    private void notifyIabServiceStarted() {
        BusProvider.getInstance().post(new BillingSupportedEvent());
        BusProvider.getInstance().post(new IabServiceStartedEvent());
    }

    private IabCallbacks.Listener queryInventoryFinishedListener = new IabCallbacks.Listener() {
        @Override
        public void callback() {
            BusProvider.getInstance().post(new RestoreTransactionsEvent(true));
            mIabService.stopBillingService(onStopBillingServiceSuccessListener);
        }
    };

    private IabCallbacks.Listener onStopBillingServiceSuccessListener = new IabCallbacks.Listener() {
        @Override
        public void callback() {
            BusProvider.getInstance().post(new IabServiceStoppedEvent());
        }
    };

    private IabCallbacks.Listener mIabFailureListener = new IabCallbacks.Listener() {
        @Override
        public void callback() {
            String msg = "There's no connectivity with the billing service.";
            StoreUtils.LogDebug(TAG, msg);
            BusProvider.getInstance().post(new BillingNotSupportedEvent());
//                BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(msg));
            mIabService.stopBillingService(onStopBillingServiceSuccessListener);
        }
    };

    /**
     *  Used for internal starting of purchase with Google Play. Do *NOT* call this on your own.
     */
    // TODO: implement checking if item is already owned in google play
    private boolean buyWithMarketInner(final Activity activity, final String sku, final String payload) {
        final PurchasableVirtualItem pvi;
        try {
            pvi = StoreInfo.getPurchasableItem(sku);
        } catch (VirtualItemNotFoundException e) {
            String msg = "Couldn't find a purchasable item associated with: " + sku;
            StoreUtils.LogError(TAG, msg);
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(msg));
            return false;
        }

        mIabService.initializeBillingService(new IabCallbacks.Listener() {
            @Override
            public void callback() {
                notifyIabServiceStarted();

                IabCallbacks.Listener purchaseFinishedListener = new IabCallbacks.Listener() {
                    @Override
                    public void callback() {
                        mIabService.stopBillingService(onStopBillingServiceSuccessListener);
                    }
                };
                mIabService.launchPurchaseFlow(activity, sku, Consts.RC_REQUEST, mPurchaseSuccessListener, purchaseActionResultCancelled, itemAlreadyOwnedListener, mPurchaseUnexpectedResultListener, purchaseFinishedListener,payload);
                BusProvider.getInstance().post(new PlayPurchaseStartedEvent(pvi));
            }
        }, mIabFailureListener);

        return true;
    }


    /*====================   Purchase Callbacks   ====================*/


    private IabCallbacks.OnPurchaseEventListener mPurchaseSuccessListener = new IabCallbacks.OnPurchaseEventListener() {

        /**
         * Check the state of the purchase and respond accordingly, giving the user an item,
         * throwing an error, or taking the item away and paying him back
         *
         * @param purchase
         */
        @Override
        public void callback(Purchase purchase) {
            String sku = purchase.getSku();
            String developerPayload = purchase.getDeveloperPayload();

            PurchasableVirtualItem pvi;
            try {
                pvi = StoreInfo.getPurchasableItem(sku);
            } catch (VirtualItemNotFoundException e) {
                StoreUtils.LogError(TAG, "(mPurchaseSuccessListener - purchase or query-inventory) ERROR : Couldn't find the " +
                        " VirtualCurrencyPack OR MarketItem  with productId: " + sku +
                        ". It's unexpected so an unexpected error is being emitted.");
                BusProvider.getInstance().post(new UnexpectedStoreErrorEvent("Couldn't find the sku of a product after purchase or query-inventory."));
                return;
            }

            switch (purchase.getPurchaseState()) {
                case 0:
                    StoreUtils.LogDebug(TAG, "Purchase successful.");
                    BusProvider.getInstance().post(new PlayPurchaseEvent(pvi, developerPayload));
                    pvi.give(1);
                    BusProvider.getInstance().post(new ItemPurchasedEvent(pvi));

                    itemAlreadyOwnedListener.callback(purchase);
                    break;
                case 1:
                case 2:
                    StoreUtils.LogDebug(TAG, "Purchase refunded.");
                    if (!StoreConfig.friendlyRefunds) {
                        pvi.take(1);
                    }
                    BusProvider.getInstance().post(new PlayRefundEvent(pvi, developerPayload));
                    break;
            }
        }
    };

    private IabCallbacks.OnPurchaseEventListener purchaseActionResultCancelled = new IabCallbacks.OnPurchaseEventListener() {

        /**
         * Post an event containing a PurchasableVirtualItem corresponding to the purchase,
         * or an unexpected error event if the item was not found.
         *
         * @param purchase
         */
        @Override
        public void callback(Purchase purchase) {
            String sku = purchase.getSku();
            try {
                PurchasableVirtualItem v = StoreInfo.getPurchasableItem(sku);
                BusProvider.getInstance().post(new PlayPurchaseCancelledEvent(v));
            } catch (VirtualItemNotFoundException e) {
                StoreUtils.LogError(TAG, "(purchaseActionResultCancelled) ERROR : Couldn't find the " +
                        "VirtualCurrencyPack OR MarketItem  with productId: " + sku +
                        ". It's unexpected so an unexpected error is being emitted.");
                BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
            }
        }
    };

    private IabCallbacks.OnPurchaseEventListener itemAlreadyOwnedListener = new IabCallbacks.OnPurchaseEventListener() {
        @Override
        public void callback(Purchase purchase) {
            String sku = purchase.getSku();
            try {
                PurchasableVirtualItem pvi = StoreInfo.getPurchasableItem(sku);

                if (!(pvi instanceof NonConsumableItem)) {
                    mIabService.consume(purchase);
                }
            } catch (VirtualItemNotFoundException e) {
                StoreUtils.LogError(TAG, "(purchaseActionResultCancelled) ERROR : Couldn't find the " +
                        "VirtualCurrencyPack OR MarketItem  with productId: " + sku +
                        ". It's unexpected so an unexpected error is being emitted.");
                BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
            } catch (IabException e) {
                StoreUtils.LogDebug(TAG, "Error while consuming: " + sku);
                BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(e.getMessage()));
            }
        }
    };


    private IabCallbacks.OnPurchaseUnexpectedResultListener mPurchaseUnexpectedResultListener = new IabCallbacks.OnPurchaseUnexpectedResultListener() {

        /**
         * Post an unexpected error event saying the purchase failed.
         * @param result
         */
        @Override
        public void callback(IabResult result) {
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(result.getMessage()));
            StoreUtils.LogError(TAG, "ERROR: Purchase failed: " + result.getMessage());
        }
    };


    /* Singleton */
    private static StoreController sInstance = null;

    public static StoreController getInstance() {
        if (sInstance == null) {
            sInstance = new StoreController();
        }
        return sInstance;
    }

    private StoreController() {
    }


    /* Private Members */
    private static final String TAG = "SOOMLA StoreController";
    private static final String PROD_ID    = "PRD#ID";
    private static final String EXTRA_DATA = "EXTR#DT";

    private boolean mInitialized = false;

    private GooglePlayIabService mIabService;


    /**
     * Android In-App Billing v3 requires an activity to receive the result of the billing process.
     * This activity's job is to do just that, it also contains the white/green IAB window.  Please
     * Do not start it on your own.
     */
    public static class IabActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent intent = getIntent();
            String productId = intent.getStringExtra(PROD_ID);
            String payload = intent.getStringExtra(EXTRA_DATA);

            try {
                if (!StoreController.getInstance().buyWithMarketInner(this, productId, payload)) {
                    finish();
                }
            }catch (Exception e) {
                StoreUtils.LogError(TAG, "Error purchasing item " + e.getMessage());
                BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(e.getMessage()));
                finish();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (!StoreController.getInstance().mIabService.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);

                if (!StoreController.getInstance().mIabService.isIabServiceInitialized())
                {
                    StoreUtils.LogError(TAG, "helper is null in onActivityResult.");
                    BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
                }
            }

            finish();
        }
    }
}

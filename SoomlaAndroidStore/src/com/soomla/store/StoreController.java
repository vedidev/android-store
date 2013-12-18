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

import com.soomla.billing.Consts;
import com.soomla.billing.IabHelper;
import com.soomla.billing.IabResult;
import com.soomla.billing.Inventory;
import com.soomla.billing.Purchase;
import com.soomla.store.data.ObscuredSharedPreferences;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.GoogleMarketItem;
import com.soomla.store.domain.NonConsumableItem;
import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.domain.VirtualItem;
import com.soomla.store.events.BillingNotSupportedEvent;
import com.soomla.store.events.BillingSupportedEvent;
import com.soomla.store.events.ClosingStoreEvent;
import com.soomla.store.events.ItemPurchasedEvent;
import com.soomla.store.events.OpeningStoreEvent;
import com.soomla.store.events.PlayPurchaseCancelledEvent;
import com.soomla.store.events.PlayPurchaseEvent;
import com.soomla.store.events.PlayPurchaseStartedEvent;
import com.soomla.store.events.PlayRefundEvent;
import com.soomla.store.events.RestoreTransactionsEvent;
import com.soomla.store.events.RestoreTransactionsStartedEvent;
import com.soomla.store.events.StoreControllerInitializedEvent;
import com.soomla.store.events.UnexpectedStoreErrorEvent;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
            StoreUtils.LogError(TAG, "StoreController is already initialized. You can't initialize it twice!");
            return false;
        }

        StoreUtils.LogDebug(TAG, "StoreController Initializing ...");

        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        SharedPreferences.Editor edit = prefs.edit();

        if (publicKey != null && publicKey.length() != 0) {
            edit.putString(StoreConfig.PUBLIC_KEY, publicKey);
        } else if (prefs.getString(StoreConfig.PUBLIC_KEY, "").length() == 0) {
            StoreUtils.LogError(TAG, "publicKey is null or empty. Can't initialize store!!");
            return false;
        }

        if (customSecret != null && customSecret.length() != 0) {
            edit.putString(StoreConfig.CUSTOM_SEC, customSecret);
        } else if (prefs.getString(StoreConfig.CUSTOM_SEC, "").length() == 0) {
            StoreUtils.LogError(TAG, "customSecret is null or empty. Can't initialize store!!");
            return false;
        }
        edit.putInt("SA_VER_NEW", storeAssets.getVersion());
        edit.commit();

        if (storeAssets != null) {
            StoreInfo.setStoreAssets(storeAssets);
        }

        // Update SOOMLA store from DB
        StoreInfo.initializeFromDB();

        // Set up helper for the first time, querying inventory
        startIabHelper(true);

        mInitialized = true;
        BusProvider.getInstance().post(new StoreControllerInitializedEvent());
        return true;
    }

    public void storeOpening() {
        mLock.lock();
        if (mStoreOpen) {
            StoreUtils.LogError(TAG, "Store is already open!");
            mLock.unlock();
            return;
        }

        if (mHelper == null) startIabHelper(false);

        mStoreOpen = true;
        mLock.unlock();

        BusProvider.getInstance().post(new OpeningStoreEvent());
    }

    /**
     * Call this function when you close the actual store window.
     */
    public void storeClosing() {
        mLock.lock();
        if (!mStoreOpen) return;

        mStoreOpen = false;
        if (!mHelper.isAsyncInProgress()) stopIabHelper();

        mLock.unlock();
        BusProvider.getInstance().post(new ClosingStoreEvent());
    }

    /**
     * Create a new IAB helper and set it up.
     *
     * @param queryInventory if we should query the inventory after setup.
     */
    private void startIabHelper(final boolean queryInventory) {
        // Setup IabHelper
        StoreUtils.LogDebug(TAG, "Creating IAB helper.");
        mHelper = new IabHelper();

        // Start the setup and call the listener when the setup is over
        StoreUtils.LogDebug(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                StoreUtils.LogDebug(TAG, "Setup finished.");
                if (result.isFailure()) {
                    StoreUtils.LogDebug(TAG, "There's no connectivity with the billing service.");
                    BusProvider.getInstance().post(new BillingNotSupportedEvent());
                    return;
                }

                BusProvider.getInstance().post(new BillingSupportedEvent());

                if (queryInventory) {
                    StoreUtils.LogDebug(TAG, "Setup successful, consuming unconsumed items and handling refunds");
                    mHelper.queryInventoryAsync(mPostInitQueryListener);
                }
            }
        });
    }

    /**
     * Dispose of the helper to prevent memory leaks
     */
    private void stopIabHelper() {
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    /**
     * Start a purchase process with Google Play.
     *
     * @param googleMarketItem is the item to purchase. This item has to be defined EXACTLY the same in Google Play.
     * @param payload a payload to get back when this purchase is finished.
     */
    public void buyWithGooglePlay(GoogleMarketItem googleMarketItem, String payload) throws IllegalStateException {
        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        String publicKey = prefs.getString(StoreConfig.PUBLIC_KEY, "");
        if (publicKey.length() == 0 || publicKey.equals("[YOUR PUBLIC KEY FROM GOOGLE PLAY]")) {
            StoreUtils.LogError(TAG, "You didn't provide a public key! You can't make purchases.");
            throw new IllegalStateException();
        }

        Intent intent = new Intent(SoomlaApp.getAppContext(), IabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PROD_ID, googleMarketItem.getProductId());
        intent.putExtra(EXTRA_DATA, payload);
        SoomlaApp.getAppContext().startActivity(intent);
    }

    /**
     *  Used for internal starting of purchase with Google Play. Do *NOT* call this on your own.
     */
    // TODO: implement checking if item is already owned
    private void buyWithGooglePlayInner(Activity activity, String sku, String payload) throws IllegalStateException, VirtualItemNotFoundException {
        if (mHelper == null) startIabHelper(false);
        mHelper.launchPurchaseFlow(activity, sku, Consts.RC_REQUEST, mPurchaseFinishedListener, payload);
        BusProvider.getInstance().post(new PlayPurchaseStartedEvent(StoreInfo.getPurchasableItem(sku)));
    }

    /**
     * Initiate the restoreTransactions process
     * @{link #storeOpen} must be called before this method or your helper will be destroyed.
     */
    public void restoreTransactions() {
        StoreUtils.LogDebug(TAG, "Sending restore transaction request");
        if (!transactionsAlreadyRestored()) {
            try {
                BusProvider.getInstance().post(new RestoreTransactionsStartedEvent());
                if (mHelper == null) startIabHelper(false);
                mHelper.queryInventoryAsync(mRestoreTransactionsListener);
            } catch (IllegalStateException e) {
                StoreUtils.LogError(TAG, "Error restoring transactions " + e.getMessage());
            }
        } else {
            StoreUtils.LogDebug(TAG, "Transactions already restored");
        }
    }

    /**
     * Check if transactions were restored already.
     * @return if transactions were restored already.
     */
    public boolean transactionsAlreadyRestored() {
        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        return prefs.getBoolean("RESTORED", false);
    }

    /* actions to take depending on billing result */

    /**
     * Check the state of the purchase and respond accordingly, giving the user an item,
     * throwing an error, or taking the item away and paying him back
     *
     * @param purchase the purchase data as received by the helper
     */
    private void purchaseActionResultOk(Purchase purchase) {
        String sku = purchase.getSku();
        String packageName = purchase.getPackageName();
        String developerPayload = purchase.getDeveloperPayload();
        try {
            PurchasableVirtualItem v = StoreInfo.getPurchasableItem(sku);
            switch (purchase.getPurchaseState()) {
                case 0:
                    StoreUtils.LogDebug(TAG, "Purchase successful.");
                    if ( !(v instanceof NonConsumableItem)) {
                        mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                    }
                    BusProvider.getInstance().post(new PlayPurchaseEvent(v, developerPayload));
                    v.give(1);
                    BusProvider.getInstance().post(new ItemPurchasedEvent(v));
                    break;
                case 1:
                    StoreUtils.LogDebug(TAG, "Purchase cancelled.");
                    BusProvider.getInstance().post(new PlayPurchaseCancelledEvent(v));
                    break;
                case 2:
                    StoreUtils.LogDebug(TAG, "Purchase refunded.");
                    if (!StoreConfig.friendlyRefunds) {
                        v.take(1);
                    }
                    BusProvider.getInstance().post(new PlayRefundEvent(v, developerPayload));
                    break;
            }
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "ERROR : Couldn't find the " + packageName +
                    " VirtualCurrencyPack OR GoogleMarketItem  with productId: " + sku +
                    ". It's unexpected so an unexpected error is being emitted.");
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
        }
    }

    /**
     * Post an event containing a PurchasableVirtualItem corresponding to the purchase,
     * or an unexpected error event if the item was not found.
     *
     * @param purchase the purchase data as received by the helper
     */
    private void purchaseActionResultCancelled(Purchase purchase) {
        String sku = purchase.getSku();
        String packageName = SoomlaApp.getAppContext().getPackageName();
        try {
            PurchasableVirtualItem v = StoreInfo.getPurchasableItem(sku);
            BusProvider.getInstance().post(new PlayPurchaseCancelledEvent(v));
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "ERROR : Couldn't find the " + packageName +
                    " VirtualCurrencyPack OR GoogleMarketItem  with productId: " + sku +
                    ". It's unexpected so an unexpected error is being emitted.");
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
        }
    }

    /**
     * Post an unexpected error event saying the purchase failed.
     */
    private void purchaseActionResultUnexpected() {
        BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
        StoreUtils.LogError(TAG, "ERROR: Purchase failed");
    }

    /* Callbacks for the IabHelper */

    /**
     * Wait to see if the purchase succeeded, then start the consumption process.
     */
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            StoreUtils.LogDebug(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (Consts.ResponseCode.valueOf(result.getResponse()) == Consts.ResponseCode.RESULT_OK) {
                purchaseActionResultOk(purchase);
            } else if (Consts.ResponseCode.valueOf(result.getResponse()) == Consts.ResponseCode.RESULT_USER_CANCELED) {
                purchaseActionResultCancelled(purchase);
            } else {
                purchaseActionResultUnexpected();
            }
        }
    };

    /**
     * After consumption succeeds, give the user 1 of the consumed item.
     */
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            StoreUtils.LogDebug(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            if (result.isSuccess()) {
                StoreUtils.LogDebug(TAG, "Consumption successful. Provisioning");
            }
            else {
                StoreUtils.LogDebug(TAG, "Error while consuming: " + result);
            }

            mLock.lock();
            if (!mStoreOpen) stopIabHelper();
            mLock.unlock();

            StoreUtils.LogDebug(TAG, "End consumption flow");
        }
    };

    /**
     * Query inventory and check for NonConsumableItems, if we have any, give them to the player.
     */
    IabHelper.QueryInventoryFinishedListener mRestoreTransactionsListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                StoreUtils.LogDebug(TAG, "Restore transactions error: " + result.getMessage());
                BusProvider.getInstance().post(new RestoreTransactionsEvent(false));
                return;
            }
            StoreUtils.LogDebug(TAG, "Restore transactions succeeded");

            SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
            SharedPreferences.Editor edit = prefs.edit();

            edit.putBoolean("RESTORED", true);
            edit.commit();

            List<String> itemSkus = inventory.getAllOwnedSkus(IabHelper.ITEM_TYPE_INAPP);
            for (String sku: itemSkus) {
                try {
                    VirtualItem v = StoreInfo.getVirtualItem(sku);
                    if ( !(v instanceof NonConsumableItem) ) {
                        continue;
                    }
                    Purchase purchase = inventory.getPurchase(sku);
                    if (purchase != null) {
                        String developerPayload = purchase.getDeveloperPayload();
                        StoreUtils.LogDebug(TAG, "Giving the player " + purchase.getPackageName());
                        BusProvider.getInstance().post(new PlayPurchaseEvent( (NonConsumableItem)v, developerPayload));
                        v.give(1);
                        BusProvider.getInstance().post(new ItemPurchasedEvent( (NonConsumableItem)v) );
                    }
                } catch (VirtualItemNotFoundException e) {
                    StoreUtils.LogError(TAG, "ERROR : Couldn't find the PURCHASED" +
                            " VirtualCurrencyPack OR GoogleMarketItem  with productId: " + sku +
                            ". It's unexpected so an unexpected error is being emitted");
                    BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
                }
            }

            mLock.lock();
            if (!mStoreOpen) stopIabHelper();
            mLock.unlock();

            StoreUtils.LogDebug(TAG, "Done restoring transactions");
            BusProvider.getInstance().post(new RestoreTransactionsEvent(true));
        }
    };

    /**
     * Handle refunds and unconsumed items
     */
    IabHelper.QueryInventoryFinishedListener mPostInitQueryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                StoreUtils.LogDebug(TAG, "Query inventory error: " + result.getMessage());
                return;
            }
            StoreUtils.LogDebug(TAG, "Query inventory succeeded");

            mPurchasesLeft = inventory.getAllPurchases();
            consumeAll();
        }

        // The following three functions call each other to avoid async clashing in IabHelper.
        private void consumeAll () {
            if (mPurchasesLeft.size() == 0) {
                // exit recursion when we have no purchases left, make sure to dispose of helper if need be
                mLock.lock();
                if (!mStoreOpen) stopIabHelper();
                mLock.unlock();

                StoreUtils.LogDebug(TAG, "Done handling refunds and unconsumed items");
                return;
            }
            Purchase purchase = mPurchasesLeft.remove(0);
            if (purchase != null && purchase.getPurchaseState() == 2) { // Purchase state is 2, item has been refunded
                mHelper.consumeAsync(purchase, consumeRefundCallback);
            } else if (purchase != null && purchase.getPurchaseState() == 0) { // Purchase state is 0, item has been refunded
                try {
                    PurchasableVirtualItem v = StoreInfo.getPurchasableItem(purchase.getSku());
                    if (!(v instanceof NonConsumableItem)) {
                        mHelper.consumeAsync(purchase, consumePurchasedCallback);
                    }
                } catch (VirtualItemNotFoundException e) {
                    StoreUtils.LogError(TAG, "ERROR : Couldn't find the PURCHASED" +
                            " VirtualCurrencyPack OR GoogleMarketItem  with productId: " + purchase.getSku() +
                            ". It's unexpected so an unexpected error is being emitted");
                }
            } else {
                consumeAll();
            }
        }

        private IabHelper.OnConsumeFinishedListener consumeRefundCallback = new IabHelper.OnConsumeFinishedListener() {
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                String sku = purchase.getSku();
                try {
                    PurchasableVirtualItem v = StoreInfo.getPurchasableItem(sku);
                    StoreUtils.LogDebug(TAG, "Purchase refunded.");
                    if (!StoreConfig.friendlyRefunds) {
                        v.take(1);
                    }
                    BusProvider.getInstance().post(new PlayRefundEvent(v, purchase.getDeveloperPayload()));
                } catch (VirtualItemNotFoundException e) {
                    StoreUtils.LogError(TAG, "ERROR : Couldn't find the PURCHASED" +
                            " VirtualCurrencyPack OR GoogleMarketItem  with productId: " + sku +
                            ". It's unexpected so an unexpected error is being emitted");
                }
                consumeAll();
            }
        };

        private IabHelper.OnConsumeFinishedListener consumePurchasedCallback = new IabHelper.OnConsumeFinishedListener() {
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                StoreUtils.LogDebug(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
                if (result.isSuccess()) {
                    StoreUtils.LogDebug(TAG, "Consumption successful");
                } else {
                    StoreUtils.LogDebug(TAG, "Error while consuming: " + result);
                }
                consumeAll();
            }
        };

        List<Purchase> mPurchasesLeft;
    };

    /**
     *  A wrapper to access IabHelper.handleActivityResult from outside
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return (mHelper != null) && mHelper.handleActivityResult(requestCode, resultCode, data);
    }

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
    public static final String PROD_ID    = "PRD#ID";
    public static final String EXTRA_DATA = "EXTR#DT";

    private static final String TAG = "SOOMLA StoreController";

    private boolean mInitialized   = false;
    private boolean mStoreOpen     = false;

    private IabHelper mHelper;

    private Lock mLock = new ReentrantLock();

    /**
     * Android In-App Billing v3 requires and activity to receive the result of the billing process.
     * This activity's job is to do just that, it also contains the white/green IAB window.  Please
     * Do not start it on your own.
     */
    public static class IabActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent();
            String productId = intent.getStringExtra(StoreController.PROD_ID);
            String payload = intent.getStringExtra(StoreController.EXTRA_DATA);

            try {
                StoreController.getInstance().buyWithGooglePlayInner(this, productId, payload);
            } catch (IllegalStateException e) {
                StoreUtils.LogError(TAG, "Error purchasing item " + e.getMessage());
                BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
                finish();
            } catch (VirtualItemNotFoundException e) {
                StoreUtils.LogError(TAG, "Couldn't find a purchasable item with productId: " + productId);
                BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
                finish();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode != RESULT_CANCELED){
                if (!StoreController.getInstance().handleActivityResult(requestCode, resultCode, data)) {
                    super.onActivityResult(requestCode, resultCode, data);
                    if (StoreController.getInstance().mHelper == null) finish(); // No helper, nothing to do here...
                } else {
                    finish();
                }
            }
        }
    }
}

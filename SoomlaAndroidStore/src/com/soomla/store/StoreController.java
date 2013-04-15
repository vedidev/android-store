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
import android.content.SharedPreferences;
import android.util.Log;
import com.soomla.billing.BillingService;
import com.soomla.billing.Consts;
import com.soomla.billing.PurchaseObserver;
import com.soomla.billing.ResponseHandler;
import com.soomla.store.data.ObscuredSharedPreferences;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.*;
import com.soomla.store.domain.virtualCurrencies.VirtualCurrency;
import com.soomla.store.domain.virtualCurrencies.VirtualCurrencyPack;
import com.soomla.store.domain.virtualGoods.VirtualGood;
import com.soomla.store.events.*;
import com.soomla.store.exceptions.InsufficientFundsException;
import com.soomla.store.exceptions.NotEnoughGoodsException;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is where all the important stuff happens. You can use it to purchase products from Google Play,
 * buy virtual goods, and get events on whatever happens.
 *
 * This is the only class you need to initialize in order to use the SOOMLA SDK.
 *
 * In addition to initializing this class, you'll also have to call
 * {@link StoreController#storeOpening(android.app.Activity)} and
 * {@link com.soomla.store.StoreController#storeClosing()} when you open the store window or close it. These two
 * calls initializes important components that support billing and storage information (see implementation below).
 * IMPORTANT: if you use the SOOMLA storefront (SOOMLA Storefront), than DON'T call these 2 functions.
 *
 */
public class StoreController extends PurchaseObserver {

    /**
     * If you're using SOOMLA's storefront, You have to initializePurchaseObserver the {@link StoreController} before you
     * open the StorefrontController (for more info about store front go to our Github page).
     * This initializer also initializes {@link StorageManager} and {@link StoreInfo}.
     * @param storeAssets is the definition of your application specific assets.
     */
    public void initialize(IStoreAssets storeAssets,
                          String publicKey,
                          String customSecret){

        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext(), SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        SharedPreferences.Editor edit = prefs.edit();
        if (publicKey != null && !publicKey.isEmpty()) {
            edit.putString(StoreConfig.PUBLIC_KEY, publicKey);
        } else if (prefs.getString(StoreConfig.PUBLIC_KEY, "").isEmpty()) {
            Log.e(TAG, "publicKey is null or empty. can't initialize store !!");
            return;
        }
        if (customSecret != null && !customSecret.isEmpty()) {
            edit.putString(StoreConfig.CUSTOM_SEC, customSecret);
        } else if (prefs.getString(StoreConfig.CUSTOM_SEC, "").isEmpty()) {
            Log.e(TAG, "customSecret is null or empty. can't initialize store !!");
            return;
        }
        edit.putInt("SA_VER_NEW", storeAssets.getVersion());
        edit.commit();

        if (storeAssets != null) {
            StoreInfo.setStoreAssets(storeAssets);
        }

        if (startBillingService()) {
            tryRestoreTransactions();
        }
    }

    /**
     * Start a currency pack purchase process (with Google Play)
     */
    public boolean buyWithGooglePlay(GoogleMarketItem googleMarketItem, String payload) {
        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext(), SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        String publicKey = prefs.getString(StoreConfig.PUBLIC_KEY, "");
        if (publicKey.isEmpty() || publicKey.equals("[YOUR PUBLIC KEY FROM GOOGLE PLAY]")) {
            Log.e(TAG, "You didn't provide a public key! You can't make purchases.");
            return false;
        }

        if (!mBillingService.requestPurchase(googleMarketItem.getProductId(), Consts.ITEM_TYPE_INAPP, payload)){
            return false;
        }
        try {
            BusProvider.getInstance().post(new PlayPurchaseStartedEvent(StoreInfo.getPurchasableItem(googleMarketItem.getProductId())));
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "Couldn't find a purchasable item with productId: " + googleMarketItem.getProductId());
        }
        return true;
    }

    /**
     * Call this function when you open the actual store window
     * @param activity is the activity being opened (or the activity that contains the store)/
     */
    public void storeOpening(Activity activity){
        mLock.lock();
        if (mStoreOpen) {
            Log.e(TAG, "You already sent storeOpening !");
            mLock.unlock();
            return;
        }

        mStoreOpen = true;
        mLock.unlock();

        initCompatibilityLayer(activity);

        /* Initialize StoreInfo from database in case any changes were done to it while the store was closed */
        StoreInfo.initializeFromDB();

        /* Billing */
        startBillingService();

        BusProvider.getInstance().post(new OpeningStoreEvent());
    }

    /**
     * Call this function when you close the actual store window.
     */
    public void storeClosing(){
        mStoreOpen = false;

        BusProvider.getInstance().post(new ClosingStoreEvent());

        stopBillingService();
//        ResponseHandler.unregister(this);
    }


    /** PurchaseObserver overridden functions**/

    /**
     * docs in {@link PurchaseObserver#onBillingSupported(boolean supported, String type)}.
     */
    @Override
    public void onBillingSupported(boolean supported, String type) {
        if (type == null || type.equals(Consts.ITEM_TYPE_INAPP)) {
            if (supported) {
                if (StoreConfig.debug){
                    Log.d(TAG, "billing is supported !");
                }
                BusProvider.getInstance().post(new BillingSupportedEvent());
            } else {
                // purchase is not supported. just send a message to JS to disable the "get more ..." button.

                if (StoreConfig.debug){
                    Log.d(TAG, "billing is not supported !");
                }
                BusProvider.getInstance().post(new BillingNotSupportedEvent());
            }
        } else if (type.equals(Consts.ITEM_TYPE_SUBSCRIPTION)) {
            // subscription is not supported
            // Soomla doesn't support subscriptions yet. doing nothing here ...
        } else {
            // subscription is not supported
            // Soomla doesn't support subscriptions yet. doing nothing here ...
        }
    }

    /**
     * docs in {@link PurchaseObserver#onPurchaseStateChange(com.soomla.billing.Consts.PurchaseState, String, long, String)}.
     */
    @Override
    public void onPurchaseStateChange(Consts.PurchaseState purchaseState, String productId, long purchaseTime, String developerPayload) {
        try {
            PurchasableVirtualItem purchasableVirtualItem = StoreInfo.getPurchasableItem(productId);

            BusProvider.getInstance().post(new PlayPurchaseEvent(purchasableVirtualItem, developerPayload));

            int amount = 1;
            try {
                amount = Integer.parseInt(developerPayload);
            } catch (Exception ignored) {}
            if (purchaseState == Consts.PurchaseState.PURCHASED) {
                purchasableVirtualItem.give(amount);
            }

            if (purchaseState == Consts.PurchaseState.REFUNDED){
                if (!StoreConfig.friendlyRefunds) {
                    purchasableVirtualItem.take(amount);
                }
            }

            BusProvider.getInstance().post(new ItemPurchasedEvent(purchasableVirtualItem));
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "ERROR : Couldn't find the " + purchaseState.name() +
                    " VirtualCurrencyPack OR GoogleMarketItem  with productId: " + productId +
                    ". It's unexpected so an unexpected error is being emitted.");
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
        }
    }

    /**
     * docs in {@link PurchaseObserver#onRequestPurchaseResponse(com.soomla.billing.BillingService.RequestPurchase, com.soomla.billing.Consts.ResponseCode)}.
     */
    @Override
    public void onRequestPurchaseResponse(BillingService.RequestPurchase request, Consts.ResponseCode responseCode) {
        if (responseCode == Consts.ResponseCode.RESULT_OK) {
            // purchase was sent to server
        } else if (responseCode == Consts.ResponseCode.RESULT_USER_CANCELED) {

            try {
                BusProvider.getInstance().post(new PlayPurchaseCancelledEvent(StoreInfo.getPurchasableItem(request.mProductId)));
            } catch (VirtualItemNotFoundException e) {
                Log.e(TAG, "ERROR : Couldn't find the CANCELLED VirtualCurrencyPack OR GoogleMarketItem  with productId: " + request.mProductId +
                        ". It's unexpected so an unexpected error is being emitted.");
                BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
            }

        } else {
            // purchase failed !

            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
            Log.e(TAG, "ERROR : Purchase failed for productId: " + request.mProductId);
        }
    }

    /**
     * docs in {@link PurchaseObserver#onRestoreTransactionsResponse(com.soomla.billing.BillingService.RestoreTransactions, com.soomla.billing.Consts.ResponseCode)}.
     */
    @Override
    public void onRestoreTransactionsResponse(BillingService.RestoreTransactions request, Consts.ResponseCode responseCode) {

        if (responseCode == Consts.ResponseCode.RESULT_OK) {
            if (StoreConfig.debug){
                Log.d(TAG, "RestoreTransactions succeeded");
            }

            // Update the shared preferences so that we don't perform
            // a RestoreTransactions again.
            SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext(), SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(StoreConfig.DB_INITIALIZED, true);
            edit.commit();
        } else {
            if (StoreConfig.debug) {
                Log.d(TAG, "RestoreTransactions error: " + responseCode);
            }
        }

        // we're stopping the billing service only if the store was not opened while the request was sent
        if (!mStoreOpen) {
            stopBillingService();
        }
    }


    /** Private methods **/

    private void tryRestoreTransactions() {
        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext(), SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        boolean initialized = prefs.getBoolean(StoreConfig.DB_INITIALIZED, false);
        if (!initialized) {
            if (StoreConfig.debug){
                Log.d(TAG, "sending restore transaction request");
            }
            mBillingService.restoreTransactions();
        }
    }

    private boolean startBillingService() {
        mLock.lock();
        if (mBillingService == null) {
            ResponseHandler.register(this);
            mBillingService = new BillingService();
            mBillingService.setContext(SoomlaApp.getAppContext());

            if (!mBillingService.checkBillingSupported(Consts.ITEM_TYPE_INAPP)){
                if (StoreConfig.debug){
                    Log.d(TAG, "There's no connectivity with the billing service.");
                }

                mLock.unlock();
                return false;
            }
        }

        mLock.unlock();
        return true;
    }

    private void stopBillingService() {
        mLock.lock();
        if (mBillingService != null) {
            mBillingService.unbind();
            mBillingService = null;
        }
        mLock.unlock();
    }

    public BillingService getBillingService() {
        return mBillingService;
    }

    /** Singleton **/

    private static StoreController sInstance = null;

    public static StoreController getInstance(){
        if (sInstance == null){
            sInstance = new StoreController();
        }

        return sInstance;
    }

    private StoreController() {
    }


    /** Private Members**/

    private static final String TAG             = "SOOMLA StoreController";

    private boolean mStoreOpen            = false;

    private BillingService mBillingService;
    private Lock    mLock = new ReentrantLock();
}

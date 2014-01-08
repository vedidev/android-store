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
package com.soomla.store.billing;

import android.app.Activity;
import android.content.Intent;

import com.soomla.store.StoreUtils;

import java.util.List;

public class GooglePlayIabService implements IIabService {


    public void initializeBillingService(final IabCallbacks.Listener successListener, final IabCallbacks.Listener failureListener) {

        // Set up helper for the first time, querying and synchronizing inventory
        startIabHelper(new OnIabSetupFinishedListener(successListener, failureListener));
    }

    public void stopBillingService(IabCallbacks.Listener successListener) {
        stopIabHelper(successListener);
    }

    public void startIabServiceInBg() {
        keepIabServiceOpen = true;
        startIabHelper(null);
    }

    public void stopIabServiceInBg() {
        keepIabServiceOpen = false;
        stopIabHelper(null);
    }

    /**
     *  A wrapper to access IabHelper.handleActivityResult from outside
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return isIabServiceInitialized() && mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void queryInventoryAsync(boolean querySkuDetails, List<String> moreSkus, IabHelper.QueryInventoryFinishedListener listener) {
        mHelper.queryInventoryAsync(querySkuDetails, moreSkus, listener);
    }

    public void queryInventoryAsync(boolean querySkuDetails,
                                    List<String> moreSkus,
                                    IabCallbacks.OnPurchaseEventListener purchaseSuccessListener,
                                    IabCallbacks.OnPurchaseUnexpectedResultListener purchaseUnexpectedResultListener,
                                    IabCallbacks.Listener queryInventoryFinishedListener) {

        mHelper.queryInventoryAsync(querySkuDetails, moreSkus, new QueryInventoryFinishedListener(purchaseSuccessListener, purchaseUnexpectedResultListener, queryInventoryFinishedListener));
    }


    @Override
    public boolean isIabServiceInitialized() {
        return mHelper != null;
    }

    @Override
    public void consume(Purchase purchase) throws IabException {
        mHelper.consume(purchase);
    }

    @Override
    public void launchPurchaseFlow(Activity act,
                                   String sku,
                                   int requestCode,
                                   final IabCallbacks.OnPurchaseEventListener purchaseSuccessListener,
                                   final IabCallbacks.OnPurchaseEventListener purchaseCancelledListener,
                                   final IabCallbacks.OnPurchaseEventListener itemAlreadyOwnedListener,
                                   final IabCallbacks.OnPurchaseUnexpectedResultListener purchaseUnexpectedResultListener,
                                   final IabCallbacks.Listener purchaseFinishedListener,
                                   String extraData) {
        mHelper.launchPurchaseFlow(act, sku, requestCode, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

                /**
                 * Wait to see if the purchase succeeded, then start the consumption process.
                 */
                StoreUtils.LogDebug(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
                if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_OK) {

                    purchaseSuccessListener.callback(purchase);
                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED) {
                    purchaseCancelledListener.callback(purchase);
                } else {
                    if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                        StoreUtils.LogDebug(TAG, "Tried to buy an item that was not consumed. Trying to consume it if it's non consumable.");
                        itemAlreadyOwnedListener.callback(purchase);
                    }

                    purchaseUnexpectedResultListener.callback(result);
                }

                if (purchaseFinishedListener != null) purchaseFinishedListener.callback();
            }
        }, extraData);
    }


    /*====================   Private Utility Methods   ====================*/

    /**
     * Create a new IAB helper and set it up.
     *
     * @param onIabSetupFinishedListener is a callback that lets users to add their own implementation for when the Iab is started
     */
    private synchronized void startIabHelper(OnIabSetupFinishedListener onIabSetupFinishedListener) {
        if (isIabServiceInitialized())
        {
            StoreUtils.LogDebug(TAG, "The helper is started. Just running the post start function.");

            // Apply success callback if given
            if (onIabSetupFinishedListener != null && onIabSetupFinishedListener.successListener != null) {
                onIabSetupFinishedListener.successListener.callback();
            }
            return;
        }

        StoreUtils.LogDebug(TAG, "Creating IAB helper.");
        mHelper = new IabHelper();

        StoreUtils.LogDebug(TAG, "IAB helper Starting setup.");
        mHelper.startSetup(onIabSetupFinishedListener);
    }

    /**
     * Dispose of the helper to prevent memory leaks
     */
    private synchronized void stopIabHelper(IabCallbacks.Listener successListener) {
        if (keepIabServiceOpen) {
            StoreUtils.LogDebug(TAG, "Not stopping Iab Helper b/c the user run 'startIabServiceInBg'. Keeping it open.");
            return;
        }

        if (mHelper == null) {
            StoreUtils.LogError(TAG, "Tried to stop IAB Helper when it was null.");
            return;
        }

        if (!mHelper.isAsyncInProgress())
        {
            StoreUtils.LogDebug(TAG, "Stopping Iab helper");
            mHelper.dispose();
            mHelper = null;
            if (successListener != null) {
                successListener.callback();
            }
        }
        else
        {
            String msg = "Cannot close store during async process. Will be stopped when async operation is finished.";
            StoreUtils.LogDebug(TAG, msg);
//        	mBusProvider.post(new UnexpectedStoreErrorEvent(msg));
        }
    }


    /**
     * Handle incomplete purchase and refund after initialization
     */
    private class QueryInventoryFinishedListener implements IabHelper.QueryInventoryFinishedListener {


        private IabCallbacks.OnPurchaseEventListener purchaseSuccessListener;
        private IabCallbacks.OnPurchaseUnexpectedResultListener purchaseUnexpectedResultListener;
        private IabCallbacks.Listener queryInventoryFinishedListener;

        public QueryInventoryFinishedListener(IabCallbacks.OnPurchaseEventListener purchaseSuccessListener,
            IabCallbacks.OnPurchaseUnexpectedResultListener purchaseUnexpectedResultListener,
            IabCallbacks.Listener queryInventoryFinishedListener) {

            this.purchaseSuccessListener            = purchaseSuccessListener;
            this.purchaseUnexpectedResultListener   = purchaseUnexpectedResultListener;
            this.queryInventoryFinishedListener     = queryInventoryFinishedListener;
        }

        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            StoreUtils.LogDebug(TAG, "Query inventory succeeded");
            if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_OK) {
                List<String> itemSkus = inventory.getAllOwnedSkus(IabHelper.ITEM_TYPE_INAPP);
                for (String sku: itemSkus) {
                    Purchase purchase = inventory.getPurchase(sku);
                    if (this.purchaseSuccessListener != null) this.purchaseSuccessListener.callback(purchase);
                }
            } else {
                StoreUtils.LogError(TAG, "Query inventory error: " + result.getMessage());
                if (this.purchaseUnexpectedResultListener != null) this.purchaseUnexpectedResultListener.callback(result);
            }

            if (this.queryInventoryFinishedListener != null) this.queryInventoryFinishedListener.callback();
        }
    }


    private class OnIabSetupFinishedListener implements IabHelper.OnIabSetupFinishedListener {

        private IabCallbacks.Listener successListener;
        private IabCallbacks.Listener failureListener;

        public OnIabSetupFinishedListener(IabCallbacks.Listener successListener, IabCallbacks.Listener failureListener) {
            this.successListener = successListener;
            this.failureListener = failureListener;
        }

        @Override
        public void onIabSetupFinished(IabResult result) {

            StoreUtils.LogDebug(TAG, "IAB helper Setup finished.");
            if (result.isFailure()) {
                if (failureListener != null) failureListener.callback();
                return;
            }
            if (successListener != null) successListener.callback();
        }
    }

    /* Private Members */
    private static final String TAG = "SOOMLA GooglePlayIabService";
    private IabHelper mHelper;
    private boolean keepIabServiceOpen = false;
}

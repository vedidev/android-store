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

import java.util.List;

/**
 * This interface defines the functionality that needs to be implemented in order to create an
 * in-app billing service (e.g. Google Play, Amazon App Store, Samsung Apps...)
 */
public interface IIabService {

    /**
     * Checks if in-app billing service is initialized
     *
     * @return true if Iab is initialized, false otherwise
     */
    public boolean isIabServiceInitialized();

    /**
     * Consumes a given in-app product. Consuming can only be done on an item
     * that's owned, and as a result of consumption, the user will no longer own it.
     * This method may block or take long to return. Do not call from the UI thread.
     * For that, see {@link #consumeAsync}.
     *
     * @param purchase the PurchaseInfo that represents the item to consume.
     * @throws IabException if there is a problem during consumption.
     */
    public void consume(IabPurchase purchase) throws IabException;

    /**
     * Asynchronous wrapper to item consumption. Works like {@link #consume}, but
     * performs the consumption in the background and notifies completion through
     * the provided listener. This method is safe to call from a UI thread.
     *
     * @param purchase the purchase to be consumed
     * @param consumeListener the listener to notify when the consumption operation finishes.
     */
    public void consumeAsync(IabPurchase purchase,
                             final IabCallbacks.OnConsumeListener consumeListener);

    /**
     * Handles an activity result that's part of the purchase flow in in-app billing. If you
     * are calling {@link #launchPurchaseFlow}, then you must call this method from your
     * Activity's {@link android.app.Activity@onActivityResult} method. This method
     * MUST be called from the UI thread of the Activity.
     *
     * @param requestCode The requestCode as you received it.
     * @param resultCode The resultCode as you received it.
     * @param data The data (Intent) as you received it.
     * @return true if the result was related to a purchase flow and was handled;
     *     false if the result was not related to a purchase, in which case you should
     *     handle it normally.
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * Initiates the UI flow for an in-app purchase.
     * Call this method to initiate an in-app purchase which will involve bringing up the Market
     * screen. The calling activity will be paused while the user interacts with the Market.
     * This method MUST be called from the UI thread of the Activity.
     *
     * @param act The calling activity.
     * @param sku The sku of the item to purchase.
     * @param purchaseListener The listener to notify when the purchase process finishes
     * @param extraData Extra data (developer payload), which will be returned with the purchase data
     *     when the purchase completes. This extra data will be permanently bound to that purchase
     *     and will always be returned when the purchase is queried.
     */
    public void launchPurchaseFlow(Activity act,
                                   String sku,
                                   final IabCallbacks.OnPurchaseListener purchaseListener,
                                   String extraData);

    /**
     * Asynchronous wrapper for inventory query. This queries the inventory - will query all owned
     * items from the server, as well as information on additional skus, if specified, and will do
     * so asynchronously and call back the specified listener upon completion.
     * This method is safe to call from a UI thread.
     *
     * @param querySkuDetails if true, SKU details (price, description, etc) will be queried as well
     *     as purchase information.
     * @param moreSkus additional PRODUCT skus to query information on, regardless of ownership.
     *     Ignored if null or if querySkuDetails is false.
     * @param queryInventoryListener The listener to notify when the refresh operation completes.
     */
    public void queryInventoryAsync(boolean querySkuDetails,
                                    List<String> moreSkus,
                                    IabCallbacks.OnQueryInventoryListener queryInventoryListener);

    /**
     * Initializes in-app billing service
     *
     * @param initListener
     */
    public void initializeBillingService(IabCallbacks.IabInitListener initListener);

    /**
     * Starts in-app billing service in background
     *
     * @param initListener
     */
    public void startIabServiceInBg(IabCallbacks.IabInitListener initListener);

    /**
     * Stops in-app billing service in background
     *
     * @param initListener
     */
    public void stopIabServiceInBg(IabCallbacks.IabInitListener initListener);

}

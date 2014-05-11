/**
 * Copyright (C) 2012-2014 Soomla Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.soomla.store.purchaseTypes;

import com.soomla.store.BusProvider;
import com.soomla.store.StoreController;
import com.soomla.store.StoreUtils;
import com.soomla.store.domain.MarketItem;
import com.soomla.store.events.ItemPurchaseStartedEvent;
import com.soomla.store.exceptions.InsufficientFundsException;

/**
 * This type of IabPurchase allows users to purchase <code>PurchasableVirtualItems</code> with the
 * Market (with real money! $$$).
 *
 * Real Game Example: Purchase a Sword for $1.99.
 */
public class PurchaseWithMarket extends PurchaseType {

    /**
     * Constructor
     * Constructs a PurchaseWithMarket object by constructing a new <code>MarketItem</code> object
     * with the given <code>productId</code> and price, and declaring it as UNMANAGED.
     *
     * @param productId the productId as it appears in the Market.
     * @param price the price in the Market.
     */
    public PurchaseWithMarket(String productId, double price) {
        mMarketItem = new MarketItem(productId, MarketItem.Managed.UNMANAGED, price);
    }

    /**
     * Constructor
     *
     * @param marketItem the representation of the item in the market
     */
    public PurchaseWithMarket(MarketItem marketItem) {
        mMarketItem = marketItem;
    }

    /**
     * Buys the virtual item with real money (from the Market).
     *
     * @throws com.soomla.store.exceptions.InsufficientFundsException
     */
    @Override
    public void buy() throws InsufficientFundsException {
        StoreUtils.LogDebug(TAG, "Starting in-app purchase for productId: "
                + mMarketItem.getProductId());
        
        BusProvider.getInstance().post(new ItemPurchaseStartedEvent(getAssociatedItem()));
        try {
            StoreController.getInstance().buyWithMarket(mMarketItem, "");
        } catch (IllegalStateException e) {
            StoreUtils.LogError(TAG, "Error when purchasing item");
        }
    }


    /** Setters and Getters */

    public MarketItem getMarketItem() {
        return mMarketItem;
    }


    /** Private Members */

    private static final String TAG = "SOOMLA PurchaseWithMarket"; //used for Log messages

    private MarketItem mMarketItem; //the representation of the item in the market
}

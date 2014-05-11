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
import com.soomla.store.StoreUtils;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.data.VirtualItemStorage;
import com.soomla.store.domain.VirtualItem;
import com.soomla.store.events.ItemPurchaseStartedEvent;
import com.soomla.store.events.ItemPurchasedEvent;
import com.soomla.store.exceptions.InsufficientFundsException;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

/**
 * This type of purchase allows users to purchase <code>PurchasableVirtualItems</code> with other
 * <code>VirtualItems</code>.
 *
 * Real Game Example: Purchase a Sword in exchange for 100 Gems. Sword is the item to be purchased,
 * Gem is the target item, and 100 is the amount.
 */
public class PurchaseWithVirtualItem extends PurchaseType {

    /**
     * Constructor
     *
     * @param targetItemId the itemId of the <code>VirtualItem</code> that is used to "pay" in
     *                     order to make the purchase
     * @param amount the number of target items needed in order to make the purchase
     */
    public PurchaseWithVirtualItem(String targetItemId, int amount) {
        mTargetItemId = targetItemId;
        mAmount = amount;
    }

    /**
     * Buys the virtual item with other virtual items.
     */
    @Override
    public void buy() throws InsufficientFundsException{

        StoreUtils.LogDebug(TAG, "Trying to buy a " + getAssociatedItem().getName() + " with "
                + mAmount + " pieces of " + mTargetItemId);

        VirtualItem item = null;
        try {
            item = StoreInfo.getVirtualItem(mTargetItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "Target virtual item doesn't exist !");
            return;
        }

        BusProvider.getInstance().post(new ItemPurchaseStartedEvent(getAssociatedItem()));

        VirtualItemStorage storage = StorageManager.getVirtualItemStorage(item);

        assert storage != null;
        int balance = storage.getBalance(item);
        if (balance < mAmount){
            throw new InsufficientFundsException(mTargetItemId);
        }

        storage.remove(item, mAmount);

        getAssociatedItem().give(1);
        BusProvider.getInstance().post(new ItemPurchasedEvent(getAssociatedItem()));
    }


    /** Setters and Getters */

    public String getTargetItemId() {
        return mTargetItemId;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int mAmount) {
        this.mAmount = mAmount;
    }


    /** Private Members */

    //used for Log messages
    private static final String TAG = "SOOMLA PurchaseWithVirtualItem";

    //the itemId of the VirtualItem that is used to "pay" with in order to make the purchase
    private String mTargetItemId;

    private int mAmount; //the number of items to purchase.
}

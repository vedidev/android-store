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

package com.soomla.store.purchaseTypes;

import com.soomla.store.BusProvider;
import com.soomla.store.StoreUtils;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.VirtualItemStorage;
import com.soomla.store.domain.VirtualItem;

import com.soomla.store.events.ItemPurchaseStartedEvent;
import com.soomla.store.events.ItemPurchasedEvent;
import com.soomla.store.exceptions.InsufficientFundsException;

public class PurchaseWithVirtualItem extends PurchaseType {

    public PurchaseWithVirtualItem(VirtualItem item, int amount) {
        mItem = item;
        mAmount = amount;
    }

    @Override
    public void buy(int amount) throws InsufficientFundsException{

        StoreUtils.LogDebug(TAG, "Trying to buy " + mAmount + " pieces of " + mItem.getName());
        BusProvider.getInstance().post(new ItemPurchaseStartedEvent(getAssociatedItem()));

        VirtualItemStorage storage = StorageManager.getVirtualItemStorage(mItem);

        assert storage != null;
        int balance = storage.getBalance(mItem);
        if (balance < mAmount){
            throw new InsufficientFundsException(mItem.getItemId());
        }

        storage.remove(mItem, mAmount);

        getAssociatedItem().give(amount);
        BusProvider.getInstance().post(new ItemPurchasedEvent(getAssociatedItem()));
    }

    public VirtualItem getItem() {
        return mItem;
    }

    public int getAmount() {
        return mAmount;
    }

    //    @Override
//    public String getTypeName() {
//        return "virtualItemPurchase";
//    }

    private static final String TAG = "SOOMLA PurchaseWithVirtualItem";

    private VirtualItem mItem;
    private int         mAmount;
}

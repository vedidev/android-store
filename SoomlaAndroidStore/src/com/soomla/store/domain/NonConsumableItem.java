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
package com.soomla.store.domain;

import com.soomla.store.StoreUtils;
import com.soomla.store.data.StorageManager;
import com.soomla.store.purchaseTypes.PurchaseWithMarket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A representation of a non-consumable item. These kinds of items are bought by the user once and kept for him forever.
 */
public class NonConsumableItem extends PurchasableVirtualItem {

    /** Constructor
     *
     * @param mName is the name of the non consumable.
     * @param mDescription is the description of the non consumable.
     * @param mItemId is the id of the non consumable.
     */
    public NonConsumableItem(String mName, String mDescription, String mItemId, PurchaseWithMarket purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);
    }

    public NonConsumableItem(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    /**
     * Converts the current {@link NonConsumableItem} to a JSONObject.
     * @return a JSONObject representation of the current {@link NonConsumableItem}.
     */
    @Override
    public JSONObject toJSONObject(){
        return super.toJSONObject();
    }

    @Override
    public void give(int amount) {
        StorageManager.getNonConsumableItemsStorage().add(this);
    }

    @Override
    public void take(int amount) {
        StorageManager.getNonConsumableItemsStorage().remove(this);
    }

    @Override
    protected boolean canBuy() {
        if (StorageManager.getNonConsumableItemsStorage().nonConsumableItemExists(this)) {
            StoreUtils.LogDebug(TAG, "You can't buy a NonConsumableItem that was already given to the user.");
            return false;
        }
        return true;
    }

    /** Private members **/

    private static final String TAG = "SOOMLA NonConsumableItem";
}

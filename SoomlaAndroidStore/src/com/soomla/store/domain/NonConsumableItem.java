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

import android.util.Log;
import com.soomla.store.StoreConfig;
import com.soomla.store.domain.AbstractVirtualItem;
import com.soomla.store.domain.data.GoogleMarketItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * A representation of a non-consumable item. These kinds of items are bought by the user once and kept for him forever.
 */
public class NonConsumableItem extends AbstractVirtualItem {

    /** Constructor
     *
     * @param mName is the name of the non consumable.
     * @param mDescription is the description of the non consumable.
     * @param mItemId is the id of the non consumable.
     * @param productId is the product id on Google Market.
     * @param mPrice is the actual $$ cost of the non consumable.
     */
    public NonConsumableItem(String mName, String mDescription, String mItemId,
                             String productId, double mPrice) {
        super(mName, mDescription, mItemId);
        this.mGoogleItem = new GoogleMarketItem(productId, GoogleMarketItem.Managed.MANAGED, mPrice);
    }

    public NonConsumableItem(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        this.mGoogleItem = new GoogleMarketItem(jsonObject);
        this.mGoogleItem.setManaged(GoogleMarketItem.Managed.MANAGED);
    }

    /**
     * Converts the current {@link NonConsumableItem} to a JSONObject.
     * @return a JSONObject representation of the current {@link NonConsumableItem}.
     */
    public JSONObject toJSONObject(){
        JSONObject parentJsonObject = super.toJSONObject();
        JSONObject gmiJSONObject = mGoogleItem.toJSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            Iterator<?> keys = parentJsonObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, parentJsonObject.get(key));
            }

            keys = gmiJSONObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, gmiJSONObject.get(key));
            }
        } catch (JSONException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "An error occured while generating JSON object.");
            }
        }

        return jsonObject;
    }

    /** Getters **/

    public GoogleMarketItem getGoogleItem() {
        return mGoogleItem;
    }

    public String getProductId(){
        return mGoogleItem.getProductId();
    }

    public double getPrice(){
        return mGoogleItem.getPrice();
    }

    /** Private members **/

    private static final String TAG = "SOOMLA NonConsumableItem";

    private GoogleMarketItem mGoogleItem;
}

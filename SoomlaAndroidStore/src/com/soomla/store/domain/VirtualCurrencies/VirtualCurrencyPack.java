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
package com.soomla.store.domain.virtualCurrencies;

import android.util.Log;
import com.soomla.store.StoreConfig;
import com.soomla.store.StoreUtils;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.purchaseTypes.PurchaseType;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Every game has its VirtualCurrencies. Here you represent a pack of a specific VirtualCurrency.
 * For example: If you have a "Coin" as a virtual currency, you will
 * sell packs of "Coins". e.g. "10 Coins Set" or "Super Saver Pack".
 *
 * This VirtualItem is purchasable.
 * In case you purchase this item in Google Play (PurchaseWithMarket), You need to define the google item in Google
 * Play Developer Console. (https://play.google.com/apps/publish)
 */
public class VirtualCurrencyPack extends PurchasableVirtualItem {

    /**
     *
     * @param mName see parent
     * @param mDescription see parent
     * @param mItemId see parent
     * @param mCurrencyAmount is the amount of currency in the pack.
     * @param mCurrency is the currency associated with this pack.
     * @param purchaseType see parent
     */
    public VirtualCurrencyPack(String mName, String mDescription, String mItemId,
                               int mCurrencyAmount,
                               VirtualCurrency mCurrency, PurchaseType purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);
        this.mCurrency = mCurrency;
        this.mCurrencyAmount = mCurrencyAmount;
    }

    /** Constructor
     *
     * see parent
     */
    public VirtualCurrencyPack(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        this.mCurrencyAmount = jsonObject.getInt(JSONConsts.CURRENCYPACK_CURRENCYAMOUNT);

        String currencyItemId = jsonObject.getString(JSONConsts.CURRENCYPACK_CURRENCYITEMID);
        try{
            this.mCurrency = (VirtualCurrency) StoreInfo.getVirtualItem(currencyItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "Couldn't find the associated currency");
        }
    }

    /**
     * see parent
     */
    public JSONObject toJSONObject(){
        JSONObject parentJsonObject = super.toJSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONConsts.CURRENCYPACK_CURRENCYAMOUNT, mCurrencyAmount);
            jsonObject.put(JSONConsts.CURRENCYPACK_CURRENCYITEMID, mCurrency.getItemId());

            Iterator<?> keys = parentJsonObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, parentJsonObject.get(key));
            }

        } catch (JSONException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "An error occured while generating JSON object.");
            }
        }

        return jsonObject;
    }

    /**
     * see parent
     * @param amount the amount of the specific item to be given.
     */
    @Override
    public void give(int amount) {
        StorageManager.getVirtualCurrencyStorage().add(mCurrency, mCurrencyAmount*amount);
    }

    /**
     * see parent
     * @param amount the amount of the specific item to be taken.
     */
    @Override
    public void take(int amount) {
        StorageManager.getVirtualCurrencyStorage().remove(mCurrency, mCurrencyAmount*amount);
    }

    /**
     * see parent
     */
    @Override
    protected boolean canBuy() {
        return true;
    }

    /** Getters **/

    public int getCurrencyAmount() {
        return mCurrencyAmount;
    }

    public VirtualCurrency getCurrency() {
        return mCurrency;
    }

    /** Private members **/

    private static final String TAG = "SOOMLA VirtualCurrencyPack";

    private int              mCurrencyAmount;
    private VirtualCurrency  mCurrency;
}

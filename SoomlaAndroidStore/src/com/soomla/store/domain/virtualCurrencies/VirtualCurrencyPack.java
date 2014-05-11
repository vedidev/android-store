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

package com.soomla.store.domain.virtualCurrencies;

import com.soomla.store.StoreUtils;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Every game has its virtual currencies. Here you represent a pack of a specific
 * <code>VirtualCurrency</code>.
 *
 * Real Game Example: If the virtual currency in your game is a 'Coin', you will sell packs of
 * 'Coins' such as "10 Coins Set" or "Super Saver Pack".
 *
 * NOTE: In case you want this item to be available for purchase with real money  you will need to
 * define the item in the market (Google Play, Amazon App Store, etc...).
 *
 * Inheritance: {@link com.soomla.store.domain.virtualCurrencies.VirtualCurrencyPack} >
 * {@link com.soomla.store.domain.PurchasableVirtualItem} >
 * {@link com.soomla.store.domain.VirtualItem}
 */
public class VirtualCurrencyPack extends PurchasableVirtualItem {

    /**
     * Constructor
     *
     * @param mName see parent
     * @param mDescription see parent
     * @param mItemId see parent
     * @param mCurrencyAmount the amount of currency in the pack
     * @param mCurrencyItemId the item id of the currency associated with this pack
     * @param purchaseType see parent
     */
    public VirtualCurrencyPack(String mName, String mDescription, String mItemId,
                               int mCurrencyAmount,
                               String mCurrencyItemId,
                               PurchaseType purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);
        this.mCurrencyItemId = mCurrencyItemId;
        this.mCurrencyAmount = mCurrencyAmount;
    }

    /**
     * Constructor
     *
     * @param jsonObject see parent
     * @throws JSONException
     */
    public VirtualCurrencyPack(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        this.mCurrencyAmount = jsonObject.getInt(JSONConsts.CURRENCYPACK_CURRENCYAMOUNT);

        this.mCurrencyItemId = jsonObject.getString(JSONConsts.CURRENCYPACK_CURRENCYITEMID);
    }

    /**
     * see parent
     *
     * @return see parent
     */
    @Override
    public JSONObject toJSONObject(){
        JSONObject parentJsonObject = super.toJSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONConsts.CURRENCYPACK_CURRENCYAMOUNT, mCurrencyAmount);
            jsonObject.put(JSONConsts.CURRENCYPACK_CURRENCYITEMID, mCurrencyItemId);

            Iterator<?> keys = parentJsonObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, parentJsonObject.get(key));
            }

        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }

    /**
     * see parent
     *
     * @param amount the amount of the specific item to be given
     * @param notify notify of change in user's balance of current virtual item
     * @return see parent
     */
    @Override
    public int give(int amount, boolean notify) {
        VirtualCurrency currency = null;
        try {
            currency = (VirtualCurrency)StoreInfo.getVirtualItem(mCurrencyItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "VirtualCurrency with itemId: " + mCurrencyItemId
                    + " doesn't exist! Can't give this pack.");
            return 0;
        }
        return StorageManager.getVirtualCurrencyStorage().add(
                currency, mCurrencyAmount * amount, notify);
    }

    /**
     * see parent
     *
     * @param amount the amount of the specific item to be taken
     * @param notify notify of change in user's balance of current virtual item
     * @return see parent
     */
    @Override
    public int take(int amount, boolean notify) {
        VirtualCurrency currency = null;
        try {
            currency = (VirtualCurrency)StoreInfo.getVirtualItem(mCurrencyItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "VirtualCurrency with itemId: " + mCurrencyItemId +
                    " doesn't exist! Can't take this pack.");
            return 0;
        }
        return StorageManager.getVirtualCurrencyStorage().remove(currency,
                mCurrencyAmount * amount, notify);
    }

    /**
     * see parent
     *
     * @param balance the balance of the current virtual item
     * @param notify notify of change in user's balance of current virtual item
     * @return see parent
     */
    @Override
    public int resetBalance(int balance, boolean notify) {
        // Not supported for VirtualCurrencyPacks !
        StoreUtils.LogError(TAG, "Someone tried to reset balance of CurrencyPack. "
                + "That's not right.");
        return 0;
    }

    /**
     * see parent
     *
     * @return see parent
     */
    @Override
    protected boolean canBuy() {
        return true;
    }


    /** Setters and Getters **/

    public int getCurrencyAmount() {
        return mCurrencyAmount;
    }

    public String getCurrencyItemId() {
        return mCurrencyItemId;
    }


    /** Private Members **/

    private static final String TAG = "SOOMLA VirtualCurrencyPack"; //used for Log messages

    private int mCurrencyAmount; //the amount of currency in the pack

    private String mCurrencyItemId; //the itemId of the currency associated with this pack
}

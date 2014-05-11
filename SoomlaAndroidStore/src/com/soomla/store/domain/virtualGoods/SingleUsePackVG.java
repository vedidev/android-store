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

package com.soomla.store.domain.virtualGoods;

import com.soomla.store.StoreUtils;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * SingleUsePacks are just bundles of <code>SingleUseVG</code>'s.
 * This kind of virtual good can be used to let your users buy more than one SingleUseVG at once.
 *
 * The SingleUsePackVG's characteristics are:
 *  1. Can be purchased an unlimited number of times.
 *  2. Doesn't have a balance in the database. The SingleUseVG that's associated with this pack
 *     has its own balance. When your users buy a SingleUsePackVG, the balance of the associated
 *     SingleUseVG goes up in the amount that this pack represents (mGoodAmount).
 *
 * Real Game Examples: 'Box Of Chocolates', '10 Swords'
 *
 * NOTE: In case you want this item to be available for purchase in the market (PurchaseWithMarket),
 * you will need to define the item in the market (Google Play, Amazon App Store, etc...).
 *
 * Inheritance: {@link com.soomla.store.domain.virtualGoods.SingleUsePackVG} >
 * {@link com.soomla.store.domain.virtualGoods.VirtualGood} >
 * {@link com.soomla.store.domain.PurchasableVirtualItem} >
 * {@link com.soomla.store.domain.VirtualItem}
 */
public class SingleUsePackVG extends VirtualGood {

    /** Constructor
     *
     * @param goodItemId the itemId of the SingleUseVG associated with this pack.
     * @param amount the number of SingleUseVGs in the pack.
     * @param name see parent
     * @param description see parent
     * @param itemId see parent
     * @param purchaseType see parent
     */
    public SingleUsePackVG(String goodItemId, int amount,
                           String name, String description,
                           String itemId, PurchaseType purchaseType) {
        super(name, description, itemId, purchaseType);

        mGoodItemId = goodItemId;
        mGoodAmount = amount;
    }

    /**
     * Constructor
     *
     * @param jsonObject see parent
     * @throws JSONException
     */
    public SingleUsePackVG(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        mGoodItemId = jsonObject.getString(JSONConsts.VGP_GOOD_ITEMID);
        mGoodAmount = jsonObject.getInt(JSONConsts.VGP_GOOD_AMOUNT);
    }

    /**
     * see parent
     *
     * @return see parent
     */
    @Override
    public JSONObject toJSONObject() {
        JSONObject parentJsonObject = super.toJSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            Iterator<?> keys = parentJsonObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, parentJsonObject.get(key));
            }

            jsonObject.put(JSONConsts.VGP_GOOD_ITEMID, mGoodItemId);
            jsonObject.put(JSONConsts.VGP_GOOD_AMOUNT, mGoodAmount);
        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }

    /**
     * see parent
     *
     * @param amount the amount of the specific item to be given
     * @return balance after the giving process
     */
    @Override
    public int give(int amount, boolean notify) {
        SingleUseVG good = null;
        try {
            good = (SingleUseVG)StoreInfo.getVirtualItem(mGoodItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "SingleUseVG with itemId: " + mGoodItemId + " doesn't exist! Can't give this pack.");
            return 0;
        }
        return StorageManager.getVirtualGoodsStorage().add(good, mGoodAmount*amount, notify);
    }

    /**
     * see parent
     * @param amount the amount of the specific item to be taken
     * @return balance after the taking process
     */
    @Override
    public int take(int amount, boolean notify) {
        SingleUseVG good = null;
        try {
            good = (SingleUseVG)StoreInfo.getVirtualItem(mGoodItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "SingleUseVG with itemId: " + mGoodItemId + " doesn't exist! Can't take this pack.");
            return 0;
        }
        return StorageManager.getVirtualGoodsStorage().remove(good, mGoodAmount*amount, notify);
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

    public String getGoodItemId() {
        return mGoodItemId;
    }

    public int getGoodAmount() {
        return mGoodAmount;
    }


    /** Private Members **/

    private static final String TAG = "SOOMLA SingleUsePackVG"; //used for Log messages

    private String mGoodItemId; //the itemId of the SingleUseVG associated with this Pack.

    private int mGoodAmount; //the number of SingleUseVGs in the pack.
}

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

package com.soomla.store.domain;

import com.soomla.store.StoreUtils;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.exceptions.InsufficientFundsException;
import com.soomla.store.purchaseTypes.PurchaseType;
import com.soomla.store.purchaseTypes.PurchaseWithMarket;
import com.soomla.store.purchaseTypes.PurchaseWithVirtualItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * A representation of a <code>VirtualItem</code> that you can actually purchase.
 *
 */
public abstract class PurchasableVirtualItem extends VirtualItem {

    /**
     * Constructor
     *
     * @param mName see parent
     * @param mDescription see parent
     * @param mItemId see parent
     * @param purchaseType the way this item is purchased
     */
    public PurchasableVirtualItem(String mName, String mDescription, String mItemId,
                                  PurchaseType purchaseType) {
        super(mName, mDescription, mItemId);

        mPurchaseType = purchaseType;

        mPurchaseType.setAssociatedItem(this);
    }

    /**
     * Constructor
     *
     * @param jsonObject see parent
     * @throws JSONException
     */
    public PurchasableVirtualItem(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        JSONObject purchasableObj = jsonObject.getJSONObject(JSONConsts.PURCHASABLE_ITEM);
        String purchaseType = purchasableObj.getString(JSONConsts.PURCHASE_TYPE);

        if (purchaseType.equals(JSONConsts.PURCHASE_TYPE_MARKET)) {
            JSONObject marketItemObj =
                    purchasableObj.getJSONObject(JSONConsts.PURCHASE_MARKET_ITEM);

            mPurchaseType = new PurchaseWithMarket(new MarketItem(marketItemObj));
        } else if (purchaseType.equals(JSONConsts.PURCHASE_TYPE_VI)) {
            String itemId = purchasableObj.getString(JSONConsts.PURCHASE_VI_ITEMID);
            int amount = purchasableObj.getInt(JSONConsts.PURCHASE_VI_AMOUNT);

            mPurchaseType = new PurchaseWithVirtualItem(itemId, amount);
        } else {
            StoreUtils.LogError(TAG, "IabPurchase type not recognized !");
        }

        if (mPurchaseType != null) {
            mPurchaseType.setAssociatedItem(this);
        }
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
            Iterator<?> keys = parentJsonObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, parentJsonObject.get(key));
            }

            JSONObject purchasableObj = new JSONObject();

            if(mPurchaseType instanceof PurchaseWithMarket) {
                purchasableObj.put(JSONConsts.PURCHASE_TYPE, JSONConsts.PURCHASE_TYPE_MARKET);

                MarketItem mi = ((PurchaseWithMarket) mPurchaseType).getMarketItem();
                purchasableObj.put(JSONConsts.PURCHASE_MARKET_ITEM, mi.toJSONObject());
            } else if(mPurchaseType instanceof PurchaseWithVirtualItem) {
                purchasableObj.put(JSONConsts.PURCHASE_TYPE, JSONConsts.PURCHASE_TYPE_VI);

                purchasableObj.put(JSONConsts.PURCHASE_VI_ITEMID,
                        ((PurchaseWithVirtualItem) mPurchaseType).getTargetItemId());
                purchasableObj.put(JSONConsts.PURCHASE_VI_AMOUNT,
                        ((PurchaseWithVirtualItem) mPurchaseType).getAmount());
            }

            jsonObject.put(JSONConsts.PURCHASABLE_ITEM, purchasableObj);
        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }

    /**
     * Buys the <code>PurchasableVirtualItem</code>, after checking if the user is in a state that allows him
     * to buy. This action uses the associated <code>PurchaseType</code> to perform the purchase.
     *
     * @throws InsufficientFundsException if the user does not have enough funds to buy()
     */
    public void buy() throws InsufficientFundsException {
        if (!canBuy()) return;

        mPurchaseType.buy();
    }

    /**
     * Determines if user is in a state that allows him to buy a specific <code>VirtualItem</code>.
     *
     * @return true if can buy, false otherwise
     */
    protected abstract boolean canBuy();


    /** Setters and Getters */

    public PurchaseType getPurchaseType() {
        return mPurchaseType;
    }


    /** Private Members */

    private static final String TAG = "SOOMLA PurchasableVirtualItem"; //used for Log messages

    private PurchaseType mPurchaseType; //the way this PurchasableVirtualItem is purchased.
}

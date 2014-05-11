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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A representation of an item in the market.
 * <code>MarketItem</code> is only used for <code>PurchaseWithMarket</code> purchase type.
 */
public class MarketItem {

    /**
     * Constructor
     *
     * @param mProductId the Id of the current item in the market.
     * @param mManaged the Managed type of the current item in the market.
     * @param mPrice the actual $$ cost of the current item in the market.
     */
    public MarketItem(String mProductId, Managed mManaged, double mPrice) {
        this.mProductId = mProductId;
        this.mManaged = mManaged;
        this.mPrice = mPrice;
    }

    /**
     * Constructor
     * Generates an instance of {@link MarketItem} from a <code>JSONObject<code>
     *
     * @param jsonObject a <code>JSONObject<code> representation of the wanted {@link MarketItem}
     * @throws JSONException
     */
    public MarketItem(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(JSONConsts.MARKETITEM_MANAGED)) {
            this.mManaged = Managed.values()[jsonObject.getInt(JSONConsts.MARKETITEM_MANAGED)];
        } else {
            this.mManaged = Managed.UNMANAGED;
        }
        if (jsonObject.has(JSONConsts.MARKETITEM_ANDROID_ID)) {
            this.mProductId = jsonObject.getString(JSONConsts.MARKETITEM_ANDROID_ID);
        } else {
            this.mProductId = jsonObject.getString(JSONConsts.MARKETITEM_PRODUCT_ID);
        }
        this.mPrice = jsonObject.getDouble(JSONConsts.MARKETITEM_PRICE);
    }

    /**
     * Converts the current <code>VirtualItem</code> to a <code>JSONObject</code>.
     *
     * @return a <code>JSONObject</code> representation of the current <code>VirtualItem</code>.
     */
    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONConsts.MARKETITEM_MANAGED, mManaged.ordinal());
            jsonObject.put(JSONConsts.MARKETITEM_ANDROID_ID, mProductId);
            jsonObject.put(JSONConsts.MARKETITEM_PRICE, new Double(mPrice));
        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }

    /**
     * Each product in the catalog can be MANAGED, UNMANAGED, or SUBSCRIPTION.
     * MANAGED means that the product can be purchased only once per user (such as a new level in
     * a game). This purchase is remembered by the Market and can be restored if this
     * application is uninstalled and then re-installed.
     * UNMANAGED is used for products that can be used up and purchased multiple times (such as
     * poker chips). It is up to the application to keep track of UNMANAGED products for the user.
     * SUBSCRIPTION is just like MANAGED except that the user gets charged periodically (monthly
     * or yearly).
     */
    public static enum Managed { MANAGED, UNMANAGED, SUBSCRIPTION }


    /** Setters and Getters **/

    public void setMarketPrice(String mMarketPrice) {
        this.mMarketPrice = mMarketPrice;
    }

    public void setMarketTitle(String mMarketTitle) {
        this.mMarketTitle = mMarketTitle;
    }

    public void setMarketDescription(String mMarketDescription) {
        this.mMarketDescription = mMarketDescription;
    }

    public String getProductId() {
        return mProductId;
    }

    public Managed getManaged() {
        return mManaged;
    }

    public void setManaged(Managed managed) {
        this.mManaged = managed;
    }

    public double getPrice() {
        return mPrice;
    }

    public String getMarketPrice() {
        return mMarketPrice;
    }

    public String getMarketTitle() {
        return mMarketTitle;
    }

    public String getMarketDescription() {
        return mMarketDescription;
    }


    /** Private Members **/

    private static final String TAG = "SOOMLA MarketItem"; //used for Log messages

    private Managed mManaged; //the Managed type of the current item in the market.

    private String mProductId; //id of this VirtualGood in the market

    private double mPrice; //the actual $$ cost of the current item in the market.

    private String mMarketPrice;

    private String mMarketTitle;

    private String mMarketDescription;
}

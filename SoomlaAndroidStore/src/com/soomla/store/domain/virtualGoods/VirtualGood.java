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

import com.soomla.store.data.StorageManager;
import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is an abstract representation of the application's virtual good.
 * Your game's virtual economy revolves around virtual goods. This class defines the abstract
 * and most common virtual good while the descendants of this class define specific definitions
 * of virtual good(s).
 *
 * Inheritance: {@link com.soomla.store.domain.virtualGoods.VirtualGood} >
 * {@link com.soomla.store.domain.PurchasableVirtualItem} >
 * {@link com.soomla.store.domain.VirtualItem}
 */
public abstract class VirtualGood extends PurchasableVirtualItem {

    /**
     * Constructor
     *
     * @param mName see parent
     * @param mDescription see parent
     * @param mItemId see parent
     * @param purchaseType see parent
     */
    public VirtualGood(String mName, String mDescription,
                       String mItemId, PurchaseType purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);
    }

    /**
     * Constructor
     *
     * @param jsonObject see parent
     * @throws JSONException
     */
    public VirtualGood(JSONObject jsonObject) throws JSONException{
        super(jsonObject);
    }

    /**
     * see parent
     *
     * @return see parent
     */
    @Override
    public JSONObject toJSONObject(){
        return super.toJSONObject();
    }

    /**
     * see parent
     *
     * @param balance see parent
     * @param notify see parent
     * @return see parent
     */
    @Override
    public int resetBalance(int balance, boolean notify) {
        return StorageManager.getVirtualGoodsStorage().setBalance(this, balance, notify);
    }

    /** Private Members **/

    private static final String TAG = "SOOMLA VirtualGood"; //used for Log messages

}

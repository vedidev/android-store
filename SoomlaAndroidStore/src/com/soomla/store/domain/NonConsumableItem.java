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
import com.soomla.store.data.StorageManager;
import com.soomla.store.purchaseTypes.PurchaseWithMarket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A representation of a non-consumable item in the Market. These kinds of items are bought by the
 * user once and kept for him forever.
 * 
 * NOTE: Don't be confused: this is not a Lifetime <code>VirtualGood</code>, it's a MANAGED item in
 * the Market. This means that the product can be purchased only once per user (such as a new level
 * in a game), and is remembered by the Market (can be restored if this application is uninstalled
 * and then re-installed).
 * If you want to make a <code>LifetimeVG</code> available for purchase in the market (purchase with
 * real money $$), you will need to declare it as a <code>NonConsumableItem</code>.
 *
 * Inheritance: {@link com.soomla.store.domain.NonConsumableItem} >
 * {@link com.soomla.store.domain.PurchasableVirtualItem} >
 * {@link com.soomla.store.domain.VirtualItem}
 */
public class NonConsumableItem extends PurchasableVirtualItem {

    /**
     * Constructor
     *
     * @param mName see parent
     * @param mDescription see parent
     * @param mItemId see parent
     * @param purchaseType see parent
     */
    public NonConsumableItem(String mName, String mDescription, String mItemId,
                             PurchaseWithMarket purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);
    }

    /**
     * Constructor
     *
     * see parent
     */
    public NonConsumableItem(JSONObject jsonObject) throws JSONException {
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
     * @param amount see parent
     * @return see parent
     */
    @Override
    public int give(int amount, boolean notify) {
        return StorageManager.getNonConsumableItemsStorage().add(this) ? 1 : 0;
    }

    /**
     * see parent
     *
     * @param amount see parent
     * @return see parent
     */
    @Override
    public int take(int amount, boolean notify) {
        return StorageManager.getNonConsumableItemsStorage().remove(this) ? 1 : 0;
    }

    /**
     * Determines if user is in a state that allows him to buy a <code>NonConsumableItem</code> by checking
     * if the user already owns such an item. If he does, he cannot purchase this item again
     * because <code>NonConsumableItems</code> can only be purchased once!
     *
     * @return True if the user does NOT own such an item, False otherwise.
     */
    @Override
    protected boolean canBuy() {
        if (StorageManager.getNonConsumableItemsStorage().nonConsumableItemExists(this)) {
            StoreUtils.LogDebug(TAG,
                    "You can't buy a NonConsumableItem that was already given to the user.");
            return false;
        }
        return true;
    }

    /**
     * see parent
     *
     * @param balance see parent
     * @return balance after the resetting process, can be either 0 or 1
     */
    @Override
    public int resetBalance(int balance, boolean notify) {
        if (balance > 0) {
            return StorageManager.getNonConsumableItemsStorage().add(this) ? 1 : 0;
        } else {
            return StorageManager.getNonConsumableItemsStorage().remove(this) ? 1 : 0;
        }
    }

    /** Private members **/

    private static final String TAG = "SOOMLA NonConsumableItem"; //used for Log messages
}

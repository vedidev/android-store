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

import com.soomla.store.data.StorageManager;
import com.soomla.store.domain.VirtualItem;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is a representation of a game's virtual currency.
 * Each game can have multiple instances of a virtual currency, all kept in
 * {@link com.soomla.store.data.StoreInfo}
 *
 * Real Game Examples: 'Coin', 'Gem', 'Muffin'
 *
 * NOTE: This item is NOT purchasable!
 * However, a <code>VirtualCurrencyPack</code> IS purchasable.
 * For example, if the virtual currency in your game is a 'Coin' and you want to make a single
 * 'Coin' available for purchase you will need to define a <code>VirtualCurrencyPack</code> of 1
 * 'Coin'.
 */
public class VirtualCurrency extends VirtualItem {

    /**
     * Constructor
     * see parent
     *
     * @param mName the name of the virtual item
     * @param mDescription the description of the virtual item
     * @param itemId the itemId of the virtual item
     */
    public VirtualCurrency(String mName, String mDescription, String itemId) {
        super(mName, mDescription, itemId);
    }

    /**
     * Constructor
     *
     * @param jsonObject a JSONObject representation of the wanted VirtualItem
     * @throws JSONException
     */
    public VirtualCurrency(JSONObject jsonObject) throws JSONException {
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
     * @param amount the amount of the specific item to be given
     * @param notify notify of change in user's balance of current virtual item
     * @return see parent
     */
    @Override
    public int give(int amount, boolean notify) {
        return StorageManager.getVirtualCurrencyStorage().add(this, amount, notify);
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
        return StorageManager.getVirtualCurrencyStorage().remove(this, amount, notify);
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
        return StorageManager.getVirtualCurrencyStorage().setBalance(this, balance, notify);
    }
}

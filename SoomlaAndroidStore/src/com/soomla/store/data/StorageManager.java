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

package com.soomla.store.data;

import com.soomla.store.domain.VirtualItem;
import com.soomla.store.domain.virtualCurrencies.VirtualCurrency;
import com.soomla.store.domain.virtualGoods.VirtualGood;

/**
 * In this class all the relevant storage classes are created.
 * This class contains static methods for you to retrieve the various storage bases.
 */
public class StorageManager {

    /** Setters and Getters **/

    public static VirtualCurrencyStorage getVirtualCurrencyStorage() {

        return mVirtualCurrencyStorage;
    }

    public static VirtualGoodsStorage getVirtualGoodsStorage() {
        return mVirtualGoodsStorage;
    }

    public static NonConsumableItemsStorage getNonConsumableItemsStorage() {
        return mNonConsumableItemsStorage;
    }

    public static KeyValueStorage getKeyValueStorage() {
        return mKeyValueStorage;
    }

    /**
     * Checks whether the given item belongs to <code>VirtualGoodStorage</code> or
     * <code>VirtualCurrencyStorage</code>.
     *
     * @param item the item to check what type of storage it belongs to.
     * @return the type of VirtualItemStorage.
     */
    public static VirtualItemStorage getVirtualItemStorage(VirtualItem item) {
        VirtualItemStorage storage = null;
        if (item instanceof VirtualGood) {
            storage = getVirtualGoodsStorage();
        } else if (item instanceof VirtualCurrency) {
            storage = getVirtualCurrencyStorage();
        }
        return storage;
    }


    /** Private Members **/

    private static final String TAG = "SOOMLA StorageManager"; //used for Log messages

    // storage of all virtual goods
    private static VirtualGoodsStorage mVirtualGoodsStorage = new VirtualGoodsStorage();

    // storage of all virtual currencies
    private static VirtualCurrencyStorage mVirtualCurrencyStorage = new VirtualCurrencyStorage();

    // storage of all non-consumable items
    private static NonConsumableItemsStorage mNonConsumableItemsStorage =
            new NonConsumableItemsStorage();

    // key-value storage
    private static KeyValueStorage mKeyValueStorage = new KeyValueStorage();

}

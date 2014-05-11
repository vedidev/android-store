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

import com.soomla.store.StoreUtils;
import com.soomla.store.domain.NonConsumableItem;

/**
 * This class provides basic storage operations on the Market's MANAGED items.
 */
public class NonConsumableItemsStorage {

    /**
     * Constructor
     */
    public NonConsumableItemsStorage() {
    }

    /**
     * Checks if the given {@link NonConsumableItem} exists.
     *
     * @param nonConsumableItem the non-consumable to check if exists.
     * @return true if the given item exists, false otherwise.
     */
    public boolean nonConsumableItemExists(NonConsumableItem nonConsumableItem){

        StoreUtils.LogDebug(TAG, "Checking if the given MANAGED item exists.");

        String itemId = nonConsumableItem.getItemId();
        String key = KeyValDatabase.keyNonConsExists(itemId);

        String val = StorageManager.getKeyValueStorage().getValue(key);

        return val != null;
    }

    /**
     * Adds the given non-consumable item to the storage.
     *
     * @param nonConsumableItem the required non-consumable item.
     * @return true
     */
    public boolean add(NonConsumableItem nonConsumableItem){
        StoreUtils.LogDebug(TAG, "Adding " + nonConsumableItem.getItemId());

        String itemId = nonConsumableItem.getItemId();
        String key = KeyValDatabase.keyNonConsExists(itemId);

        StorageManager.getKeyValueStorage().setValue(key, "");

        return true;
    }

    /**
     * Removes the given non-consumable item from the storage.
     *
     * @param nonConsumableItem the required non-consumable item.
     * @return false
     */
    public boolean remove(NonConsumableItem nonConsumableItem){
        StoreUtils.LogDebug(TAG, "Removing " + nonConsumableItem.getName());

        String itemId = nonConsumableItem.getItemId();
        String key = KeyValDatabase.keyNonConsExists(itemId);

        StorageManager.getKeyValueStorage().deleteKeyValue(key);

        return false;
    }


    /** Private Members **/

    private static final String TAG = "SOOMLA NonConsumableItemsStorage"; //used for Log messages
}

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

package com.soomla.store.data;

import android.util.Log;
import com.soomla.store.StoreConfig;
import com.soomla.store.domain.data.NonConsumableItem;

/**
 * This class provide basic storage operations on Google Play's MANAGED items.
 */
public class NonConsumableItemsStorage {

    /** Constructor
     *
     */
    public NonConsumableItemsStorage() {
    }

    /** Public functions **/

    /**
     * Figure out if the given non-consumable {@link NonConsumableItem} exists.
     * @param nonConsumableItem the required non-consumable {@link NonConsumableItem}.
     * @return whether the given item exists or not.
     */
    public boolean nonConsumableItemExists(NonConsumableItem nonConsumableItem){

        if (StoreConfig.debug){
            Log.d(TAG, "trying to figure out if the given MANAGED item exists.");
        }

        String productId = nonConsumableItem.getProductId();
        String key = KeyValDatabase.keyNonConsExists(productId);
        key = StorageManager.getAESObfuscator().obfuscateString(key);

        String val = StorageManager.getDatabase().getKeyVal(key);

        return val != null;
    }

    /**
     * Adds the given google non-consumable item to the storage.
     * @param nonConsumableItem is the required google non-consumable item.
     */
    public void add(NonConsumableItem nonConsumableItem){
        if (StoreConfig.debug){
            Log.d(TAG, "adding " + nonConsumableItem.getProductId());
        }

        String productId = nonConsumableItem.getProductId();
        String key = KeyValDatabase.keyNonConsExists(productId);
        key = StorageManager.getAESObfuscator().obfuscateString(key);
        StorageManager.getDatabase().setKeyVal(key, "");
    }

    /**
     * Removes the given google non-consumable item from the storage.
     * @param nonConsumableItem is the required google non-consumable item.
     */
    public void remove(NonConsumableItem nonConsumableItem){
        if (StoreConfig.debug){
            Log.d(TAG, "removing " + nonConsumableItem.getProductId());
        }

        String productId = nonConsumableItem.getProductId();
        String key = KeyValDatabase.keyNonConsExists(productId);
        key = StorageManager.getAESObfuscator().obfuscateString(key);
        StorageManager.getDatabase().deleteKeyVal(key);
    }

    /** Private members **/

    private static final String TAG = "SOOMLA NonConsumableItemsStorage";
}

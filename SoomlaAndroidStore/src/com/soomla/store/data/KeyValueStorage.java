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

import android.database.Cursor;
import android.util.Log;
import com.soomla.billing.util.AESObfuscator;
import com.soomla.store.StoreConfig;

/**
 * This class provides basic storage operations for a simple key-value store.
 */
public class KeyValueStorage {

    /** Constructor
     *
     */
    public KeyValueStorage() {
    }

    /**
     * Fetch the value for the given key.
     * @param key is the key in the key-val pair.
     * @return the value for the given key.
     */
    public String getValue(String key) {
        if (StoreConfig.debug){
            Log.d(TAG, "trying to fetch a value for key: " + key);
        }

        if (StorageManager.getObfuscator() != null){
            key = StorageManager.getObfuscator().obfuscateString(key);
        }

        Cursor cursor = StorageManager.getDatabase().getKeyValVal(key);

        if (cursor == null) {
            return "";
        }

        try {
            int valCol = cursor.getColumnIndexOrThrow(
                    StoreDatabase.KEYVAL_COLUMN_VAL);
            if (cursor.moveToNext()) {
                String valStr = cursor.getString(valCol);
                if (StorageManager.getObfuscator() != null){
                    valStr = StorageManager.getObfuscator().unobfuscateToString(valStr);
                }

                if (StoreConfig.debug){
                    Log.d(TAG, "the fetched value is " + valStr);
                }
                return valStr;
            }
        } catch (AESObfuscator.ValidationException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return "";
    }

    /**
     * Sets the given value to the given key.
     * @param key is the key in the key-val pair.
     * @param val is the val in the key-val pair.
     */
    public void setValue(String key, String val) {
        if (StoreConfig.debug){
            Log.d(TAG, "setting " + val + " for key: " + key);
        }

        if (StorageManager.getObfuscator() != null){
            key = StorageManager.getObfuscator().obfuscateString(key);
            val = StorageManager.getObfuscator().obfuscateString(val);
        }

        StorageManager.getDatabase().setKeyValVal(key, val);
    }


    /** Private Members **/

    private static final String TAG = "SOOMLA KeyValueStorage";
}

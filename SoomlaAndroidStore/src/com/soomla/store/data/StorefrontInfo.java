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

import android.text.TextUtils;
import android.util.Log;
import com.soomla.billing.util.AESObfuscator;
import com.soomla.store.SoomlaApp;
import com.soomla.store.StoreConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is used to retrieve the storefront JSON when it's needed.
 */
public class StorefrontInfo {

    public static StorefrontInfo getInstance(){
        if (sInstance == null){
            sInstance = new StorefrontInfo();
        }

        return sInstance;
    }

    /**
     * This function initializes StorefrontInfo. On first initialization, when the
     * database doesn't have any previous version of the store metadata (JSON), StorefrontInfo
     * saves the given JSON to the DB. After the first initialization,
     * StorefrontInfo will load the JSON metadata when needed.
     * NOTE: If you want to override the current StorefrontInfo metadata JSON file, you'll have to bump the
     * database version (the old database will be destroyed but balances will be saved!!).
     */
    public void initialize(){
        if (!initializeFromDB()) {
            // if the json doesn't already exist in the database, we load it into the DB here.

            String storefrontJSON = fetchTemeJsonFromFile();
            if (TextUtils.isEmpty(storefrontJSON)){
                Log.e(TAG, "Couldn't find storefront in the DB AND the filesystem. Something is totally wrong !");
                return;
            }

            mStorefrontJSON = storefrontJSON;
            String key = KeyValDatabase.keyMetaStorefrontInfo();
            if (StorageManager.getObfuscator() != null){
                storefrontJSON = StorageManager.getObfuscator().obfuscateString(storefrontJSON);
                key = StorageManager.getObfuscator().obfuscateString(key);
            }
            StorageManager.getDatabase().setKeyVal(key, storefrontJSON);

            mInitialized = true;
        }
    }

    public boolean initializeFromDB() {

        String key = KeyValDatabase.keyMetaStorefrontInfo();
        if (StorageManager.getObfuscator() != null){
            key = StorageManager.getObfuscator().obfuscateString(key);
        }

        String val = StorageManager.getDatabase().getKeyVal(key);

        if (val == null && TextUtils.isEmpty(val)){
            if (StoreConfig.debug){
                Log.d(TAG, "storefront json is not in DB yet ");
            }
            return false;
        }

        if (StorageManager.getObfuscator() != null){
            try {
                mStorefrontJSON = StorageManager.getObfuscator().unobfuscateToString(val);
            } catch (AESObfuscator.ValidationException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }

        if (StoreConfig.debug){
            Log.d(TAG, "the metadata json (from DB) is " + mStorefrontJSON);
        }

        mInitialized = true;
        return true;

    }

    public String fetchTemeJsonFromFile() {
        String storefrontJSON = "";
        try {
            InputStream in = SoomlaApp.getAppContext().getAssets().open("theme.json");

            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(buffer);
            out.close();
            in.close();

            storefrontJSON = out.toString();
        } catch (IOException e) {
            Log.e(TAG, "Can't read JSON storefront file. Please add theme.json to your 'assets' folder.");
        }

        return storefrontJSON;
    }

    public String getStorefrontJSON() {
        if (!mInitialized) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Couldn't initialize StoreFrontInfo. Can't fetch the JSON!");
                return "";
            }
        }
        return mStorefrontJSON;
    }

    /** Private functions **/

    private StorefrontInfo() { }

    /** Private members **/

    private static final String TAG = "SOOMLA StorefrontInfo";
    private static StorefrontInfo sInstance = null;
    private static boolean mInitialized = false;

    private String  mStorefrontJSON;
}
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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.soomla.billing.util.AESObfuscator;
import com.soomla.store.SoomlaApp;
import com.soomla.store.StoreConfig;

/**
 * This is the place where all the relevant storage classes are created.
 * This is a singleton class and you can call it from your application in order
 * to get the instances of the Virtual goods/currency storages.
 *
 * You will usually need the storage in order to get/set the amounts of virtual goods/currency.
 */
public class StorageManager {

    /** Getters **/

    public static VirtualCurrencyStorage getVirtualCurrencyStorage(){
        return mVirtualCurrencyStorage;
    }

    public static VirtualGoodsStorage getVirtualGoodsStorage(){
        return mVirtualGoodsStorage;
    }

    public static AESObfuscator getObfuscator(){
        if (mObfuscator == null) {
            String deviceId = Settings.Secure.getString(SoomlaApp.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            mObfuscator = new AESObfuscator(StoreConfig.obfuscationSalt, SoomlaApp.getAppContext().getPackageName(), deviceId);
        }

        return mObfuscator;
    }

    public static KeyValDatabase getDatabase(){

        if (mKvDatabase == null) {
            mKvDatabase = new KeyValDatabase(SoomlaApp.getAppContext());

            migrateOldData();

            SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext(),
                    SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
            int mt_ver = prefs.getInt("MT_VER", 0);
            int sa_ver_old = prefs.getInt("SA_VER_OLD", -1);
            int sa_ver_new = prefs.getInt("SA_VER_NEW", 0);
//            boolean mt_override = prefs.getBoolean("MT_FORCE_DELETE", false);
            if (mt_ver < StoreConfig.METADATA_VERSION || sa_ver_old < sa_ver_new) {// || mt_override) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("MT_VER", StoreConfig.METADATA_VERSION);
                edit.putInt("SA_VER_OLD", sa_ver_new);
//                edit.putBoolean("MT_FORCE_DELETE", false);
                edit.commit();

                mKvDatabase.deleteKeyVal(KeyValDatabase.keyMetaStorefrontInfo());
                mKvDatabase.deleteKeyVal(KeyValDatabase.keyMetaStoreInfo());
            }
        }

        return mKvDatabase;
    }

    public static NonConsumableItemsStorage getNonConsumableItemsStorage() {
        return mNonConsumableItemsStorage;
    }

    public static KeyValueStorage getKeyValueStorage() {
        return mKeyValueStorage;
    }


    /** Private members **/
    private static final String TAG = "SOOMLA StorageManager";

    private static boolean mOldDataMigrated = false;

    private static VirtualGoodsStorage     mVirtualGoodsStorage        = new VirtualGoodsStorage();
    private static VirtualCurrencyStorage  mVirtualCurrencyStorage     = new VirtualCurrencyStorage();
    private static NonConsumableItemsStorage mNonConsumableItemsStorage = new NonConsumableItemsStorage();
    private static KeyValueStorage         mKeyValueStorage            = new KeyValueStorage();
    private static AESObfuscator           mObfuscator;
    private static KeyValDatabase          mKvDatabase;
















    /** Migration of databases for versions < v2.0 **/
    /** This code will be removed in the next version of android-store. Along with the StoreDatabase itself.**/

    private static void migrateOldData() {
        if (mOldDataMigrated) {
            return;
        }

        if (!StoreDatabase.checkDataBaseExists(SoomlaApp.getAppContext())) {
            if (StoreConfig.debug) {
                Log.d(TAG, "Old store database doesn't exist. Nothing to migrate.");
            }
            return;
        }

        if (StoreConfig.debug) {
            Log.d(TAG, "Old store database exists. Migrating now!");
        }

        StoreDatabase storeDatabase = new StoreDatabase(SoomlaApp.getAppContext());

        Cursor cursor = storeDatabase.getVirtualCurrencies();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int itemIdColIdx = cursor.getColumnIndexOrThrow(StoreDatabase.VIRTUAL_CURRENCY_COLUMN_ITEM_ID);
                int balanceColIdx = cursor.getColumnIndexOrThrow(StoreDatabase.VIRTUAL_CURRENCY_COLUMN_BALANCE);
                String itemIdStr = cursor.getString(itemIdColIdx);
                if (StorageManager.getObfuscator() != null){
                    try {
                        itemIdStr = mObfuscator.unobfuscateToString(itemIdStr);
                    } catch (AESObfuscator.ValidationException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                String balanceStr = cursor.getString(balanceColIdx);

                String key = KeyValDatabase.keyCurrencyBalance(itemIdStr);
                Log.d(TAG, "currency key: " + key + " val: " + balanceStr);
                if (StorageManager.getObfuscator() != null){
                    key = mObfuscator.obfuscateString(key);
                }
                mKvDatabase.setKeyVal(key, balanceStr);

            }
        }
        if (cursor != null) {
            cursor.close();
        }

        cursor = storeDatabase.getVirtualGoods();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int itemIdColIdx = cursor.getColumnIndexOrThrow(StoreDatabase.VIRTUAL_GOODS_COLUMN_ITEM_ID);
                int balanceColIdx = cursor.getColumnIndexOrThrow(StoreDatabase.VIRTUAL_GOODS_COLUMN_BALANCE);
                int equippedColIdx = cursor.getColumnIndexOrThrow(StoreDatabase.VIRTUAL_GOODS_COLUMN_EQUIPPED);
                String itemIdStr = cursor.getString(itemIdColIdx);
                try {
                    itemIdStr = mObfuscator.unobfuscateToString(itemIdStr);
                } catch (AESObfuscator.ValidationException e) {
                    e.printStackTrace();
                }
                String balanceStr = cursor.getString(balanceColIdx);
                int equippedInt = cursor.getInt(equippedColIdx);

                String key = KeyValDatabase.keyGoodBalance(itemIdStr);
                try {
                    Log.d(TAG, "good key: " + key + " val: " + mObfuscator.unobfuscateToString(balanceStr));
                } catch (AESObfuscator.ValidationException e) {
                    e.printStackTrace();
                }
                if (StorageManager.getObfuscator() != null){
                    key = mObfuscator.obfuscateString(key);
                }
                mKvDatabase.setKeyVal(key, balanceStr);
                if (equippedInt > 0) {
                    mKvDatabase.setKeyVal(key, "");
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        cursor = storeDatabase.getMetaData();
        if (cursor != null) {
            int storeinfoIdx = cursor.getColumnIndexOrThrow(StoreDatabase.METADATA_COLUMN_STOREINFO);
            int storefrontinfoIdx = cursor.getColumnIndexOrThrow(StoreDatabase.METADATA_COLUMN_STOREFRONTINFO);
            if (cursor.moveToNext()) {
                String storeInfo = cursor.getString(storeinfoIdx);
                String storefrontInfo = cursor.getString(storefrontinfoIdx);

                String key = KeyValDatabase.keyMetaStoreInfo();
                Log.d(TAG, "meta1 key: " + key + " val: " + storeInfo);
                if (StorageManager.getObfuscator() != null){
                    key = mObfuscator.obfuscateString(key);
                }
                if (!TextUtils.isEmpty(storeInfo)) {
                    mKvDatabase.setKeyVal(key, storeInfo);
                }

                key = KeyValDatabase.keyMetaStorefrontInfo();
                Log.d(TAG, "meta1 key: " + key + " val: " + storefrontInfo);
                if (StorageManager.getObfuscator() != null){
                    key = mObfuscator.obfuscateString(key);
                }
                if (!TextUtils.isEmpty(storefrontInfo)) {
                    mKvDatabase.setKeyVal(key, storefrontInfo);
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        cursor = storeDatabase.getGoogleManagedItems();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int productIdColIdx = cursor.getColumnIndexOrThrow(StoreDatabase.GOOGLE_MANAGED_ITEMS_COLUMN_PRODUCT_ID);
                String productIdStr = cursor.getString(productIdColIdx);
                try {
                    productIdStr = mObfuscator.unobfuscateToString(productIdStr);
                } catch (AESObfuscator.ValidationException e) {
                    e.printStackTrace();
                }

                String key = KeyValDatabase.keyNonConsExists(productIdStr);
                Log.d(TAG, "gmi key: " + key + " val: " + "");
                if (StorageManager.getObfuscator() != null){
                    key = mObfuscator.obfuscateString(key);
                }
                mKvDatabase.setKeyVal(key, "");
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        cursor = storeDatabase.getKeyVals();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int keyColIdx = cursor.getColumnIndexOrThrow(StoreDatabase.KEYVAL_COLUMN_KEY);
                int valColIdx = cursor.getColumnIndexOrThrow(StoreDatabase.KEYVAL_COLUMN_KEY);
                String keyStr = cursor.getString(keyColIdx);
                String valStr = cursor.getString(valColIdx);

                Log.d(TAG, "kv key: " + keyStr + " val: " + valStr);
                mKvDatabase.setKeyVal(keyStr, valStr);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        storeDatabase.purgeDatabase(SoomlaApp.getAppContext());
        storeDatabase.close();

        mOldDataMigrated = true;

        if (StoreConfig.debug) {
            Log.d(TAG, "Finished migrating old database.");
        }
    }
}

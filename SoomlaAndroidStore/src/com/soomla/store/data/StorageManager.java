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

import android.provider.Settings;
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
        if (mObfuscator == null && StoreConfig.DB_SECURE) {
            String deviceId = Settings.Secure.getString(SoomlaApp.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            mObfuscator = new AESObfuscator(StoreConfig.obfuscationSalt, SoomlaApp.getAppContext().getPackageName(), deviceId);
        }

        return mObfuscator;
    }

    public static StoreDatabase getDatabase(){

        if (mDatabase == null) {
            mDatabase = new StoreDatabase(SoomlaApp.getAppContext());
        }

        return mDatabase;
    }

    public static GoogleManagedItemsStorage getGoogleManagedItemsStorage() {
        return mGoogleManagedItemsStorage;
    }

    public static KeyValueStorage getKeyValueStorage() {
        return mKeyValueStorage;
    }


    /** Private members **/
    private static final String TAG = "SOOMLA StorageManager";

    private static boolean initialized = false;

    private static VirtualGoodsStorage     mVirtualGoodsStorage        = new VirtualGoodsStorage();
    private static VirtualCurrencyStorage  mVirtualCurrencyStorage     = new VirtualCurrencyStorage();
    private static GoogleManagedItemsStorage mGoogleManagedItemsStorage = new GoogleManagedItemsStorage();
    private static KeyValueStorage         mKeyValueStorage            = new KeyValueStorage();
    private static AESObfuscator           mObfuscator;
    private static StoreDatabase           mDatabase;
}

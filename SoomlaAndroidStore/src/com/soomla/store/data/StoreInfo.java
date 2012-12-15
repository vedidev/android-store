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
import android.text.TextUtils;
import android.util.Log;
import com.soomla.billing.util.AESObfuscator;
import com.soomla.store.IStoreAssets;
import com.soomla.store.StoreConfig;
import com.soomla.store.domain.data.*;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class holds the store's meta data including:
 * - Virtual Currencies definitions
 * - Virtual Currency Packs definitions
 * - Virtual Goods definitions
 * - Virtual Categories definitions
 */
public class StoreInfo {

    /**
     * This function initializes StoreInfo. On first initialization, when the
     * database doesn't have any previous version of the store metadata, StoreInfo
     * is being loaded from the given {@link IStoreAssets}. After the first initialization,
     * StoreInfo will be initialized from the database.
     * NOTE: If you want to override the current StoreInfo, you'll have to bump the
     * database version (the old database will be destroyed) OR just set StoreConfig.DB_VOLATILE_METADATA
     * to "true" in order to always remove the metadata when the application loads.
     */
    public static void setStoreAssets(IStoreAssets storeAssets){
        if (storeAssets == null){
            Log.e(TAG, "The given store assets can't be null !");
            return;
        }

        // we prefer initialization from the database (storeAssets are only set on the first time the game is loaded)!
        if (!initializeFromDB()){
            /// fall-back here if the json doesn't exist, we load the store from the given {@link IStoreAssets}.
            mVirtualCategories    = Arrays.asList(storeAssets.getVirtualCategories());
            mVirtualCurrencies    = Arrays.asList(storeAssets.getVirtualCurrencies());
            mVirtualCurrencyPacks = Arrays.asList(storeAssets.getVirtualCurrencyPacks());
            mVirtualGoods         = Arrays.asList(storeAssets.getVirtualGoods());
            mGoogleManagedItems   = Arrays.asList(storeAssets.getGoogleManagedItems());

            // put StoreInfo in the database as JSON
            String store_json = toJSONObject().toString();
            if (StorageManager.getObfuscator() != null){
                store_json = StorageManager.getObfuscator().obfuscateString(store_json);
            }
            StorageManager.getDatabase().setStoreInfo(store_json);
        }
    }

    public static boolean initializeFromDB() {
        // first, trying to load StoreInfo from the local DB.
        Cursor cursor = StorageManager.getDatabase().getMetaData();
        if (cursor != null) {
            String storejson = "";
            try {
                int storeVal = cursor.getColumnIndexOrThrow(
                        StoreDatabase.METADATA_COLUMN_STOREINFO);
                if (cursor.moveToNext()) {
                    storejson = cursor.getString(storeVal);

                    if (TextUtils.isEmpty(storejson)){
                        if (StoreConfig.debug){
                            Log.d(TAG, "store json is not in DB yet ");
                        }
                        return false;
                    }

                    if (StorageManager.getObfuscator() != null){
                        storejson = StorageManager.getObfuscator().unobfuscateToString(storejson);
                    }

                    if (StoreConfig.debug){
                        Log.d(TAG, "the metadata json (from DB) is " + storejson);
                    }
                }
            } catch (AESObfuscator.ValidationException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }

            if (!TextUtils.isEmpty(storejson)){
                try {
                    fromJSONObject(new JSONObject(storejson));

                    // everything went well... StoreInfo is initialized from the local DB.
                    // it's ok to return now.
                    return true;
                } catch (JSONException e) {
                    if (StoreConfig.debug){
                        Log.d(TAG, "Can't parse metadata json. Going to return false and make " +
                                "StoreInfo load from static data.: " + storejson);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Use this function if you need to know the definition of a specific virtual currency pack.
     * @param productId is the requested pack's product id.
     * @return the definition of the virtual pack requested.
     * @throws VirtualItemNotFoundException
     */
    public static VirtualCurrencyPack getPackByGoogleProductId(String productId) throws VirtualItemNotFoundException {
        if (mVirtualCurrencyPacks == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        for(VirtualCurrencyPack p : mVirtualCurrencyPacks){
            if (p.getmGoogleItem().getProductId().equals(productId)){
                return p;
            }
        }

        throw new VirtualItemNotFoundException("productId", productId);
    }

    /**
     * Use this function if you need to know the definition of a specific virtual currency pack.
     * @param itemId is the requested pack's item id.
     * @return the definition of the virtual pack requested.
     * @throws VirtualItemNotFoundException
     */
    public static VirtualCurrencyPack getPackByItemId(String itemId) throws VirtualItemNotFoundException {
        if (mVirtualCurrencyPacks == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        for(VirtualCurrencyPack p : mVirtualCurrencyPacks){
            if (p.getItemId().equals(itemId)){
                return p;
            }
        }

        throw new VirtualItemNotFoundException("itemId", itemId);
    }

    /**
     * Use this function if you need to know the definition of a specific virtual good.
     * @param itemId is the requested good's item id.
     * @return the definition of the virtual good requested.
     * @throws VirtualItemNotFoundException
     */
    public static VirtualGood getVirtualGoodByItemId(String itemId) throws VirtualItemNotFoundException {
        if (mVirtualGoods == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        for(VirtualGood g : mVirtualGoods){
            if (g.getItemId().equals(itemId)){
                return g;
            }
        }

        throw new VirtualItemNotFoundException("itemId", itemId);
    }

    /**
     * Use this function if you need to know the definition of a specific virtual category.
     * @param id is the requested category's id.
     * @return the definition of the requested category.
     * @throws VirtualItemNotFoundException
     */
    public static VirtualCategory getVirtualCategoryById(int id) throws VirtualItemNotFoundException {
        if (mVirtualCategories == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        for(VirtualCategory c : mVirtualCategories){
            if (c.getmId() == id){
                return c;
            }
        }

        throw new VirtualItemNotFoundException("id", "" + id);
    }

    /**
     * Use this function if you need to know the definition of a specific virtual currency.
     * @param itemId is the requested currency's item id.
     * @return the definition of the virtual currency requested.
     * @throws VirtualItemNotFoundException
     */
    public static VirtualCurrency getVirtualCurrencyByItemId(String itemId) throws VirtualItemNotFoundException {
        if (mVirtualCurrencies == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        for(VirtualCurrency c : mVirtualCurrencies){
            if (c.getItemId().equals(itemId)){
                return c;
            }
        }

        throw new VirtualItemNotFoundException("itemId", itemId);
    }

    /**
     * Use this function if you need to know the definition of a specific google MANAGED item.
     * @param productId is the requested MANAGED item's product id.
     * @return the definition of the MANAGED item requested.
     * @throws VirtualItemNotFoundException
     */
    public static GoogleMarketItem getGoogleManagedItemByProductId(String productId) throws VirtualItemNotFoundException {
        if (mGoogleManagedItems == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        for (GoogleMarketItem gmi : mGoogleManagedItems){
            if (gmi.getProductId().equals(productId)){
                return gmi;
            }
        }

        throw new VirtualItemNotFoundException("productId", productId);
    }

    /** Getters **/

    public static List<VirtualCurrency> getVirtualCurrencies(){
        if (mVirtualCurrencies == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        return mVirtualCurrencies;
    }

    public static List<VirtualCurrencyPack> getCurrencyPacks() {
        if (mVirtualCurrencyPacks == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        return mVirtualCurrencyPacks;
    }

    public static List<VirtualGood> getVirtualGoods() {
        if (mVirtualGoods == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        return mVirtualGoods;
    }

    /** Private functions **/

    private static void fromJSONObject(JSONObject jsonObject) throws JSONException{
        JSONArray virtualCategories = jsonObject.getJSONArray(JSONConsts.STORE_VIRTUALCATEGORIES);
        mVirtualCategories = new LinkedList<VirtualCategory>();
        for(int i=0; i<virtualCategories.length(); i++){
            JSONObject o = virtualCategories.getJSONObject(i);
            mVirtualCategories.add(new VirtualCategory(o));
        }

        JSONArray virtualCurrencies = jsonObject.getJSONArray(JSONConsts.STORE_VIRTUALCURRENCIES);
        mVirtualCurrencies = new LinkedList<VirtualCurrency>();
        for (int i=0; i<virtualCurrencies.length(); i++){
            JSONObject o = virtualCurrencies.getJSONObject(i);
            mVirtualCurrencies.add(new VirtualCurrency(o));
        }

        JSONArray currencyPacks = jsonObject.getJSONArray(JSONConsts.STORE_CURRENCYPACKS);
        mVirtualCurrencyPacks = new LinkedList<VirtualCurrencyPack>();
        for (int i=0; i<currencyPacks.length(); i++){
            JSONObject o = currencyPacks.getJSONObject(i);
            mVirtualCurrencyPacks.add(new VirtualCurrencyPack(o));
        }

        JSONArray virtualGoods = jsonObject.getJSONArray(JSONConsts.STORE_VIRTUALGOODS);
        mVirtualGoods = new LinkedList<VirtualGood>();
        for (int i=0; i<virtualGoods.length(); i++){
            JSONObject o = virtualGoods.getJSONObject(i);
            mVirtualGoods.add(new VirtualGood(o));
        }

        JSONArray googleManagedItems = jsonObject.getJSONArray(JSONConsts.STORE_GOOGLEMANAGED);
        mGoogleManagedItems = new LinkedList<GoogleMarketItem>();
        for (int i=0; i<googleManagedItems.length(); i++){
            JSONObject o = googleManagedItems.getJSONObject(i);
            mGoogleManagedItems.add(new GoogleMarketItem(o));
        }
    }

    /**
     * Converts StoreInfo to a JSONObject.
     * @return a JSONObject representation of the StoreInfo.
     */
    public static JSONObject toJSONObject(){
        if (mVirtualGoods == null) {
            if (!initializeFromDB()) {
                Log.e(TAG, "Can't initialize StoreInfo !");
                return null;
            }
        }

        JSONArray virtualCategories = new JSONArray();
        for (VirtualCategory cat : mVirtualCategories){
            virtualCategories.put(cat.toJSONObject());
        }

        JSONArray virtualCurrencies = new JSONArray();
        for(VirtualCurrency c : mVirtualCurrencies){
            virtualCurrencies.put(c.toJSONObject());
        }

        JSONArray currencyPacks = new JSONArray();
        for(VirtualCurrencyPack pack : mVirtualCurrencyPacks){
            currencyPacks.put(pack.toJSONObject());
        }

        JSONArray virtualGoods = new JSONArray();
        for(VirtualGood good : mVirtualGoods){
            virtualGoods.put(good.toJSONObject());
        }

        JSONArray googleManagedItems = new JSONArray();
        for(GoogleMarketItem gmi : mGoogleManagedItems){
            googleManagedItems.put(gmi.toJSONObject());
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONConsts.STORE_VIRTUALCATEGORIES, virtualCategories);
            jsonObject.put(JSONConsts.STORE_VIRTUALCURRENCIES, virtualCurrencies);
            jsonObject.put(JSONConsts.STORE_VIRTUALGOODS, virtualGoods);
            jsonObject.put(JSONConsts.STORE_CURRENCYPACKS, currencyPacks);
            jsonObject.put(JSONConsts.STORE_GOOGLEMANAGED, googleManagedItems);
        } catch (JSONException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "An error occurred while generating JSON object.");
            }
        }

        return jsonObject;
    }

    /** Private members **/

    private static final String TAG = "SOOMLA StoreInfo";

    private static List<VirtualCurrency>                   mVirtualCurrencies;
    private static List<VirtualCurrencyPack>               mVirtualCurrencyPacks;
    private static List<VirtualGood>                       mVirtualGoods;
    private static List<VirtualCategory>                   mVirtualCategories;
    private static List<GoogleMarketItem>                  mGoogleManagedItems;
}
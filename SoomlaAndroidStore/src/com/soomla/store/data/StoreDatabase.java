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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.soomla.store.SoomlaApp;
import com.soomla.store.StoreConfig;

/**
 * The StoreDatabase provides basic SQLite database io functions for specific needs around the SDK.
 */
public class StoreDatabase {

    public StoreDatabase(Context context) {

        if (StoreConfig.DB_DELETE){
            context.deleteDatabase(DATABASE_NAME);
        }

        mDatabaseHelper = new DatabaseHelper(context);
        mStoreDB = mDatabaseHelper.getWritableDatabase();

        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext(), SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        int mt_ver = prefs.getInt("MT_VER", 0);
        int sa_ver_old = prefs.getInt("SA_VER_OLD", 0);
        int sa_ver_new = prefs.getInt("SA_VER_NEW", 0);
        if (mt_ver < StoreConfig.METADATA_VERSION && sa_ver_old < sa_ver_new ) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("MT_VER", StoreConfig.METADATA_VERSION);
            edit.putInt("SA_VER_OLD", sa_ver_new);
            edit.commit();

            mStoreDB.execSQL("drop table IF EXISTS " + METADATA_TABLE_NAME);
            createDatabaseTables(mStoreDB);
        }
    }

    /**
     * Closes the database.
     */
    public void close() {
        mDatabaseHelper.close();
    }

    /**
     * Updates the balance of the virtual currency with the given itemId.
     * @param itemId is the item id of the required virtual currency.
     * @param balance is the required virtual currency's new balance.
     */
    public synchronized void updateVirtualCurrencyBalance(String itemId, String balance){
        ContentValues values = new ContentValues();
        values.put(VIRTUAL_CURRENCY_COLUMN_BALANCE, balance);

        int affected = mStoreDB.update(VIRTUAL_CURRENCY_TABLE_NAME, values, VIRTUAL_CURRENCY_COLUMN_ITEM_ID + "=?",
                new String[]{ itemId });
        if (affected == 0){
            values.put(VIRTUAL_CURRENCY_COLUMN_ITEM_ID, itemId);
            mStoreDB.replace(VIRTUAL_CURRENCY_TABLE_NAME, null, values);
        }
    }

    /**
     * Fetch all virtual currencies information from the database.
     * @return a {@link Cursor} that represents the query response.
     */
    public synchronized Cursor getVirtualCurrencies(){
        return mStoreDB.query(VIRTUAL_CURRENCY_TABLE_NAME, VIRTUAL_CURRENCY_COLUMNS,
                null, null, null, null, null);
    }

    /**
     * Fetch a single virtual currency information with the given itemId.
     * @param itemId is the required currency's item id.
     * @return a {@link Cursor} that represents the query response.
     */
    public synchronized Cursor getVirtualCurrency(String itemId){
        return mStoreDB.query(VIRTUAL_CURRENCY_TABLE_NAME, VIRTUAL_CURRENCY_COLUMNS,
                VIRTUAL_CURRENCY_COLUMN_ITEM_ID +  " = '" + itemId + "'", null, null, null, null);
    }

    /**
     * Updates the balance of the virtual good with the given itemId.
     * @param itemId is the item id of the required virtual good.
     * @param balance is the required virtual good's new balance.
     */
    public synchronized void updateVirtualGoodBalance(String itemId, String balance){
        ContentValues values = new ContentValues();
        values.put(VIRTUAL_GOODS_COLUMN_BALANCE, balance);

        int affected = mStoreDB.update(VIRTUAL_GOODS_TABLE_NAME, values, VIRTUAL_CURRENCY_COLUMN_ITEM_ID + "=?",
                new String[]{ itemId });
        if (affected == 0){
            values.put(VIRTUAL_GOODS_COLUMN_ITEM_ID, itemId);
            mStoreDB.replace(VIRTUAL_GOODS_TABLE_NAME, null, values);
        }
    }

    public synchronized void updateVirtualGoodEquip(String itemId, boolean equipped){
        ContentValues values = new ContentValues();
        values.put(VIRTUAL_GOODS_COLUMN_EQUIPPED, equipped);

        int affected = mStoreDB.update(VIRTUAL_GOODS_TABLE_NAME, values, VIRTUAL_CURRENCY_COLUMN_ITEM_ID + "=?",
                new String[]{ itemId });
        if (affected == 0){
            values.put(VIRTUAL_GOODS_COLUMN_ITEM_ID, itemId);
            mStoreDB.replace(VIRTUAL_GOODS_TABLE_NAME, null, values);
        }
    }

    /**
     * Fetch all virtual goods information from database.
     * @return a {@link Cursor} that represents the query response.
     */
    public synchronized Cursor getVirtualGoods(){
        return mStoreDB.query(VIRTUAL_GOODS_TABLE_NAME, VIRTUAL_GOODS_COLUMNS,
                null, null, null, null, null);
    }

    /**
     * Fetch a single virtual good information with the given itemId.
     * @param itemId is the required good's item id.
     * @return a {@link Cursor} that represents the query response.
     */
    public synchronized Cursor getVirtualGood(String itemId){
        return mStoreDB.query(VIRTUAL_GOODS_TABLE_NAME, VIRTUAL_GOODS_COLUMNS,
                VIRTUAL_GOODS_COLUMN_ITEM_ID + "='" + itemId + "'", null, null, null, null);
    }

    /**
     * Overwrites the current storeinfo information with a new one.
     * @param storeinfo is the new store information.
     */
    public synchronized void setStoreInfo(String storeinfo){
        ContentValues values = new ContentValues();
        values.put(METADATA_COLUMN_STOREINFO, storeinfo);

        int affected = mStoreDB.update(METADATA_TABLE_NAME, values, METADATA_COLUMN_PACKAGE + "='INFO'", null);
        if (affected == 0){
            values.put(METADATA_COLUMN_PACKAGE, "INFO");
            mStoreDB.replace(METADATA_TABLE_NAME, null, values);
        }
    }

    /**
     * Overwrites the current storefrontinfo information with a new one.
     * @param storefrontinfo is the new storefront information.
     */
    public synchronized void setStorefrontInfo(String storefrontinfo){
        ContentValues values = new ContentValues();
        values.put(METADATA_COLUMN_STOREFRONTINFO, storefrontinfo);

        int affected = mStoreDB.update(METADATA_TABLE_NAME, values, METADATA_COLUMN_PACKAGE + "='INFO'", null);
        if (affected == 0){
            values.put(METADATA_COLUMN_PACKAGE, "INFO");
            mStoreDB.replace(METADATA_TABLE_NAME, null, values);
        }
    }

    /**
     * Sets the status of the Google MANAGED item with the given purchased boolean.
     * @param productId is the Google MANAGED item.
     * @param purchased is the status of the Google MANAGED item.
     */
    public synchronized void setGoogleManagedItem(String productId, boolean purchased){
        ContentValues values = new ContentValues();

        if (purchased){
            values.put(GOOGLE_MANAGED_ITEMS_COLUMN_PRODUCT_ID, productId);
            mStoreDB.execSQL("INSERT OR IGNORE INTO " + GOOGLE_MANAGED_ITEMS_TABLE_NAME + " (" +
                    GOOGLE_MANAGED_ITEMS_COLUMN_PRODUCT_ID + ") VALUES ('" + productId + "')");
        }
        else {
            mStoreDB.delete(GOOGLE_MANAGED_ITEMS_TABLE_NAME, GOOGLE_MANAGED_ITEMS_COLUMN_PRODUCT_ID + "=?", new String[] { productId });
        }
    }

    /**
     * Fetch a single GoogleManagedItem information with the given productId.
     * @param productId is the required item's product id.
     * @return a {@link Cursor} that represents the query response.
     */
    public synchronized Cursor getGoogleManagedItem(String productId){
        return mStoreDB.query(GOOGLE_MANAGED_ITEMS_TABLE_NAME, GOOGLE_MANAGED_ITEMS_COLUMNS,
                GOOGLE_MANAGED_ITEMS_COLUMN_PRODUCT_ID + "='" + productId + "'", null, null, null, null);
    }

    /**
     * Fetch the meta data information.
     * @return the meta-data information.
     */
    public synchronized Cursor getMetaData(){
        return mStoreDB.query(METADATA_TABLE_NAME, METADATA_COLUMNS,
                null, null, null, null, null);
    }

    /**
     * Sets the given value to the given key
     * @param key the key of the key-val pair.
     * @param val the val of the key-val pair.
     */
    public synchronized void setKeyValVal(String key, String val) {
        ContentValues values = new ContentValues();
        values.put(KEYVAL_COLUMN_VAL, val);

        int affected = mStoreDB.update(KEYVAL_TABLE_NAME, values, KEYVAL_COLUMN_KEY + "='" + key + "'", null);
        if (affected == 0){
            values.put(KEYVAL_COLUMN_KEY, key);
            mStoreDB.replace(KEYVAL_TABLE_NAME, null, values);
        }
    }

    /**
     * Gets the value for the given key.
     * @param key the key of the key-val pair.
     * @return a value for the given key.
     */
    public synchronized Cursor getKeyValVal(String key) {
        return mStoreDB.query(KEYVAL_TABLE_NAME, KEYVAL_COLUMNS, KEYVAL_COLUMN_KEY + "='" + key + "'",
                null, null, null, null);
    }

    private void createDatabaseTables(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + KEYVAL_TABLE_NAME + "(" +
                KEYVAL_COLUMN_KEY + " TEXT PRIMARY KEY, " +
                KEYVAL_COLUMN_VAL + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + GOOGLE_MANAGED_ITEMS_TABLE_NAME + "(" +
                GOOGLE_MANAGED_ITEMS_COLUMN_PRODUCT_ID + " TEXT PRIMARY KEY)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + VIRTUAL_CURRENCY_TABLE_NAME + "(" +
                VIRTUAL_CURRENCY_COLUMN_ITEM_ID + " TEXT PRIMARY KEY, " +
                VIRTUAL_CURRENCY_COLUMN_BALANCE + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + VIRTUAL_GOODS_TABLE_NAME + "(" +
                VIRTUAL_GOODS_COLUMN_ITEM_ID + " TEXT PRIMARY KEY, " +
                VIRTUAL_GOODS_COLUMN_BALANCE + " TEXT, " +
                VIRTUAL_GOODS_COLUMN_EQUIPPED + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + METADATA_TABLE_NAME + "(" +
                METADATA_COLUMN_PACKAGE + " TEXT PRIMARY KEY, " +
                METADATA_COLUMN_STOREINFO + " TEXT, " +
                METADATA_COLUMN_STOREFRONTINFO + " TEXT)");
    }

    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            if (!sqLiteDatabase.isReadOnly()){
                sqLiteDatabase.execSQL("PRAGMA foreign_key=ON");
            }

            createDatabaseTables(sqLiteDatabase);
        }

        /**
         * On database upgrade we just want to delete the meta-data information. We must keep the balances.
         */
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("drop table IF EXISTS " + METADATA_TABLE_NAME);

            createDatabaseTables(sqLiteDatabase);
        }
    }

    // General key-value storage
    private static final String KEYVAL_TABLE_NAME = "kv_store";
    public static final String KEYVAL_COLUMN_KEY = "key";
    public static final String KEYVAL_COLUMN_VAL = "val";
    private static final String[] KEYVAL_COLUMNS = {
            KEYVAL_COLUMN_KEY, KEYVAL_COLUMN_VAL
    };

    // Managed items Table
    private static final String GOOGLE_MANAGED_ITEMS_TABLE_NAME = "managed_items";
    public static final String GOOGLE_MANAGED_ITEMS_COLUMN_PRODUCT_ID = "product_id";
    private static final String[] GOOGLE_MANAGED_ITEMS_COLUMNS = {
            GOOGLE_MANAGED_ITEMS_COLUMN_PRODUCT_ID
    };

    // Virtual Currency Table
    private static final String VIRTUAL_CURRENCY_TABLE_NAME     = "virtual_currency";
    public static final String VIRTUAL_CURRENCY_COLUMN_BALANCE  = "balance";
    public static final String VIRTUAL_CURRENCY_COLUMN_ITEM_ID  = "item_id";
    private static final String[] VIRTUAL_CURRENCY_COLUMNS = {
            VIRTUAL_CURRENCY_COLUMN_ITEM_ID, VIRTUAL_CURRENCY_COLUMN_BALANCE
    };

    // Virtual Goods Table
    private static final String VIRTUAL_GOODS_TABLE_NAME        = "virtual_goods";
    public static final String VIRTUAL_GOODS_COLUMN_BALANCE     = "balance";
    public static final String VIRTUAL_GOODS_COLUMN_ITEM_ID     = "item_id";
    public static final String VIRTUAL_GOODS_COLUMN_EQUIPPED    = "equipped";
    private static final String[] VIRTUAL_GOODS_COLUMNS = {
            VIRTUAL_GOODS_COLUMN_ITEM_ID, VIRTUAL_GOODS_COLUMN_BALANCE, VIRTUAL_GOODS_COLUMN_EQUIPPED
    };

    // Store Meta-Data Table
    private static final String METADATA_TABLE_NAME             = "metadata";
    public static final String METADATA_COLUMN_PACKAGE          = "package";
    public static final String METADATA_COLUMN_STOREINFO        = "store_info";
    public static final String METADATA_COLUMN_STOREFRONTINFO   = "storefront_info";
    private static final String[] METADATA_COLUMNS = {
            METADATA_COLUMN_PACKAGE, METADATA_COLUMN_STOREINFO, METADATA_COLUMN_STOREFRONTINFO
    };


    /** Private Members**/

    private static final String TAG = "StoreDatabase";
    private static final String DATABASE_NAME               = "store.db";
    private static final int    DATABASE_VERSION            = 1;

    private SQLiteDatabase mStoreDB;
    private DatabaseHelper mDatabaseHelper;
}

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

package com.soomla.store;

import com.soomla.store.domain.NonConsumableItem;
import com.soomla.store.domain.VirtualCategory;
import com.soomla.store.domain.virtualCurrencies.VirtualCurrency;
import com.soomla.store.domain.virtualCurrencies.VirtualCurrencyPack;
import com.soomla.store.domain.virtualGoods.VirtualGood;

/**
 * This interface represents a single game's metadata. Use this interface to create your assets
 * class that will be transferred to StoreInfo upon initialization.
 */
public interface IStoreAssets {

    /** Setters and Getters */

    /**
     * Retrieves the current version of your <code>IStoreAssets</code>.
     *
     * This value will determine if the saved data in the database will be deleted or not.
     * Bump the version every time you want to delete the old data in the DB.
     *
     * Real Game Example:
     *   Suppose that your game has a <code>VirtualGood</code> called "Hat".
     *   Let's say your game's <code>IStoreAssets</code> version is currently 0.
     *   Now you want to change the name "Hat" to "Green Hat" - you will need to bump the version
     *   from 0 to 1, in order for the new name, "Green Hat" to replace the old one, "Hat".
     *
     * Explanation: The local database on every one of your users' devices keeps your economy's
     * metadata, such as the <code>VirtualGood</code>'s name "Hat". When you change
     * <code>IStoreAssets</code>, you must bump the version in order for the data to change in
     * your users' local databases.
     *
     * You need to bump the version after ANY change in <code>IStoreAssets</code> for the local
     * database to realize it needs to refresh its data.
     *
     * @return the version of your specific <code>IStoreAssets</code>.
     */
    int getVersion();

    /**
     * Retrieves the array of your game's virtual currencies.
     */
    VirtualCurrency[] getCurrencies();

    /**
     * Retrieves the array of all virtual goods served by your store (all kinds in one array).
     */
    VirtualGood[] getGoods();

    /**
     * Retrieves the array of all virtual currency packs served by your store.
     */
    VirtualCurrencyPack[] getCurrencyPacks();

    /**
     * Retrieves the array of all virtual categories handled in your store.
     */
    VirtualCategory[] getCategories();

    /**
     * Retrieves the array of all non-consumable items served by your store.
     * You can define non-consumable items that you'd like to use for your needs.
     * CONSUMABLE (or UNMANAGED) items are usually just currency packs.
     * NON-CONSUMABLE (or MANAGED) items are usually used to let users purchase a "no-ads" token.
     *
     * NOTE: Make sure you set the type of the items you add here as Managed.MANAGED.
     *
     * @return an array of all non-consumables served in your game.
     */
    NonConsumableItem[] getNonConsumableItems();
}

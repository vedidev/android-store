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
package com.soomla.store;

import android.text.TextUtils;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.NonConsumableItem;
import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.domain.VirtualItem;
import com.soomla.store.domain.virtualGoods.EquippableVG;
import com.soomla.store.domain.virtualGoods.UpgradeVG;
import com.soomla.store.domain.virtualGoods.VirtualGood;
import com.soomla.store.exceptions.InsufficientFundsException;
import com.soomla.store.exceptions.NotEnoughGoodsException;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

import java.util.List;

/**
 * This class will help you do your day to day virtual economy operations easily.
 * You can give or take items from your users. You can buy items or upgrade them.
 * You can also check their equipping status and change it.
 */
public class StoreInventory {

    /**
     * Buys item with the given itemId
     *
     * @param itemId id of item to be purchased
     * @throws InsufficientFundsException
     * @throws VirtualItemNotFoundException
     */
    public static void buy(String itemId) throws InsufficientFundsException,
            VirtualItemNotFoundException {
        PurchasableVirtualItem pvi = (PurchasableVirtualItem) StoreInfo.getVirtualItem(itemId);
        pvi.buy();
    }

    /** VIRTUAL ITEMS **/

    /**
     * Returns the balance of the virtual item with the given itemId
     *
     * @param itemId id of the virtual item to be fetched - must be of VirtualCurrency or
     *               SingleUseVG or LifetimeVG or EquippableVG
     * @return balance
     * @throws VirtualItemNotFoundException
     */
    public static int getVirtualItemBalance(String itemId) throws VirtualItemNotFoundException {
        VirtualItem item = StoreInfo.getVirtualItem(itemId);
        return StorageManager.getVirtualItemStorage(item).getBalance(item);
    }

    /**
     * Gives your user the given amount of the virtual item with the given itemId.
     * For example, when your user plays your game for the first time you GIVE him 1000 gems.
     *
     * NOTE: This action is different than buy -
     * You use give(int amount) to give your user something for free.
     * You use buy() to give your user something and you get something in return.
     *
     * @param itemId id of the virtual item to be given
     * @param amount amount of the item to be given
     * @throws VirtualItemNotFoundException
     */
    public static void giveVirtualItem(String itemId, int amount)
            throws VirtualItemNotFoundException  {
        VirtualItem item = StoreInfo.getVirtualItem(itemId);
        item.give(amount);
    }

    /**
     * Takes from your user the given amount of the virtual item with the given itemId.
     *
     * @param itemId id of the virtual item to be taken
     * @param amount amount of the item to be given
     * @throws VirtualItemNotFoundException
     */
    public static void takeVirtualItem(String itemId, int amount)
            throws VirtualItemNotFoundException  {
        VirtualItem item = StoreInfo.getVirtualItem(itemId);
        item.take(amount);
    }

    /** VIRTUAL GOODS **/

    /**
     * Equips the virtual good with the given goodItemId.
     *
     * @param goodItemId id of the virtual good to be equipped
     * @throws VirtualItemNotFoundException
     * @throws ClassCastException
     * @throws NotEnoughGoodsException
     */
    public static void equipVirtualGood(String goodItemId) throws
            VirtualItemNotFoundException, ClassCastException, NotEnoughGoodsException{
        EquippableVG good = (EquippableVG) StoreInfo.getVirtualItem(goodItemId);

        try {
            good.equip();
        } catch (NotEnoughGoodsException e) {
            StoreUtils.LogError("StoreInventory", "UNEXPECTED! Couldn't equip something");
            throw e;
        }
    }

    /**
     * Unequips the virtual good with the given goodItemId.
     *
     * @param goodItemId id of the virtual good to be unequipped
     * @throws VirtualItemNotFoundException
     * @throws ClassCastException
     */
    public static void unEquipVirtualGood(String goodItemId) throws
            VirtualItemNotFoundException, ClassCastException{
        EquippableVG good = (EquippableVG) StoreInfo.getVirtualItem(goodItemId);

        good.unequip();
    }

    /**
     * Checks if the virtual good with the given goodItemId is equipped.
     *
     * @param goodItemId id of the virtual good who we want to know if is equipped
     * @return true if the virtual good is equipped, false otherwise
     * @throws VirtualItemNotFoundException
     * @throws ClassCastException
     */
    public static boolean isVirtualGoodEquipped(String goodItemId) throws
            VirtualItemNotFoundException, ClassCastException{
        EquippableVG good = (EquippableVG) StoreInfo.getVirtualItem(goodItemId);

        return StorageManager.getVirtualGoodsStorage().isEquipped(good);
    }

    /**
     * Retrieves upgrade level of the virtual good with the given goodItemId
     *
     * @param goodItemId id of the virtual good whose upgrade level we want to know
     * @return upgrade level
     * @throws VirtualItemNotFoundException
     */
    public static int getGoodUpgradeLevel(String goodItemId) throws VirtualItemNotFoundException {
        VirtualGood good = (VirtualGood) StoreInfo.getVirtualItem(goodItemId);
        UpgradeVG upgradeVG = StorageManager.getVirtualGoodsStorage().getCurrentUpgrade(good);
        if (upgradeVG == null) {
            return 0;
        }

        UpgradeVG first = StoreInfo.getGoodFirstUpgrade(goodItemId);
        int level = 1;
        while (!first.equals(upgradeVG)) {
            first = (UpgradeVG) StoreInfo.getVirtualItem(first.getNextItemId());
            level++;
        }

        return level;
    }

    /**
     * Retrieves upgrade id, if exists, of the virtual good with the given goodItemId
     *
     * @param goodItemId id of the virtual good whose upgrade id we want to know
     * @return upgrade id if exists, or "" otherwise
     * @throws VirtualItemNotFoundException
     */
    public static String getGoodCurrentUpgrade(String goodItemId)
            throws VirtualItemNotFoundException {
        VirtualGood good = (VirtualGood) StoreInfo.getVirtualItem(goodItemId);
        UpgradeVG upgradeVG = StorageManager.getVirtualGoodsStorage().getCurrentUpgrade(good);
        if (upgradeVG == null) {
            return "";
        }
        return upgradeVG.getItemId();
    }

    /**
     * Upgrades the virtual good with the given goodItemId
     * (the 'buy' process is applied)
     *
     * @param goodItemId
     * @throws VirtualItemNotFoundException
     * @throws InsufficientFundsException
     */
    public static void upgradeVirtualGood(String goodItemId)
            throws VirtualItemNotFoundException, InsufficientFundsException {
        VirtualGood good = (VirtualGood) StoreInfo.getVirtualItem(goodItemId);
        UpgradeVG upgradeVG = StorageManager.getVirtualGoodsStorage().getCurrentUpgrade(good);
        if (upgradeVG != null) {
            String nextItemId = upgradeVG.getNextItemId();
            if (TextUtils.isEmpty(nextItemId)) {
                return;
            }
            UpgradeVG vgu = (UpgradeVG) StoreInfo.getVirtualItem(nextItemId);
            vgu.buy();
        } else {
            UpgradeVG first = StoreInfo.getGoodFirstUpgrade(goodItemId);
            if (first != null) {
                first.buy();
            }
        }
    }

    /**
     * Forces upgrade (if possible) to the virtual good with the given upgradeItemId
     *
     * @param upgradeItemId id of the virtual good who we want to force an upgrade upon
     * @throws VirtualItemNotFoundException
     */
    public static void forceUpgrade(String upgradeItemId) throws VirtualItemNotFoundException {
        try {
            UpgradeVG upgradeVG = (UpgradeVG) StoreInfo.getVirtualItem(upgradeItemId);
            upgradeVG.give(1);
        } catch (ClassCastException ex) {
            StoreUtils.LogError("SOOMLA StoreInventory",
                    "The given itemId was of a non UpgradeVG VirtualItem. Can't force it.");
        }
    }

    /**
     * Removes upgrade from the virtual good with the given upgradeItemId
     *
     * @param goodItemId id of the virtual good
     * @throws VirtualItemNotFoundException
     */
    public static void removeUpgrades(String goodItemId) throws VirtualItemNotFoundException {
        List<UpgradeVG> upgrades = StoreInfo.getGoodUpgrades(goodItemId);
        for (UpgradeVG upgrade : upgrades) {
            StorageManager.getVirtualGoodsStorage().remove(upgrade, 1, true);
        }
        VirtualGood good = (VirtualGood) StoreInfo.getVirtualItem(goodItemId);
        StorageManager.getVirtualGoodsStorage().removeUpgrades(good);
    }

    /** NON CONSUMABLES **/

    /**
     * Checks if non-consumable item exists according to given nonConsItemId
     *
     * @param nonConsItemId
     * @return true if non-consumable item with nonConsItemId exists, false otherwise
     * @throws VirtualItemNotFoundException
     * @throws ClassCastException
     */
    public static boolean nonConsumableItemExists(String nonConsItemId)
            throws VirtualItemNotFoundException, ClassCastException {
        NonConsumableItem nonConsumableItem =
                (NonConsumableItem) StoreInfo.getVirtualItem(nonConsItemId);

        return StorageManager.getNonConsumableItemsStorage().nonConsumableItemExists(
                nonConsumableItem);
    }

    /**
     * Adds the non-consumable item with the given nonConsItemId to the non-consumable items
     * storage.
     *
     * @param nonConsItemId
     * @throws VirtualItemNotFoundException
     * @throws ClassCastException
     */
    public static void addNonConsumableItem(String nonConsItemId)
            throws VirtualItemNotFoundException, ClassCastException {
        NonConsumableItem nonConsumableItem =
                (NonConsumableItem) StoreInfo.getVirtualItem(nonConsItemId);

        StorageManager.getNonConsumableItemsStorage().add(nonConsumableItem);
    }

    /**
     * Removes the non-consumable item with the given nonConsItemId from the non-consumable items
     * storage.
     *
     * @param nonConsItemId
     * @throws VirtualItemNotFoundException
     * @throws ClassCastException
     */
    public static void removeNonConsumableItem(String nonConsItemId)
            throws VirtualItemNotFoundException, ClassCastException {
        NonConsumableItem nonConsumableItem =
                (NonConsumableItem) StoreInfo.getVirtualItem(nonConsItemId);

        StorageManager.getNonConsumableItemsStorage().remove(nonConsumableItem);
    }
}

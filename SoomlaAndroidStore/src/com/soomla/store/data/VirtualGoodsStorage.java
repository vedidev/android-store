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

import com.soomla.store.BusProvider;
import com.soomla.store.StoreUtils;
import com.soomla.store.domain.VirtualItem;
import com.soomla.store.domain.virtualGoods.EquippableVG;
import com.soomla.store.domain.virtualGoods.UpgradeVG;
import com.soomla.store.domain.virtualGoods.VirtualGood;
import com.soomla.store.events.GoodBalanceChangedEvent;
import com.soomla.store.events.GoodEquippedEvent;
import com.soomla.store.events.GoodUnEquippedEvent;
import com.soomla.store.events.GoodUpgradeEvent;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

/**
 * This class provides basic storage operations on virtual goods.
 */
public class VirtualGoodsStorage extends VirtualItemStorage{

    /**
     * Constructor
     */
    public VirtualGoodsStorage() {
        mTag = "SOOMLA VirtualGoodsStorage";
    }

    /**
     * Removes any upgrade associated with the given <code>VirtualGood</code>.
     *
     * @param good the VirtualGood to remove upgrade from
     */
    public void removeUpgrades(VirtualGood good) {
        removeUpgrades(good, true);
    }

    /**
     * Removes any upgrade associated with the given VirtualGood.
     *
     * @param good the virtual good to remove the upgrade from
     * @param notify if true post event to bus
     */
    public void removeUpgrades(VirtualGood good, boolean notify) {
        StoreUtils.LogDebug(mTag, "Removing upgrade information from virtual good: " +
                good.getName());

        String itemId = good.getItemId();
        String key = KeyValDatabase.keyGoodUpgrade(itemId);

        StorageManager.getKeyValueStorage().deleteKeyValue(key);

        if (notify) {
            BusProvider.getInstance().post(new GoodUpgradeEvent(good, null));
        }
    }

    /**
     * Assigns a specific upgrade to the given virtual good.
     *
     * @param good the virtual good to upgrade.
     * @param upgradeVG the upgrade to assign.
     */
    public void assignCurrentUpgrade(VirtualGood good, UpgradeVG upgradeVG) {
        assignCurrentUpgrade(good, upgradeVG, true);
    }

    /**
     * Assigns a specific upgrade to the given virtual good.
     *
     * @param good the VirtualGood to upgrade.
     * @param upgradeVG the upgrade to assign.
     * @param notify if true post event to bus
     */
    public void assignCurrentUpgrade(VirtualGood good, UpgradeVG upgradeVG, boolean notify) {
        if (getCurrentUpgrade(good) != null && getCurrentUpgrade(good).getItemId().equals(
                upgradeVG.getItemId())) {
            return;
        }

        StoreUtils.LogDebug(mTag, "Assigning upgrade " + upgradeVG.getName() + " to virtual good: "
                + good.getName());

        String itemId = good.getItemId();
        String key = KeyValDatabase.keyGoodUpgrade(itemId);
        String upItemId = upgradeVG.getItemId();

        StorageManager.getKeyValueStorage().setValue(key, upItemId);

        if (notify) {
            BusProvider.getInstance().post(new GoodUpgradeEvent(good, upgradeVG));
        }
    }

    /**
     * Retrieves the current upgrade for the given virtual good.
     *
     * @param good the virtual good to retrieve upgrade for.
     * @return the current upgrade for the given virtual good.
     */
    public UpgradeVG getCurrentUpgrade(VirtualGood good) {
        StoreUtils.LogDebug(mTag, "Fetching upgrade to virtual good: " + good.getName());

        String itemId = good.getItemId();
        String key = KeyValDatabase.keyGoodUpgrade(itemId);

        String upItemId = StorageManager.getKeyValueStorage().getValue(key);

        if (upItemId == null) {
            StoreUtils.LogDebug(mTag, "You tried to fetch the current upgrade of " + good.getName()
                    + " but there's not upgrade to it.");
            return null;
        }

        try {
            return (UpgradeVG) StoreInfo.getVirtualItem(upItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(mTag,
                    "The current upgrade's itemId from the DB is not found in StoreInfo.");
        } catch (ClassCastException e) {
            StoreUtils.LogError(mTag,
                    "The current upgrade's itemId from the DB is not an UpgradeVG.");
        }

        return null;
    }

    /**
     * Checks if the given <code>EquippableVG</code> is currently equipped or not.
     *
     * @param good the <code>EquippableVG</code> to check the status for
     * @return true if the given good is equipped, false otherwise
     */
    public boolean isEquipped(EquippableVG good){
        StoreUtils.LogDebug(mTag, "checking if virtual good with itemId: " + good.getItemId() +
                " is equipped.");

        String itemId = good.getItemId();
        String key = KeyValDatabase.keyGoodEquipped(itemId);
        String val = StorageManager.getKeyValueStorage().getValue(key);

        return val != null;
    }

    /**
     * Equips the given <code>EquippableVG</code>.
     *
     * @param good the <code>EquippableVG</code> to equip
     */
    public void equip(EquippableVG good) {
        equip(good, true);
    }

    /**
     * Equips the given <code>EquippableVG</code>.
     *
     * @param good the EquippableVG to equip
     * @param notify if notify is true post event to bus
     */
    public void equip(EquippableVG good, boolean notify) {
        if (isEquipped(good)) {
            return;
        }
        equipPriv(good, true, notify);
    }

    /**
     * UnEquips the given <code>EquippableVG</code>.
     *
     * @param good the <code>EquippableVG</code> to unequip
     */
    public void unequip(EquippableVG good) {
        unequip(good, true);
    }

    /**
     * UnEquips the given <code>EquippableVG</code>.
     *
     * @param good the <code>EquippableVG</code> to unequip
     * @param notify if true post event to bus
     */
    public void unequip(EquippableVG good, boolean notify) {
        if (!isEquipped(good)) {
            return;
        }
        equipPriv(good, false, notify);
    }

    /**
     * see parent
     *
     * @param itemId id of the virtual item whose balance is to be retrieved
     * @return see parent
     */
    @Override
    protected String keyBalance(String itemId) {
        return KeyValDatabase.keyGoodBalance(itemId);
    }

    /**
     * see parent
     *
     * @param item virtual item whose balance has changed
     * @param balance the balance that has changed
     * @param amountAdded the amount added to the item's balance
     */
    @Override
    protected void postBalanceChangeEvent(VirtualItem item, int balance, int amountAdded) {
        BusProvider.getInstance().post(new GoodBalanceChangedEvent((VirtualGood) item,
                balance, amountAdded));
    }

    /**
     * Helper function for <code>equip</code> and <code>unequip</code> functions
     */
    private void equipPriv(EquippableVG good, boolean equip, boolean notify){
        StoreUtils.LogDebug(mTag, (!equip ? "unequipping " : "equipping ") + good.getName() + ".");

        String itemId = good.getItemId();
        String key = KeyValDatabase.keyGoodEquipped(itemId);

        if (equip) {
            StorageManager.getKeyValueStorage().setValue(key, "");
            if (notify) {
                BusProvider.getInstance().post(new GoodEquippedEvent(good));
            }
        } else {
            StorageManager.getKeyValueStorage().deleteKeyValue(key);
            if (notify) {
                BusProvider.getInstance().post(new GoodUnEquippedEvent(good));
            }
        }
    }

}

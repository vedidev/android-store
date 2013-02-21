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
import com.soomla.billing.util.AESObfuscator;
import com.soomla.store.BusProvider;
import com.soomla.store.StoreConfig;
import com.soomla.store.domain.data.VirtualGood;
import com.soomla.store.events.GoodBalanceChangedEvent;
import com.soomla.store.events.VirtualGoodEquippedEvent;
import com.soomla.store.events.VirtualGoodUnEquippedEvent;

/**
 * This class provide basic storage operations on VirtualGoods.
 */
public class VirtualGoodsStorage {

    /** Constructor
     *
     */
    public VirtualGoodsStorage() {
    }


    /** Public functions **/

    /**
     * Fetch the balance of the given virtual good.
     * @param virtualGood is the required virtual good.
     * @return the balance of the required virtual currency.
     */
    public int getBalance(VirtualGood virtualGood){
        if (StoreConfig.debug){
            Log.d(TAG, "trying to fetch balance for virtual good with itemId: " + virtualGood.getItemId());
        }
        String itemId = virtualGood.getItemId();
        String key = KeyValDatabase.keyGoodBalance(itemId);
        if (StorageManager.getObfuscator() != null){
            key = StorageManager.getObfuscator().obfuscateString(key);
        }
        String val = StorageManager.getDatabase().getKeyVal(key);

        int balance = 0;
        if (val != null) {
            if (StorageManager.getObfuscator() != null){
                try {
                    balance = StorageManager.getObfuscator().unobfuscateToInt(val);
                } catch (AESObfuscator.ValidationException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            else {
                balance = Integer.parseInt(val);
            }
        }

        if (StoreConfig.debug){
            Log.d(TAG, "the balance for " + virtualGood.getItemId() + " is " + balance);
        }
        return balance;
	}

    public int setBalance(VirtualGood virtualGood, int balance) {
        if (StoreConfig.debug){
            Log.d(TAG, "setting balance " + balance + " to " + virtualGood.getName() + ".");
        }

        String itemId = virtualGood.getItemId();

        String balanceStr = "" + balance;
        String key = KeyValDatabase.keyGoodBalance(itemId);
        if (StorageManager.getObfuscator() != null){
            balanceStr = StorageManager.getObfuscator().obfuscateString(balanceStr);
            key      = StorageManager.getObfuscator().obfuscateString(key);
        }
        StorageManager.getDatabase().setKeyVal(key, balanceStr);

        BusProvider.getInstance().post(new GoodBalanceChangedEvent(virtualGood, balance, 0));

        return balance;
    }

    /**
    * Adds the given amount of goods to the storage.
    * @param virtualGood is the required virtual good.
    * @param amount is the amount of goods to add.
    */
    public int add(VirtualGood virtualGood, int amount){
        if (StoreConfig.debug){
            Log.d(TAG, "adding " + amount + " " + virtualGood.getName() + ".");
        }

        String itemId = virtualGood.getItemId();
        int balance = getBalance(virtualGood);
        String balanceStr = "" + (balance + amount);
        String key = KeyValDatabase.keyGoodBalance(itemId);
        if (StorageManager.getObfuscator() != null){
            balanceStr = StorageManager.getObfuscator().obfuscateString(balanceStr);
            key      = StorageManager.getObfuscator().obfuscateString(key);
        }
        StorageManager.getDatabase().setKeyVal(key, balanceStr);

        BusProvider.getInstance().post(new GoodBalanceChangedEvent(virtualGood, balance+amount, amount));

        return balance + amount;
	}

    /**
     * Removes the given amount from the given virtual good's balance.
     * @param virtualGood is the virtual good to remove the given amount from.
     * @param amount is the amount to remove.
     */
    public int remove(VirtualGood virtualGood, int amount){
        if (StoreConfig.debug){
            Log.d(TAG, "removing " + amount + " " + virtualGood.getName() + ".");
        }

        String itemId = virtualGood.getItemId();
        int balance = getBalance(virtualGood) - amount;
        balance = balance > 0 ? balance : 0;
        String balanceStr = "" + balance;
        String key = KeyValDatabase.keyGoodBalance(itemId);
        if (StorageManager.getObfuscator() != null){
            balanceStr = StorageManager.getObfuscator().obfuscateString(balanceStr);
            key      = StorageManager.getObfuscator().obfuscateString(key);
        }
        StorageManager.getDatabase().setKeyVal(key, balanceStr);

        BusProvider.getInstance().post(new GoodBalanceChangedEvent(virtualGood, balance, -1*amount));

        return balance;
	}

    public boolean isEquipped(VirtualGood virtualGood){
        if (StoreConfig.debug){
            Log.d(TAG, "checking if virtual good with itemId: " + virtualGood.getItemId() + " is equipped.");
        }
        String itemId = virtualGood.getItemId();
        String key = KeyValDatabase.keyGoodEquipped(itemId);
        if (StorageManager.getObfuscator() != null){
            key = StorageManager.getObfuscator().obfuscateString(key);
        }
        String val = StorageManager.getDatabase().getKeyVal(key);

        return val != null;
    }

    public void equip(VirtualGood virtualGood, boolean equip){
        if (StoreConfig.debug){
            Log.d(TAG, (!equip ? "unequipping " : "equipping ") + virtualGood.getName() + ".");
        }

        String itemId = virtualGood.getItemId();
        String key = KeyValDatabase.keyGoodEquipped(itemId);
        if (StorageManager.getObfuscator() != null){
            key = StorageManager.getObfuscator().obfuscateString(key);
        }

        if (equip) {
            StorageManager.getDatabase().setKeyVal(key, "");
            BusProvider.getInstance().post(new VirtualGoodEquippedEvent(virtualGood));
        } else {
            StorageManager.getDatabase().deleteKeyVal(key);
            BusProvider.getInstance().post(new VirtualGoodUnEquippedEvent(virtualGood));
        }
    }

    /** Private members **/
    private static final String TAG = "SOOMLA VirtualGoodsStorage";
}

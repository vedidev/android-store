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

import com.soomla.store.BusProvider;
import com.soomla.store.StoreUtils;
import com.soomla.store.domain.VirtualItem;
import com.soomla.store.domain.virtualGoods.EquippableVG;
import com.soomla.store.domain.virtualGoods.VirtualGood;
import com.soomla.store.events.GoodBalanceChangedEvent;
import com.soomla.store.events.GoodEquippedEvent;
import com.soomla.store.events.GoodUnEquippedEvent;

/**
 * This class provide basic storage operations on VirtualGoods.
 */
public class VirtualGoodsStorage extends VirtualItemStorage{

    /** Constructor
     *
     */
    public VirtualGoodsStorage() {
        mTag = "SOOMLA VirtualGoodsStorage";
    }


    /** Public functions **/

    public boolean isEquipped(VirtualGood good){
        StoreUtils.LogDebug(mTag, "checking if virtual good with itemId: " + good.getItemId() + " is equipped.");

        String itemId = good.getItemId();
        String key = KeyValDatabase.keyGoodEquipped(itemId);
        key = StorageManager.getAESObfuscator().obfuscateString(key);
        String val = StorageManager.getDatabase().getKeyVal(key);

        return val != null;
    }

    public void equip(EquippableVG good) {
        equipPriv(good, true);
    }

    public void unequip(EquippableVG good) {
        equipPriv(good, false);
    }

    private void equipPriv(EquippableVG good, boolean equip){
        StoreUtils.LogDebug(mTag, (!equip ? "unequipping " : "equipping ") + good.getName() + ".");

        String itemId = good.getItemId();
        String key = KeyValDatabase.keyGoodEquipped(itemId);
        key = StorageManager.getAESObfuscator().obfuscateString(key);

        if (equip) {
            StorageManager.getDatabase().setKeyVal(key, "");
            BusProvider.getInstance().post(new GoodEquippedEvent(good));
        } else {
            StorageManager.getDatabase().deleteKeyVal(key);
            BusProvider.getInstance().post(new GoodUnEquippedEvent(good));
        }
    }

    @Override
    protected String keyBalance(String itemId) {
        return KeyValDatabase.keyGoodBalance(itemId);
    }

    @Override
    protected void postBalanceChangeEvent(VirtualItem item, int balance, int amountAdded) {
        BusProvider.getInstance().post(new GoodBalanceChangedEvent((VirtualGood) item, balance, amountAdded));
    }

}

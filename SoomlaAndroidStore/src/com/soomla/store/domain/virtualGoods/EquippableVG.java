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

package com.soomla.store.domain.virtualGoods;


import com.soomla.store.StoreUtils;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.VirtualCategory;
import com.soomla.store.exceptions.NotEnoughGoodsException;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * An Equippable virtual good is a special type of Lifetime Virtual good that can be equipped
 * by your users. Equipping means that the user decides to currently use a specific virtual good.
 *
 * The EquippableVG's characteristics are:
 *  1. Can be purchased only once.
 *  2. Can be equipped by the user.
 *  3. Inherits the definition of LifetimeVG.
 *
 * There are 3 ways to equip an EquippableVG:
 *  1. LOCAL    - The current EquippableVG's equipping status doesn't affect any other EquippableVG.
 *  2. CATEGORY - In the containing category, if this EquippableVG is equipped, all other
 *                EquippableVGs must stay unequipped.
 *  3. GLOBAL   - In the whole game, if this EquippableVG is equipped, all other EquippableVG's
 *                must stay unequipped.
 *
 * Real Game Examples:
 *  1. LOCAL: Say your game offers 3 weapons: a sword, a gun, and an axe (LifetimeVGs). Let’s
 *  suppose your user has already bought all 3. These are euippables that do not affect one another
 *  - your user can “carry” the sword, gun, and axe at the same time if he chooses to!
 *
 *  2. CATEGORY: Suppose your game offers “shirts” and “hats”. Let’s say there are 4 available
 *  shirts and 2 available hats, and your user has already purchased all of the clothing items
 *  available. He can equip a shirt and a hat at the same time, but cannot equip more than 1 shirt
 *  or more than 1 hat at the same time. In other words, he can equip at most one of each clothing
 *  category (shirts, hats)!
 *
 *  3. GLOBAL: Suppose your game offers multiple characters (LifetimeVGs): RobotX and RobotY.
 *  Let’s say your user has bought both. In other words he owns both characters and will own them
 *  forever (because they are LifetimeVGs) . Your user can only play as (i.e. Equip) one character
 *  at a time, either RobotX or RobotY, but never both at the same time!
 *
 * NOTE: In case you want this item to be available for purchase in the market (PurchaseWithMarket),
 * you will need to define it in the market (Google Play, Amazon App Store, etc...).
 *
 * Inheritance: EquippableVG > LifeTimeVG > VirtualGood > PurchasableVirtualItem > VirtualItem
 */
public class EquippableVG extends LifetimeVG{

    /** Constructor
     *
     * @param equippingModel the way this EquippableVG is equipped - local, category, or global
     * @param mName see parent
     * @param mDescription see parent
     * @param mItemId see parent
     * @param purchaseType see parent
     */
    public EquippableVG(EquippingModel equippingModel,
                        String mName, String mDescription,
                        String mItemId,
                        PurchaseType purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);

        mEquippingModel = equippingModel;
    }

    /**
     * Constructor
     *
     * @param jsonObject see parent
     * @throws JSONException
     */
    public EquippableVG(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        String equipping = jsonObject.getString(JSONConsts.EQUIPPABLE_EQUIPPING);
        if (equipping.equals(EquippingModel.LOCAL.toString())) {
            mEquippingModel = EquippingModel.LOCAL;
        } else if (equipping.equals(EquippingModel.CATEGORY.toString())) {
            mEquippingModel = EquippingModel.CATEGORY;
        } else if (equipping.equals(EquippingModel.GLOBAL.toString())) {
            mEquippingModel = EquippingModel.GLOBAL;
        }
    }

    /**
     * see parent
     *
     * @return see parent
     */
    @Override
    public JSONObject toJSONObject() {
        JSONObject parentJsonObject = super.toJSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            Iterator<?> keys = parentJsonObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, parentJsonObject.get(key));
            }

            jsonObject.put(JSONConsts.EQUIPPABLE_EQUIPPING, mEquippingModel.toString());
        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }

    /**
     * Equips the current EquippableVG
     *
     * @throws NotEnoughGoodsException
     */
    public void equip() throws NotEnoughGoodsException {
        equip(true);
    }

    /**
     * Equips the current EquippableVG.
     * The equipping is done according to the equipping model ('GLOBAL', 'CATEGORY', or 'LOCAL').
     *
     * @param notify if notify is true post event to bus
     * @throws NotEnoughGoodsException
     */
    public void equip(boolean notify) throws NotEnoughGoodsException {
        // only if the user has bought this EquippableVG, the EquippableVG is equipped.
        if (StorageManager.getVirtualGoodsStorage().getBalance(this) > 0){

            if (mEquippingModel == EquippingModel.CATEGORY) {
                VirtualCategory category = null;
                try {
                    category = StoreInfo.getCategory(getItemId());
                } catch (VirtualItemNotFoundException e) {
                    StoreUtils.LogError(TAG,
                            "Tried to unequip all other category VirtualGoods but there was no " +
                            "associated category. virtual good itemId: " + getItemId());
                    return;
                }

                for(String goodItemId : category.getGoodsItemIds()) {
                    EquippableVG equippableVG = null;
                    try {
                        equippableVG = (EquippableVG) StoreInfo.getVirtualItem(goodItemId);

                        if (equippableVG != null && equippableVG != this) {
                            equippableVG.unequip(notify);
                        }
                    } catch (VirtualItemNotFoundException e) {
                        StoreUtils.LogError(TAG, "On equip, couldn't find one of the itemIds "
                                + "in the category. Continuing to the next one. itemId: "
                                + goodItemId);
                    } catch (ClassCastException ex) {
                        StoreUtils.LogDebug(TAG, "On equip, an error occurred. It's a debug "
                                + "message b/c the VirtualGood may just not be an EquippableVG. "
                                + "itemId: " + goodItemId);
                    }
                }
            } else if (mEquippingModel == EquippingModel.GLOBAL) {
                for(VirtualGood good : StoreInfo.getGoods()) {
                    if (good != this &&
                        good instanceof EquippableVG) {
                        ((EquippableVG)good).unequip(notify);
                    }
                }
            }

            StorageManager.getVirtualGoodsStorage().equip(this, notify);
        }
        else {
            throw new NotEnoughGoodsException(getItemId());
        }
    }

    /**
     * Unequips the current EquippableVG
     */
    public void unequip() {
        unequip(true);
    }

    /**
     * Unequips the current EquippableVG
     *
     * @param notify
     */
    public void unequip(boolean notify) {
        StorageManager.getVirtualGoodsStorage().unequip(this, notify);
    }

    /**
     * EquippingModel is the way EquippableVG is equipped.
     * LOCAL    - The current EquippableVG's equipping status doesn't affect any other EquippableVG.
     * CATEGORY - In the containing category, if this EquippableVG is equipped, all other
     *            EquippableVGs are unequipped.
     * GLOBAL   - In the whole game, if this EquippableVG is equipped, all other EquippableVGs
     *            are unequipped.
     */
    public static enum EquippingModel {
        LOCAL("local"), CATEGORY("category"), GLOBAL("global");

        private EquippingModel(final String em) {
            this.mEm = em;
        }

        private final String mEm;

        public String toString() {
            return mEm;
        }

        public static EquippingModel fromString(String em) {
            for (final EquippingModel element : EnumSet.allOf(EquippingModel.class)) {
                if (element.toString().equals(em)) {
                    return element;
                }
            }
            return null;
        }
    }


    /** Setters and Getters **/

    public EquippingModel getEquippingModel() {
        return mEquippingModel;
    }


    /** Private Members **/

    private static final String TAG = "SOOMLA EquippableVG"; //used for Log messages

    //the way this EquippableVG is equipped - local, category, or global
    private EquippingModel mEquippingModel;
}

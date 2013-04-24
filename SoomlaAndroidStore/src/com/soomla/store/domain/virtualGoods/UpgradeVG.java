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

import android.text.TextUtils;
import com.soomla.store.StoreUtils;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * An upgrade virtual good is one VG in a series of VGs that define an upgrade scale of an associated VirtualGood.
 *
 * This type of virtual good is best explained with an example:
 * Lets say you have a strength attribute to your character in the game and that strength is on the scale 1-5.
 * You want to provide your users with the ability to upgrade that strength. This is what you'll need to create:
 *  1. SingleUseVG for 'strength'
 *  2. UpgradeVG for strength 'level 1'
 *  3. UpgradeVG for strength 'level 2'
 *  4. UpgradeVG for strength 'level 3'
 *  5. UpgradeVG for strength 'level 4'
 *  6. UpgradeVG for strength 'level 5'
 *
 * Now, when the user buys this UpgradeVG, we check and make sure the appropriate conditions are met and buy it for you
 * (which actually means we upgrading the associated VirtualGood).
 *
 * This VirtualItem is purchasable.
 * In case you purchase this item in Google Play (PurchaseWithMarket), You need to define the google item in Google
 * Play Developer Console. (https://play.google.com/apps/publish)
 */
public class UpgradeVG extends VirtualGood {

    /** Constructor
     *
     * @param good is the VirtualGood associated with this upgrade.
     * @param level is the level of the current UpgradeVG.
     * @param prev is the UpgradeVG before or if this is the first UpgradeVG in the scale then the value is null.
     * @param mName see parent
     * @param mDescription see parent
     * @param mItemId see parent
     * @param purchaseType see parent
     */
    public UpgradeVG(VirtualGood good, int level,
                     UpgradeVG prev,
                     String mName, String mDescription,
                     String mItemId,
                     PurchaseType purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);

        mGood = good;
        mLevel = level;
        mPrev = prev;
    }

    /** Constructor
     * see parent
     */
    public UpgradeVG(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        String goodItemId = jsonObject.getString(JSONConsts.VGU_GOOD_ITEMID);
        String prevItemId = jsonObject.getString(JSONConsts.VGU_PREV_ITEMID);
        mLevel = jsonObject.getInt(JSONConsts.VGU_LEVEL);

        try {
            mGood = (VirtualGood) StoreInfo.getVirtualItem(goodItemId);
            mPrev = TextUtils.isEmpty(prevItemId) ? null : (UpgradeVG) StoreInfo.getVirtualItem(prevItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "The wanted virtual good was not found.");
        }
    }

    /**
     * see parent
     */
    @Override
    public JSONObject toJSONObject(){
        JSONObject parentJsonObject = super.toJSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            Iterator<?> keys = parentJsonObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, parentJsonObject.get(key));
            }

            jsonObject.put(JSONConsts.VGU_GOOD_ITEMID, mGood.getItemId());

            jsonObject.put(JSONConsts.VGU_PREV_ITEMID, mPrev == null ? "" : mPrev.getItemId());
            jsonObject.put(JSONConsts.VGU_LEVEL, mLevel);
        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }

    /** Getters **/

    public VirtualGood getGood() {
        return mGood;
    }

    public int getLevel() {
        return mLevel;
    }

    public UpgradeVG getPrev() {
        return mPrev;
    }

    /**
     * Assigning the current upgrade to the associated VirtualGood (mGood).
     *
     * This action doesn't check nothing!! It just assigns the current UpgradeVG to the associated mGood.
     * @param amount is NOT USED HERE !
     */
    @Override
    public void give(int amount) {
        StoreUtils.LogDebug(TAG, "Assigning " + getName() + " to: " + mGood.getName());
        StorageManager.getVirtualGoodsStorage().assignCurrentUpgrade(mGood, this);
    }

    /**
     * This is actually a downgrade of the associated VirtualGood (mGood).
     * We check if the current Upgrade is really associated with the VirtualGood and:
     *  if YES we downgrade to the previous upgrade (or removing upgrades in case of null).
     *  if NO we return (do nothing).
     *
     * @param amount is NOT USED HERE !
     */
    @Override
    public void take(int amount) {
        UpgradeVG upgradeVG = StorageManager.getVirtualGoodsStorage().getCurrentUpgrade(mGood);
        if (upgradeVG != this) {
            StoreUtils.LogError(TAG, "You can't take what's not yours. The UpgradeVG " + getName() + " is not assigned to " +
                    "the VirtualGood: " + mGood.getName());
            return;
        }

        if (mPrev != null) {
            StoreUtils.LogDebug(TAG, "Downgrading " + mGood.getName() + " to: " + mPrev.getName());
            StorageManager.getVirtualGoodsStorage().assignCurrentUpgrade(mGood, mPrev);
        } else {
            StoreUtils.LogDebug(TAG, "Downgrading " + mGood.getName() + " to NO-UPGRADE");
            StorageManager.getVirtualGoodsStorage().removeUpgrades(mGood);
        }
    }

    @Override
    protected boolean canBuy() {
        UpgradeVG upgradeVG = StorageManager.getVirtualGoodsStorage().getCurrentUpgrade(mGood);
        return (upgradeVG == null && mLevel == 1) ||
               (upgradeVG != null && ((upgradeVG.getLevel() == (mLevel-1)) || (upgradeVG.getLevel() == (mLevel+1))));
    }

    private static final String TAG = "SOOMLA UpgradeVG";

    private VirtualGood mGood;
    private int         mLevel;
    private UpgradeVG   mPrev;
}

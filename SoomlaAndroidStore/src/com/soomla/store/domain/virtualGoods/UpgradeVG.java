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
import com.soomla.store.data.StoreInfo;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class UpgradeVG extends VirtualGood {

    public UpgradeVG(VirtualGood good, int level,
                     UpgradeVG next,
                     UpgradeVG prev,
                     String mName, String mDescription,
                     String mItemId,
                     PurchaseType purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);

        mGood = good;
        mLevel = level;
        mNext = next;
        mPrev = prev;
    }

    public UpgradeVG(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        String goodItemId = jsonObject.getString(JSONConsts.VGU_GOOD_ITEMID);
        String nextItemId = jsonObject.getString(JSONConsts.VGU_NEXT_ITEMID);
        String prevItemId = jsonObject.getString(JSONConsts.VGU_PREV_ITEMID);
        mLevel = jsonObject.getInt(JSONConsts.VGU_LEVEL);

        try {
            mGood = (VirtualGood) StoreInfo.getVirtualItem(goodItemId);
            mNext = (UpgradeVG) StoreInfo.getVirtualItem(nextItemId);
            mPrev = (UpgradeVG) StoreInfo.getVirtualItem(prevItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "The wanted virtual good was not found.");
        }
    }

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
            jsonObject.put(JSONConsts.VGU_NEXT_ITEMID, mNext.getItemId());
            jsonObject.put(JSONConsts.VGU_PREV_ITEMID, mPrev.getItemId());
            jsonObject.put(JSONConsts.VGU_LEVEL, mLevel);
        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }

    @Override
    public void give(int amount) {
        // TODO: we still need to check if "canBuy" before we give
    }

    @Override
    public void take(int amount) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected boolean canBuy() {
        // TODO: check the level of the virtual good associated with this.
        //          if the level is not one step lower then the answer is FALSE !
        return false;
    }

    private static final String TAG = "SOOMLA UpgradeVG";

    private VirtualGood mGood;
    private int         mLevel;
    private UpgradeVG   mNext;
    private UpgradeVG   mPrev;
}

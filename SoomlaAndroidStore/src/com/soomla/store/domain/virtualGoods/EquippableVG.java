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
import com.soomla.store.exceptions.NotEnoughGoodsException;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumSet;
import java.util.Iterator;

public class EquippableVG extends LifetimeVG{

    public EquippableVG(EquippingModel equippingModel,
                        String mName, String mDescription,
                        String mItemId,
                        PurchaseType purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);

        mEquippingModel = equippingModel;
    }

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

    public void equip() throws NotEnoughGoodsException {
        // only if the user has bought this EquippableVG, the EquippableVG is equipped.
        if (StorageManager.getVirtualGoodsStorage().getBalance(this) > 0){
            StorageManager.getVirtualGoodsStorage().equip(this);

            if (mEquippingModel == EquippingModel.CATEGORY) {

            } else if (mEquippingModel == EquippingModel.GLOBAL) {

            }
//            if (getCategory().getEquippingModel() == VirtualCategory.EquippingModel.SINGLE) {
//                for(VirtualGood g : StoreInfo.getVirtualGoods()) {
//                    if (g.getCategory().equals(good.getCategory()) && !g.equals(good)) {
//                        StorageManager.getVirtualGoodsStorage().equip(g, false);
//                    }
//                }
//            }
        }
        else {
            throw new NotEnoughGoodsException(getItemId());
        }
    }

    /**
     * EquippingModel is the way EquippableVG is equipped.
     * LOCAL    - The current EquippableVG's equipping status doesn't affect any other EquippableVG.
     * CATEGORY - In the containing category, if this EquippableVG is equipped, all other EquippableVGs are unequipped.
     * GLOBAL   - In the whole game, if this EquippableVG is equipped, all other EquippableVGs are unequipped.
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

    private static final String TAG = "SOOMLA EquippableVG";

    private EquippingModel mEquippingModel;
}

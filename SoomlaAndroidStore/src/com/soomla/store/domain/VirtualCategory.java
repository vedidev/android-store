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
package com.soomla.store.domain;

import android.util.Log;
import com.soomla.store.StoreConfig;
import com.soomla.store.data.JSONConsts;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumSet;

/**
 * This class is a definition of a category. A single category can be associated with many virtual goods.
 * The purposes of virtual category are:
 * 1. You can use it to arrange virtual goods to their specific categories.
 * 2. SOOMLA's storefront uses this to show the goods in their categories on the UI (for supported themes only).
 */
public class VirtualCategory {

    /** Constructor
     *
     * @param mName is the category's name.
     * @param mId is the category's unique id.
     * @param mEquippingModel is the equipping model for this category.
     */
    public VirtualCategory(String mName, int mId, EquippingModel mEquippingModel) {
        this.mName = mName;
        this.mId = mId;
        this.mEquippingModel = mEquippingModel;
    }

    /** Constructor
     *
     * Generates an instance of {@link VirtualCategory} from a JSONObject.
     * @param jsonObject is a JSONObject representation of the wanted {@link VirtualCategory}.
     * @throws JSONException
     */
    public VirtualCategory(JSONObject jsonObject) throws JSONException{
        this.mName = jsonObject.getString(JSONConsts.CATEGORY_NAME);
        this.mId   = jsonObject.getInt(JSONConsts.CATEGORY_ID);
        this.mEquippingModel = EquippingModel.fromString(jsonObject.getString(JSONConsts.CATEGORY_EQUIPPING));
    }

    /**
     * Converts the current {@link VirtualCategory} to a JSONObject.
     * @return a JSONObject representation of the current {@link VirtualCategory}.
     */
    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONConsts.CATEGORY_NAME, mName);
            jsonObject.put(JSONConsts.CATEGORY_ID, mId);
            jsonObject.put(JSONConsts.CATEGORY_EQUIPPING, mEquippingModel.toString());
        } catch (JSONException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "An error occurred while generating JSON object.");
            }
        }

        return jsonObject;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public EquippingModel getEquippingModel() {
        return mEquippingModel;
    }

    /**
     * EquippingModel is the way VirtualGoods are equipped inside the current category.
     * NONE - Can't equip virtual goods.
     * SINGLE - Only one virtual good can be equipped at any given time in the game.
     * MULTIPLE - Many virtual goods can be equipped at any given time in the game.
     */
    public static enum EquippingModel {
        NONE("none"), SINGLE("single"), MULTIPLE("multiple");

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualCategory that = (VirtualCategory) o;

        return mId == that.mId;

    }

    @Override
    public int hashCode() {
        return mId;
    }

    /** Private members **/

    private static final String TAG = "SOOMLA VirtualCategory";

    private String  mName;
    private int     mId;
    private EquippingModel mEquippingModel;
}

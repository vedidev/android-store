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

import android.util.Log;
import com.soomla.store.StoreConfig;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.domain.VirtualCategory;
import com.soomla.store.domain.purchaseStrategies.IPurchaseStrategy;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * This is a representation of the application's virtual good.
 */
public abstract class VirtualGood extends PurchasableVirtualItem {

    /** Constructor
     *
     * @param mName is the name of the virtual good.
     * @param mDescription is the description of the virtual good. This will show up
     *                       in the store in the description section.
     * @param mItemId is the id of the virtual good.
     * @param mCategory is the category this virtual good is associated with.
     */
    public VirtualGood(String mName, String mDescription,
                       String mItemId, VirtualCategory mCategory, IPurchaseStrategy purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);
        this.mCategory = mCategory;
    }

    /** Constructor
     *
     * Generates an instance of {@link VirtualGood} from a JSONObject.
     * @param jsonObject is a JSONObject representation of the wanted {@link VirtualGood}.
     * @throws JSONException
     */
    public VirtualGood(JSONObject jsonObject) throws JSONException{
        super(jsonObject);
        int catId = jsonObject.getInt(JSONConsts.GOOD_CATEGORY_ID);
        try {
            if (catId > -1){
                this.mCategory = StoreInfo.getVirtualCategoryById(catId);
            }
        } catch (VirtualItemNotFoundException e) {
            Log.e(TAG, "Can't find category with id: " + catId);
        }
    }

    /**
     * Converts the current {@link VirtualGood} to a JSONObject.
     * @return a JSONObject representation of the current {@link VirtualGood}.
     */
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
        } catch (JSONException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "An error occurred while generating JSON object.");
            }
        }

        return jsonObject;
    }

    public VirtualCategory getCategory() {
        return mCategory;
    }

    /** Private members **/

    private static final String TAG = "SOOMLA VirtualGood";

    private VirtualCategory    mCategory;
}

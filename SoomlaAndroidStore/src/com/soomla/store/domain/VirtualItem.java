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

/**
 * This class is the parent of all virtual items in the application.
 */
public abstract class VirtualItem {

    /** Constructor
     *
     * @param mName is the name of the virtual item.
     * @param mDescription is the description of the virtual item. If you use SOOMLA's storefront, This will show up
     *                       in the store in the description section.
     * @param mItemId is the itemId of the virtual item.
     */
    public VirtualItem(String mName, String mDescription, String mItemId) {
        this.mName = mName;
        this.mDescription = mDescription;
        this.mItemId = mItemId.trim();
    }

    /** Constructor
     *
     * Generates an instance of {@link VirtualItem} from a JSONObject.
     * @param jsonObject is a JSONObject representation of the wanted {@link VirtualItem}.
     * @throws JSONException
     */
    public VirtualItem(JSONObject jsonObject) throws JSONException{
        mName = jsonObject.getString(JSONConsts.ITEM_NAME);
        mDescription = jsonObject.getString(JSONConsts.ITEM_DESCRIPTION);
        mItemId = jsonObject.getString(JSONConsts.ITEM_ITEMID);
    }

    /**
     * Converts the current {@link VirtualItem} to a JSONObject.
     * @return a JSONObject representation of the current {@link VirtualItem}.
     */
    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONConsts.ITEM_NAME, mName);
            jsonObject.put(JSONConsts.ITEM_DESCRIPTION, mDescription);
            jsonObject.put(JSONConsts.ITEM_ITEMID, mItemId);
        } catch (JSONException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "An error occured while generating JSON object.");
            }
        }

        return jsonObject;
    }

    /**
     * By performing this action, you give your user a curtain amount of the specific VirtualItem.
     * The giving process is different from the "buy" process. You just give your user something and you get
     * nothing in return.
     * @param amount the amount of the specific item to be given.
     */
    public abstract void give(int amount);

    /**
     * By performing this action, you take curtain amount of the specific VirtualItem from your user.
     * @param amount the amount of the specific item to be taken.
     */
    public abstract void take(int amount);

    /**
     * ItemId is the unique id of every VirtualItem. We use it to compare VirtualItems.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VirtualItem)) return false;

        VirtualItem that = (VirtualItem) o;

        return mItemId.equals(that.mItemId);
    }

    @Override
    public int hashCode() {
        return mItemId != null ? mItemId.hashCode() : 0;
    }

    /** Getters **/

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getItemId(){
        return mItemId;
    }

    /** Private members **/

    private static final String TAG = "SOOMLA VirtualItem";

    private String mName;
    private String mDescription;
    private String mItemId;
}

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
package com.soomla.store.domain.data;

import android.util.Log;
import com.soomla.store.StoreConfig;
import com.soomla.store.data.JSONConsts;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is a definition of a category. A single category can be associated with many virtual items.
 * The purposes of virtual category are:
 * 1. You can use it to arrange virtual items to their specific categories.
 * 2. SOOMLA's storefront uses this to show the items in their categories on the UI (for supported themes only).
 */
public class VirtualCategory {

    /** Constructor
     *
     * @param mName is the category's name.
     * @param mId is the category's unique id.
     * @param mTitle is the category's title (presented in the UI)
     * @param mImgFilePath is the category's image (presented in the UI)
     */
    public VirtualCategory(String mName, int mId, String mTitle, String mImgFilePath) {
        this.mName = mName;
        this.mId = mId;
        this.mTitle = mTitle;
        this.mImgFilePath = mImgFilePath;
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
        this.mTitle = jsonObject.getString(JSONConsts.CATEGORY_TITLE);
        this.mImgFilePath = jsonObject.getString(JSONConsts.CATEGORY_IMAGEFILEPATH);
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
            jsonObject.put(JSONConsts.CATEGORY_TITLE, mTitle);
            jsonObject.put(JSONConsts.CATEGORY_IMAGEFILEPATH, mImgFilePath);
        } catch (JSONException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "An error occurred while generating JSON object.");
            }
        }

        return jsonObject;
    }

    public int getmId() {
        return mId;
    }

    /** Private members **/

    private static final String TAG = "SOOMLA VirtualCategory";

    private String  mName;
    private int     mId;
    private String  mTitle;
    private String  mImgFilePath;
}

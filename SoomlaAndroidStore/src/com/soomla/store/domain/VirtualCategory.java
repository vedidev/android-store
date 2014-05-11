/**
 * Copyright (C) 2012-2014 Soomla Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.soomla.store.domain;

import com.soomla.store.StoreUtils;
import com.soomla.store.data.JSONConsts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is a definition of a category. A single category can be associated with many virtual
 * goods. You can use it to arrange virtual goods to their specific categories.
 */
public class VirtualCategory {

    /**
     * Constructor
     *
     * @param name the category's name.
     * @param goodsItemIds the list of itemIds of the VirtualGoods in this category.
     */
    public VirtualCategory(String name, ArrayList<String> goodsItemIds) {
        mName = name;
        mGoodsItemIds = goodsItemIds;
    }

    /**
     * Constructor
     * Generates an instance of {@link VirtualCategory} from a <code>JSONObject</code>.
     *
     * @param jsonObject is a JSONObject representation of the wanted {@link VirtualCategory}.
     * @throws JSONException
     */
    public VirtualCategory(JSONObject jsonObject) throws JSONException{
        mName = jsonObject.getString(JSONConsts.CATEGORY_NAME);

        JSONArray goodsArr = jsonObject.getJSONArray(JSONConsts.CATEGORY_GOODSITEMIDS);
        for(int i=0; i<goodsArr.length(); i++) {
            String goodItemId = goodsArr.getString(i);
            mGoodsItemIds.add(goodItemId);
        }
    }

    /**
     * Converts the current {@link VirtualCategory} to a <code>JSONObject</code>.
     *
     * @return a JSONObject representation of the current {@link VirtualCategory}.
     */
    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONConsts.CATEGORY_NAME, mName);

            JSONArray goodsArr = new JSONArray();
            for(String goodItemId : mGoodsItemIds) {
                goodsArr.put(goodItemId);
            }

            jsonObject.put(JSONConsts.CATEGORY_GOODSITEMIDS, goodsArr);
        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }


    /** Setters and Getters **/

    public String getName() {
        return mName;
    }

    public ArrayList<String> getGoodsItemIds() {
        return mGoodsItemIds;
    }


    /** Private members **/

    private static final String TAG = "SOOMLA VirtualCategory"; //used for Log messages

    //the list of itemIds of the VirtualGoods in this category
    private ArrayList<String> mGoodsItemIds = new ArrayList<String>();

    private String  mName; //the category's name

}

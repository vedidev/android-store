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

import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

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
     */
    public VirtualGood(String mName, String mDescription,
                       String mItemId, PurchaseType purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);
    }

    /** Constructor
     *
     * Generates an instance of {@link VirtualGood} from a JSONObject.
     * @param jsonObject is a JSONObject representation of the wanted {@link VirtualGood}.
     * @throws JSONException
     */
    public VirtualGood(JSONObject jsonObject) throws JSONException{
        super(jsonObject);
    }

    /**
     * Converts the current {@link VirtualGood} to a JSONObject.
     * @return a JSONObject representation of the current {@link VirtualGood}.
     */
    @Override
    public JSONObject toJSONObject(){
        return super.toJSONObject();
    }

    /** Private members **/

    private static final String TAG = "SOOMLA VirtualGood";}

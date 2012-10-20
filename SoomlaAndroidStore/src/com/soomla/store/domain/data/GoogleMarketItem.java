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
 * This class represents an item in Google Play.
 * Every {@link VirtualCurrencyPack} has an instance of this class which is a
 * representation of the same currency pack as an item on Google Play.
 */
public class GoogleMarketItem {

    /** Constructor
     *
     * @param mProductId is the Id of the current item in Google Play.
     * @param mManaged is the Managed type of the current item in Google Play.
     */
    public GoogleMarketItem(String mProductId, Managed mManaged) {

        this.mProductId = mProductId;
        this.mManaged = mManaged;
    }

    /** Constructor
     *
     * Generates an instance of {@link GoogleMarketItem} from a JSONObject.
     * @param jsonObject is a JSONObject representation of the wanted {@link GoogleMarketItem}.
     * @throws JSONException
     */
    public GoogleMarketItem(JSONObject jsonObject) throws JSONException {
        this.mManaged = Managed.valueOf(jsonObject.getString(JSONConsts.GOOGLEMANAGED_MANAGED));
        this.mProductId = jsonObject.getString(JSONConsts.GOOGLEMANAGED_PRODUCT_ID);
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONConsts.GOOGLEMANAGED_MANAGED, mManaged.name());
            jsonObject.put(JSONConsts.GOOGLEMANAGED_PRODUCT_ID, mProductId);
        } catch (JSONException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "An error occured while generating JSON object.");
            }
        }

        return jsonObject;
    }

    /** Getters **/

    public String getProductId() {
        return mProductId;
    }

    public Managed getManaged() {
        return mManaged;
    }

    /** Private members **/

    /**
     * Each product in the catalog can be MANAGED, UNMANAGED, or SUBSCRIPTION.  MANAGED
     * means that the product can be purchased only once per user (such as a new
     * level in a game). The purchase is remembered by Android Market and
     * can be restored if this application is uninstalled and then
     * re-installed. UNMANAGED is used for products that can be used up and
     * purchased multiple times (such as poker chips). It is up to the
     * application to keep track of UNMANAGED products for the user.
     * SUBSCRIPTION is just like MANAGED except that the user gets charged monthly
     * or yearly.
     */
    public static enum Managed { MANAGED, UNMANAGED, SUBSCRIPTION }

    private Managed mManaged;
    /**
     *  The Id of this VirtualGood in Google Market
    */

    private static final String TAG = "SOOMLA GoogleMarketItem";

    private String mProductId;
}

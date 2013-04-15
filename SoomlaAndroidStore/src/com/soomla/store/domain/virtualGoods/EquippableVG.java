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


import com.soomla.store.domain.VirtualCategory;
import com.soomla.store.purchaseStrategies.IPurchaseStrategy;
import org.json.JSONException;
import org.json.JSONObject;

public class EquippableVG extends LifetimeVG{

    public EquippableVG(String mName, String mDescription, String mItemId, VirtualCategory mCategory, IPurchaseStrategy purchaseType) {
        super(mName, mDescription, mItemId, mCategory, purchaseType);
    }

    public EquippableVG(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }
}

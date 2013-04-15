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

import com.soomla.store.data.StorageManager;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

public class SingleUseVG extends VirtualGood{

    public SingleUseVG(String mName, String mDescription, String mItemId, PurchaseType purchaseType) {
        super(mName, mDescription, mItemId, purchaseType);
    }

    public SingleUseVG(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    public JSONObject toJSONObject() {
        return super.toJSONObject();
    }

    @Override
    public void give(int amount) {
        StorageManager.getVirtualGoodsStorage().add(this, amount);
    }

    @Override
    public void take(int amount) {
        StorageManager.getVirtualGoodsStorage().remove(this, amount);
    }

    @Override
    protected boolean canBuy() {
        return true;
    }
}

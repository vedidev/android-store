package com.soomla.store.domain.virtualGoods;

import com.soomla.store.StoreUtils;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SingleUsePackVG extends VirtualGood {

    public SingleUsePackVG(SingleUseVG good, int amount,
                           String name, String description,
                           String itemId, PurchaseType purchaseType) {
        super(name, description, itemId, purchaseType);

        mGood = good;
        mGoodAmount = amount;
    }

    public SingleUsePackVG(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        String goodItemId = jsonObject.getString(JSONConsts.VGP_GOOD_ITEMID);
        mGoodAmount = jsonObject.getInt(JSONConsts.VGP_GOOD_AMOUNT);

        try {
            mGood = (SingleUseVG) StoreInfo.getVirtualItem(goodItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "Tried to fetch virtual item with itemId '" + goodItemId + "' but it didn't exist.");
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

            jsonObject.put(JSONConsts.VGP_GOOD_ITEMID, mGood.getItemId());
            jsonObject.put(JSONConsts.VGP_GOOD_AMOUNT, mGoodAmount);
        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }

    @Override
    public void give(int amount) {
        StorageManager.getVirtualGoodsStorage().add(mGood, mGoodAmount*amount);
    }

    @Override
    public void take(int amount) {
        StorageManager.getVirtualGoodsStorage().remove(mGood, mGoodAmount*amount);
    }

    @Override
    protected boolean canBuy() {
        return true;
    }

    private static final String TAG = "SOOMLA SingleUsePackVG";

    private SingleUseVG mGood;
    private int         mGoodAmount;
}

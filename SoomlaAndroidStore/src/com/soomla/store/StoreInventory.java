package com.soomla.store;

import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.data.VirtualCurrency;
import com.soomla.store.domain.data.VirtualGood;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

public class StoreInventory {

    /** Virtual Currencies **/

    public static int getCurrencyBalance(String currencyItemId) throws VirtualItemNotFoundException {
        VirtualCurrency currency = StoreInfo.getInstance().getVirtualCurrencyByItemId(currencyItemId);

        return StorageManager.getInstance().getVirtualCurrencyStorage().getBalance(currency);
    }

    public static int addCurrencyAmount(String currencyItemId, int amount) throws VirtualItemNotFoundException {
        VirtualCurrency currency = StoreInfo.getInstance().getVirtualCurrencyByItemId(currencyItemId);

        return StorageManager.getInstance().getVirtualCurrencyStorage().add(currency, amount);
    }

    public static int removeCurrencyAmount(String currencyItemId, int amount) throws VirtualItemNotFoundException {
        VirtualCurrency currency = StoreInfo.getInstance().getVirtualCurrencyByItemId(currencyItemId);

        return StorageManager.getInstance().getVirtualCurrencyStorage().remove(currency, amount);
    }

    /** Virtual Goods **/

    public static int getGoodBalance(String goodItemId) throws VirtualItemNotFoundException {
        VirtualGood good = StoreInfo.getInstance().getVirtualGoodByItemId(goodItemId);

        return StorageManager.getInstance().getVirtualGoodsStorage().getBalance(good);
    }

    public static int addGoodAmount(String goodItemId, int amount) throws VirtualItemNotFoundException {
        VirtualGood good = StoreInfo.getInstance().getVirtualGoodByItemId(goodItemId);

        return StorageManager.getInstance().getVirtualGoodsStorage().add(good, amount);
    }

    public static int removeGoodAmount(String goodItemId, int amount) throws VirtualItemNotFoundException {
        VirtualGood good = StoreInfo.getInstance().getVirtualGoodByItemId(goodItemId);

        return StorageManager.getInstance().getVirtualGoodsStorage().remove(good, amount);
    }
}

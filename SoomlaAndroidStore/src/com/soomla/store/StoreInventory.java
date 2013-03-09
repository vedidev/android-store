package com.soomla.store;

import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.data.NonConsumableItem;
import com.soomla.store.domain.data.VirtualCurrency;
import com.soomla.store.domain.data.VirtualGood;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

public class StoreInventory {

    /** Virtual Currencies **/

    public static int getCurrencyBalance(String currencyItemId) throws VirtualItemNotFoundException {
        VirtualCurrency currency = StoreInfo.getVirtualCurrencyByItemId(currencyItemId);

        return StorageManager.getVirtualCurrencyStorage().getBalance(currency);
    }

    public static int addCurrencyAmount(String currencyItemId, int amount) throws VirtualItemNotFoundException {
        VirtualCurrency currency = StoreInfo.getVirtualCurrencyByItemId(currencyItemId);

        return StorageManager.getVirtualCurrencyStorage().add(currency, amount);
    }

    public static int removeCurrencyAmount(String currencyItemId, int amount) throws VirtualItemNotFoundException {
        VirtualCurrency currency = StoreInfo.getVirtualCurrencyByItemId(currencyItemId);

        return StorageManager.getVirtualCurrencyStorage().remove(currency, amount);
    }

    /** Virtual Goods **/

    public static int getGoodBalance(String goodItemId) throws VirtualItemNotFoundException {
        VirtualGood good = StoreInfo.getVirtualGoodByItemId(goodItemId);

        return StorageManager.getVirtualGoodsStorage().getBalance(good);
    }

    public static int addGoodAmount(String goodItemId, int amount) throws VirtualItemNotFoundException {
        VirtualGood good = StoreInfo.getVirtualGoodByItemId(goodItemId);

        return StorageManager.getVirtualGoodsStorage().add(good, amount);
    }

    public static int removeGoodAmount(String goodItemId, int amount) throws VirtualItemNotFoundException {
        VirtualGood good = StoreInfo.getVirtualGoodByItemId(goodItemId);

        return StorageManager.getVirtualGoodsStorage().remove(good, amount);
    }

    /** NonConsumables **/

    public boolean nonConsumableItemExists(String nonConsProductId) throws VirtualItemNotFoundException {
        NonConsumableItem nonConsumableItem = StoreInfo.getNonConsumableByProductId(nonConsProductId);

        return StorageManager.getNonConsumableItemsStorage().nonConsumableItemExists(nonConsumableItem);
    }

    public void addNonConsumableItem(String nonConsProductId) throws VirtualItemNotFoundException {
        NonConsumableItem nonConsumableItem = StoreInfo.getNonConsumableByProductId(nonConsProductId);

        StorageManager.getNonConsumableItemsStorage().add(nonConsumableItem);
    }

    public void removeNonConsumableItem(String nonConsProductId) throws VirtualItemNotFoundException {
        NonConsumableItem nonConsumableItem = StoreInfo.getNonConsumableByProductId(nonConsProductId);

        StorageManager.getNonConsumableItemsStorage().remove(nonConsumableItem);
    }
}

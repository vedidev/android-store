package com.soomla.example;


import android.os.Handler;
import android.widget.Toast;
import com.soomla.store.IStoreEventHandler;
import com.soomla.store.SoomlaApp;
import com.soomla.store.StoreConfig;
import com.soomla.store.domain.data.GoogleMarketItem;
import com.soomla.store.domain.data.VirtualCurrency;
import com.soomla.store.domain.data.VirtualGood;


public class ExampleEventHandler implements IStoreEventHandler {

    private Handler mHandler;
    private StoreExampleActivity mActivityI;
    public ExampleEventHandler(Handler handler, StoreExampleActivity activityI){
        mHandler = handler;
        mActivityI = activityI;
    }

    @Override
    public void onMarketPurchase(GoogleMarketItem googleMarketItem) {
        showToastIfDebug(googleMarketItem.getProductId() + " was just purchased");
    }

    @Override
    public void onMarketRefund(GoogleMarketItem googleMarketItem) {
        showToastIfDebug(googleMarketItem.getProductId() + " was just refunded");
    }

    @Override
    public void onVirtualGoodPurchased(VirtualGood good) {
        showToastIfDebug(good.getName() + " was just purchased");
    }

    @Override
    public void onVirtualGoodEquipped(VirtualGood good) {
        showToastIfDebug(good.getName() + " was just equipped");
    }

    @Override
    public void onVirtualGoodUnequipped(VirtualGood good) {
        showToastIfDebug(good.getName() + " was just unequipped");
    }

    @Override
    public void onBillingSupported() {
        showToastIfDebug("Billing is supported");
    }

    @Override
    public void onBillingNotSupported() {
        showToastIfDebug("Billing is not supported");
    }

    @Override
    public void onMarketPurchaseProcessStarted(GoogleMarketItem googleMarketItem) {
        showToastIfDebug("Market purchase started for productId: " + googleMarketItem);
    }

    @Override
    public void onGoodsPurchaseProcessStarted() {
        showToastIfDebug("Goods purchase started");
    }

    @Override
    public void onClosingStore() {
        mActivityI.robotBackHome();

        showToastIfDebug("Going to close store");
    }

    @Override
    public void onUnexpectedErrorInStore() {
        showToastIfDebug("Unexpected error occurred !");
    }

    @Override
    public void onOpeningStore() {
        showToastIfDebug("Store is opening");
    }

    @Override
    public void currencyBalanceChanged(VirtualCurrency currency, int balance) {
        showToastIfDebug("(currency) " + currency.getName() + " balance was changed to " + balance + ".");
    }

    @Override
    public void goodBalanceChanged(VirtualGood good, int balance) {
        showToastIfDebug("(good) " + good.getName() + " balance was changed to " + balance + ".");
    }

    private void showToastIfDebug(final String msg) {
        if (StoreConfig.debug){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(SoomlaApp.getAppContext(), msg, 5000);
                    toast.show();
                }
            });
        }
    }
}

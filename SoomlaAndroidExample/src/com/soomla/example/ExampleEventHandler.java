package com.soomla.example;


import android.os.Handler;
import android.widget.Toast;
import com.soomla.store.BusProvider;
import com.soomla.store.SoomlaApp;
import com.soomla.store.StoreConfig;
import com.soomla.store.domain.data.GoogleMarketItem;
import com.soomla.store.domain.data.VirtualCurrency;
import com.soomla.store.domain.data.VirtualGood;
import com.soomla.store.events.*;
import com.squareup.otto.Subscribe;


public class ExampleEventHandler {

    private Handler mHandler;
    private StoreExampleActivity mActivityI;
    public ExampleEventHandler(Handler handler, StoreExampleActivity activityI){
        mHandler = handler;
        mActivityI = activityI;

        BusProvider.getInstance().register(this);
    }

    @Subscribe
    public void onMarketPurchase(MarketPurchaseEvent marketPurchaseEvent) {
        showToastIfDebug(marketPurchaseEvent.getGoogleMarketItem().getProductId() + " was just purchased");
    }

    @Subscribe
    public void onMarketRefund(MarketRefundEvent marketRefundEvent) {
        showToastIfDebug(marketRefundEvent.getGoogleMarketItem().getProductId() + " was just refunded");
    }

    @Subscribe
    public void onVirtualGoodPurchased(GoodPurchasedEvent goodPurchasedEvent) {
        showToastIfDebug(goodPurchasedEvent.getGood().getName() + " was just purchased");
    }

    @Subscribe
    public void onVirtualGoodEquipped(VirtualGoodEquippedEvent virtualGoodEquippedEvent) {
        showToastIfDebug(virtualGoodEquippedEvent.getGood().getName() + " was just equipped");
    }

    @Subscribe
    public void onVirtualGoodUnequipped(VirtualGoodUnEquippedEvent virtualGoodUnEquippedEvent) {
        showToastIfDebug(virtualGoodUnEquippedEvent.getGood().getName() + " was just unequipped");
    }

    @Subscribe
    public void onBillingSupported(BillingSupportedEvent billingSupportedEvent) {
        showToastIfDebug("Billing is supported");
    }

    @Subscribe
    public void onBillingNotSupported(BillingNotSupportedEvent billingNotSupportedEvent) {
        showToastIfDebug("Billing is not supported");
    }

    @Subscribe
    public void onMarketPurchaseProcessStarted(MarketPurchaseStartedEvent marketPurchaseStartedEvent) {
        showToastIfDebug("Market purchase started for productId: " + marketPurchaseStartedEvent.getGoogleMarketItem().getProductId());
    }

    @Subscribe
    public void onGoodsPurchaseProcessStarted(GoodPurchaseStartedEvent goodPurchaseStartedEvent) {
        showToastIfDebug("Goods purchase started for good: " + goodPurchaseStartedEvent.getGood().getName());
    }

    @Subscribe
    public void onClosingStore(ClosingStoreEvent closingStoreEvent) {
        mActivityI.robotBackHome();

        showToastIfDebug("Going to close store");
    }

    @Subscribe
    public void onUnexpectedErrorInStore(UnexpectedStoreErrorEvent unexpectedStoreErrorEvent) {
        showToastIfDebug("Unexpected error occurred !");
    }

    @Subscribe
    public void onOpeningStore(OpeningStoreEvent openingStoreEvent) {
        showToastIfDebug("Store is opening");
    }

    @Subscribe
    public void onCurrencyBalanceChanged(CurrencyBalanceChangedEvent currencyBalanceChangedEvent) {
        showToastIfDebug("(currency) " + currencyBalanceChangedEvent.getCurrency().getName() + " balance was changed to " + currencyBalanceChangedEvent.getBalance() + ".");
    }

    @Subscribe
    public void onGoodBalanceChanged(GoodBalanceChangedEvent goodBalanceChangedEvent) {
        showToastIfDebug("(good) " + goodBalanceChangedEvent.getGood().getName() + " balance was changed to " + goodBalanceChangedEvent.getBalance() + ".");
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

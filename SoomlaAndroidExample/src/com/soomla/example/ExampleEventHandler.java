package com.soomla.example;


import android.os.Handler;
import android.widget.Toast;
import com.soomla.store.BusProvider;
import com.soomla.store.SoomlaApp;
import com.soomla.store.StoreConfig;
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
    public void onMarketPurchase(PlayPurchaseEvent marketPurchaseEvent) {
        showToastIfDebug(marketPurchaseEvent.getPurchasableVirtualItem().getName() + " was just purchased");
    }

    @Subscribe
    public void onMarketRefund(PlayRefundEvent marketRefundEvent) {
        showToastIfDebug(marketRefundEvent.getPurchasableVirtualItem().getName() + " was just refunded");
    }

    @Subscribe
    public void onVirtualItemPurchased(ItemPurchasedEvent itemPurchasedEvent) {
        showToastIfDebug(itemPurchasedEvent.getPurchasableVirtualItem().getName() + " was just purchased");
    }

    @Subscribe
    public void onVirtualGoodEquipped(GoodEquippedEvent virtualGoodEquippedEvent) {
        showToastIfDebug(virtualGoodEquippedEvent.getGood().getName() + " was just equipped");
    }

    @Subscribe
    public void onVirtualGoodUnequipped(GoodUnEquippedEvent virtualGoodUnEquippedEvent) {
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
    public void onPlayPurchaseStartedEvent(PlayPurchaseStartedEvent marketPurchaseStartedEvent) {
        showToastIfDebug("Market purchase started for: " + marketPurchaseStartedEvent.getPurchasableVirtualItem().getName());
    }

    @Subscribe
    public void onPlayPurchaseCancelledEvent(PlayPurchaseCancelledEvent marketPurchaseCancelledEvent) {
        showToastIfDebug("Market purchase started for: " + marketPurchaseCancelledEvent.getPurchasableVirtualItem().getName());
    }

    @Subscribe
    public void onItemPurchaseStartedEvent(ItemPurchaseStartedEvent itemPurchaseStartedEvent) {
        showToastIfDebug("Item purchase started for: " + itemPurchaseStartedEvent.getPurchasableVirtualItem().getName());
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

    @Subscribe
    public void onRestoreTransactionsEvent(RestoreTransactionsEvent restoreTransactionsEvent) {
        showToastIfDebug("restoreTransactions: " + restoreTransactionsEvent.isSuccess() + ".");
    }

    @Subscribe
    public void onRestoreTransactionsStartedEvent(RestoreTransactionsStartedEvent restoreTransactionsStartedEvent) {
        showToastIfDebug("restoreTransactions Started");
    }

    private void showToastIfDebug(final String msg) {
        if (StoreConfig.logDebug){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(SoomlaApp.getAppContext(), msg, 2000);
                    toast.show();
                }
            });
        }
    }
}

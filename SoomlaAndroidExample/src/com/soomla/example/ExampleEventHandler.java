package com.soomla.example;


import android.os.Handler;
import android.widget.Toast;
import com.soomla.store.BusProvider;
import com.soomla.store.SoomlaApp;
import com.soomla.store.StoreConfig;
import com.soomla.store.events.*;
import com.squareup.otto.Subscribe;

/**
 * This class contains functions that receive events that they are subscribed to. Annotating with
 * @subscribe before each function lets the function receive notification when an event has
 * occurred.
 */
public class ExampleEventHandler {

    /**
     * Constructor
     * In order to receive events, we need to register a class instance (this) with the bus.
     *
     * @param handler event handler
     * @param activityI StoreExampleActivity
     */
    public ExampleEventHandler(Handler handler, StoreExampleActivity activityI){
        mHandler = handler;
        mActivityI = activityI;
        //Register this class
        BusProvider.getInstance().register(this);
    }

    /**
     * Receives the given marketPurchaseEvent, and in case the debugging setting is on,
     * displays a message stating the name of the market item that was purchased.
     * The item in the given marketPurchasedEvent is an item that was purchase via the Market
     * (with money, not with virtual items).
     *
     * @param marketPurchaseEvent the event received
     */
    @Subscribe
    public void onMarketPurchase(MarketPurchaseEvent marketPurchaseEvent) {
        showToastIfDebug(marketPurchaseEvent.getPurchasableVirtualItem().getName()
                + " was just purchased");
    }

    /**
     * Receives the given marketRefundEvent, and in case the debugging setting is on,
     * displays a message stating the name of the item that was refunded.
     *
     * @param marketRefundEvent the event received
     */
    @Subscribe
    public void onMarketRefund(MarketRefundEvent marketRefundEvent) {
        showToastIfDebug(marketRefundEvent.getPurchasableVirtualItem().getName()
                + " was just refunded");
    }

    /**
     * Receives the given itemPurchasedEvent, and in case the debugging setting is on,
     * displays a message stating the name of the item that was purchased.
     *
     * @param itemPurchasedEvent the event received
     */
    @Subscribe
    public void onVirtualItemPurchased(ItemPurchasedEvent itemPurchasedEvent) {
        showToastIfDebug(itemPurchasedEvent.getPurchasableVirtualItem().getName()
                + " was just purchased");
    }

    /**
     * Receives the given virtualGoodEquippedEvent, and in case the debugging setting is on,
     * displays a message stating the name of the item that was equipped.
     *
     * @param virtualGoodEquippedEvent the event received
     */
    @Subscribe
    public void onVirtualGoodEquipped(GoodEquippedEvent virtualGoodEquippedEvent) {
        showToastIfDebug(virtualGoodEquippedEvent.getGood().getName() + " was just equipped");
    }

    /**
     * Receives the given virtualGoodUnEquippedEvent, and in case the debugging setting is on,
     * displays a message stating the name of the item that was unequipped.
     *
     * @param virtualGoodUnEquippedEvent the event received
     */
    @Subscribe
    public void onVirtualGoodUnequipped(GoodUnEquippedEvent virtualGoodUnEquippedEvent) {
        showToastIfDebug(virtualGoodUnEquippedEvent.getGood().getName() + " was just unequipped");
    }

    /**
     * Receives the given billingSupportedEvent, and in case the debugging setting is on,
     * displays a message stating that "billing is supported".
     *
     * @param billingSupportedEvent the event received
     */
    @Subscribe
    public void onBillingSupported(BillingSupportedEvent billingSupportedEvent) {
        showToastIfDebug("Billing is supported");
    }

    /**
     * Receives the given billingNotSupportedEvent, and in case the debugging setting is on,
     * displays a message stating that "billing is not supported".
     *
     * @param billingNotSupportedEvent the event received
     */
    @Subscribe
    public void onBillingNotSupported(BillingNotSupportedEvent billingNotSupportedEvent) {
        showToastIfDebug("Billing is not supported");
    }

    /**
     * Receives the given marketPurchaseStartedEvent, and in case the debugging setting is on,
     * displays a message stating the name of the market item that is starting to be purchased.
     * The item in the given marketPurchaseStartedEvent is an item that is being purchased via
     * the Market (with money, not with virtual items).
     *
     * @param marketPurchaseStartedEvent the event received
     */
    @Subscribe
    public void onMarketPurchaseStarted(MarketPurchaseStartedEvent marketPurchaseStartedEvent) {
        showToastIfDebug("Market purchase started for: "
                + marketPurchaseStartedEvent.getPurchasableVirtualItem().getName());
    }

    /**
     * Receives the given marketPurchaseCancelledEvent, and in case the debugging setting is on,
     * displays a message stating the name of the purchase item that is being cancelled.
     * The item in the given marketPurchaseCancelledEvent is an item that was purchased via the
     * Market (with money, not with virtual items).
     *
     * @param marketPurchaseCancelledEvent the event received
     */
    @Subscribe
    public void onMarketPurchaseCancelled(
            MarketPurchaseCancelledEvent marketPurchaseCancelledEvent) {
        showToastIfDebug("Market purchase cancelled for: "
                + marketPurchaseCancelledEvent.getPurchasableVirtualItem().getName());
    }

    /**
     * Receives the given itemPurchaseStartedEvent, and in case the debugging setting is on,
     * displays a message stating the name of the item that is starting to be purchased.
     *
     * @param itemPurchaseStartedEvent the event received
     */
    @Subscribe
    public void onItemPurchaseStarted(ItemPurchaseStartedEvent itemPurchaseStartedEvent) {
        showToastIfDebug("Item purchase started for: "
                + itemPurchaseStartedEvent.getPurchasableVirtualItem().getName());
    }

    /**
     * Receives the given unexpectedStoreErrorEvent, and in case the debugging setting is on,
     * displays a message stating that an error has occurred.
     *
     * @param unexpectedStoreErrorEvent the event received
     */
    @Subscribe
    public void onUnexpectedErrorInStore(UnexpectedStoreErrorEvent unexpectedStoreErrorEvent) {
        showToastIfDebug("Unexpected error occurred !");
    }

    /**
     * Receives the given iabServiceStartedEvent, and in case the debugging setting is on,
     * displays a message stating that the service has started.
     *
     * @param iabServiceStartedEvent the event received
     */
    @Subscribe
    public void onIabServiceStarted(IabServiceStartedEvent iabServiceStartedEvent) {
        showToastIfDebug("Iab Service started");
    }

    /**
     * Receives the given iabServiceStoppedEvent, and in case the debugging setting is on,
     * displays a message stating that the service has stopped.
     *
     * @param iabServiceStoppedEvent the event received
     */
    @Subscribe
    public void onIabServiceStopped(IabServiceStoppedEvent iabServiceStoppedEvent) {
        showToastIfDebug("Iab Service stopped");
    }

    /**
     * Receives the given onCurrencyBalanceChanged, and in case the debugging setting is on,
     * displays a message stating what currency's balance has changed, and what the new balance is.
     *
     * @param currencyBalanceChangedEvent the event received
     */
    @Subscribe
    public void onCurrencyBalanceChanged(CurrencyBalanceChangedEvent currencyBalanceChangedEvent) {
        showToastIfDebug("(currency) " + currencyBalanceChangedEvent.getCurrency().getName()
                + " balance was changed to " + currencyBalanceChangedEvent.getBalance() + ".");
    }

    /**
     * Receives the given goodBalanceChangedEvent, and in case the debugging setting is on,
     * displays a message stating what good's balance has changed, and what the new balance is.
     *
     * @param goodBalanceChangedEvent the event received
     */
    @Subscribe
    public void onGoodBalanceChanged(GoodBalanceChangedEvent goodBalanceChangedEvent) {
        showToastIfDebug("(good) " + goodBalanceChangedEvent.getGood().getName()
                + " balance was changed to " + goodBalanceChangedEvent.getBalance() + ".");
    }

    /**
     * Receives the given restoreTransactionsFinishedEvent, and in case the debugging setting is on,
     * displays a message stating that restoreTransactions finished successfully/unsuccessfully.
     *
     * @param restoreTransactionsFinishedEvent the event received
     */
    @Subscribe
    public void onRestoreTransactionsFinished(
            RestoreTransactionsFinishedEvent restoreTransactionsFinishedEvent) {
        showToastIfDebug("restoreTransactions: "
                + restoreTransactionsFinishedEvent.isSuccess() + ".");
    }

    /**
     * Receives the given restoreTransactionsStartedEvent, and in case the debugging setting is on,
     * displays a message stating that restoreTransactions has started.
     *
     * @param restoreTransactionsStartedEvent the event received
     */
    @Subscribe
    public void onRestoreTransactionsStarted(
            RestoreTransactionsStartedEvent restoreTransactionsStartedEvent) {
        showToastIfDebug("restoreTransactions Started");
    }

    /**
     * Receives the given storeControllerInitializedEvent, and in case the debugging setting is on,
     * displays a message stating that StoreController has been initialized.
     *
     * @param storeControllerInitializedEvent the event received
     */
    @Subscribe
    public void onStoreControllerInitialized(
            StoreControllerInitializedEvent storeControllerInitializedEvent) {
        String [] s = {"no_ads"};
        // StoreController.getInstance().getItemDetails(s);
        showToastIfDebug("storeControllerInitialized");
    }

    /**
     * Posts to Handler if logDebug is set to true. Enqueues a Runnable object to be called
     * by the message queue when it is received. The Runnable displays a debug message.
     *
     * @param msg message to be displayed as a part of the Runnable's run method.
     */
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


    /** Private Members */

    private Handler mHandler;

    private StoreExampleActivity mActivityI;

}

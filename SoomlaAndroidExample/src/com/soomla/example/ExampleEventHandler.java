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
 * @subscribe before each function lets the function receive a notification when an event has
 * occurred.
 */
public class ExampleEventHandler {

    /**
     * Constructor
     * In order to receive events, this class instance needs to register with the bus.
     *
     * @param handler event handler
     * @param activityI StoreExampleActivity
     */
    public ExampleEventHandler(Handler handler, StoreExampleActivity activityI){
        mHandler = handler;
        mActivityI = activityI;
        BusProvider.getInstance().register(this);
    }

    /**
     * Listens for the given marketPurchaseEvent that was fired. Upon receiving such an event, if
     * the debugging setting is on, displays a message stating the name of the market item that was
     * purchased. The item in the given marketPurchasedEvent is an item that was purchased via the
     * Market (with money, not with virtual items).
     *
     * @param marketPurchaseEvent the market purchase event that was fired
     */
    @Subscribe
    public void onMarketPurchase(MarketPurchaseEvent marketPurchaseEvent) {
        showToastIfDebug(marketPurchaseEvent.getPurchasableVirtualItem().getName()
                + " was just purchased");
    }

    /**
     * Listens for the given marketRefundEvent that was fired. Upon receiving such an event, if the
     * debugging setting is on, displays a message stating the name of the item that was refunded.
     *
     * @param marketRefundEvent the market refund event that was fired
     */
    @Subscribe
    public void onMarketRefund(MarketRefundEvent marketRefundEvent) {
        showToastIfDebug(marketRefundEvent.getPurchasableVirtualItem().getName()
                + " was just refunded");
    }

    /**
     * Listens for the given itemPurchasedEvent that was fired. Upon receiving such an event, if
     * the debugging setting is on, displays a message stating the name of the virtual item that
     * was purchased.
     *
     * @param itemPurchasedEvent the item purchased event that was fired
     */
    @Subscribe
    public void onVirtualItemPurchased(ItemPurchasedEvent itemPurchasedEvent) {
        showToastIfDebug(itemPurchasedEvent.getPurchasableVirtualItem().getName()
                + " was just purchased");
    }

    /**
     * Listens for the given virtualGoodEquippedEvent that was fired. Upon receiving such an event,
     * if the debugging setting is on, displays a message stating the name of the virtual good that
     * was equipped.
     *
     * @param virtualGoodEquippedEvent the virtual good equipped event that was fired
     */
    @Subscribe
    public void onVirtualGoodEquipped(GoodEquippedEvent virtualGoodEquippedEvent) {
        showToastIfDebug(virtualGoodEquippedEvent.getGood().getName() + " was just equipped");
    }

    /**
     * Listens for the given virtualGoodUnEquippedEvent that was fired. Upon receiving such an
     * event, if the debugging setting is on, displays a message stating the name of the virtual
     * good that was unequipped.
     *
     * @param virtualGoodUnEquippedEvent the virtual good unequipped event that was fired
     */
    @Subscribe
    public void onVirtualGoodUnequipped(GoodUnEquippedEvent virtualGoodUnEquippedEvent) {
        showToastIfDebug(virtualGoodUnEquippedEvent.getGood().getName() + " was just unequipped");
    }

    /**
     * Listens for the given billingSupportedEvent that was fired. Upon receiving such an event,
     * if the debugging setting is on, displays a message stating that billing is supported.
     *
     * @param billingSupportedEvent the billing supported event that was fired
     */
    @Subscribe
    public void onBillingSupported(BillingSupportedEvent billingSupportedEvent) {
        showToastIfDebug("Billing is supported");
    }

    /**
     * Listens for the given billingNotSupportedEvent that was fired. Upon receiving such an event,
     * if the debugging setting is on, displays a message stating that billing is not supported.
     *
     * @param billingNotSupportedEvent the billing not supported event that was fired
     */
    @Subscribe
    public void onBillingNotSupported(BillingNotSupportedEvent billingNotSupportedEvent) {
        showToastIfDebug("Billing is not supported");
    }

    /**
     * Listens for the given marketPurchaseStartedEvent that was fired. Upon receiving such an
     * event, if the debugging setting is on, displays a message stating the name of the market
     * item that is starting to be purchased. The item in the given marketPurchaseStartedEvent is
     * an item that is being purchased via the Market (with money, not with virtual items).
     *
     * @param marketPurchaseStartedEvent the market purchase started event that was fired
     */
    @Subscribe
    public void onMarketPurchaseStarted(MarketPurchaseStartedEvent marketPurchaseStartedEvent) {
        showToastIfDebug("Market purchase started for: "
                + marketPurchaseStartedEvent.getPurchasableVirtualItem().getName());
    }

    /**
     * Listens for the given marketPurchaseCancelledEvent that was fired. Upon receiving such an
     * event, if the debugging setting is on, displays a message stating the name of the market
     * item that is being cancelled. The item in the given marketPurchaseCancelledEvent is an item
     * that was purchased via the Market (with money, not with virtual items).
     *
     * @param marketPurchaseCancelledEvent the market purchase cancelled event that was fired
     */
    @Subscribe
    public void onMarketPurchaseCancelled(
            MarketPurchaseCancelledEvent marketPurchaseCancelledEvent) {
        showToastIfDebug("Market purchase cancelled for: "
                + marketPurchaseCancelledEvent.getPurchasableVirtualItem().getName());
    }

    /**
     * Listens for the given itemPurchaseStartedEvent that was fired. Upon receiving such an
     * event, if the debugging setting is on, displays a message stating the name of the item that
     * is starting to be purchased.
     *
     * @param itemPurchaseStartedEvent the item purchase started event that was fired
     */
    @Subscribe
    public void onItemPurchaseStarted(ItemPurchaseStartedEvent itemPurchaseStartedEvent) {
        showToastIfDebug("Item purchase started for: "
                + itemPurchaseStartedEvent.getPurchasableVirtualItem().getName());
    }

    /**
     * Listens for the given unexpectedStoreErrorEvent that was fired. Upon receiving such an event,
     * if the debugging setting is on, displays a message stating that an error has occurred.
     *
     * @param unexpectedStoreErrorEvent the unexpected store error event that was fired
     */
    @Subscribe
    public void onUnexpectedErrorInStore(UnexpectedStoreErrorEvent unexpectedStoreErrorEvent) {
        showToastIfDebug("Unexpected error occurred !");
    }

    /**
     * Listens for the given iabServiceStartedEvent that was fired. Upon receiving such an event,
     * if the debugging setting is on, displays a message stating that the service has started.
     *
     * @param iabServiceStartedEvent the in-app billing service started event that was fired
     */
    @Subscribe
    public void onIabServiceStarted(IabServiceStartedEvent iabServiceStartedEvent) {
        showToastIfDebug("Iab Service started");
    }

    /**
     * Listens for the given iabServiceStoppedEvent that was fired. Upon receiving such an event,
     * if the debugging setting is on, displays a message stating that the service has stopped.
     *
     * @param iabServiceStoppedEvent the in-app billing service stopped event that was fired
     */
    @Subscribe
    public void onIabServiceStopped(IabServiceStoppedEvent iabServiceStoppedEvent) {
        showToastIfDebug("Iab Service stopped");
    }

    /**
     * Listens for the given currencyBalanceChangedEvent that was fired. Upon receiving such an
     * event, if the debugging setting is on, displays a message stating which currency's balance
     * has changed, and what its new balance is.
     *
     * @param currencyBalanceChangedEvent the currency balance changed event that was fired
     */
    @Subscribe
    public void onCurrencyBalanceChanged(CurrencyBalanceChangedEvent currencyBalanceChangedEvent) {
        showToastIfDebug("(currency) " + currencyBalanceChangedEvent.getCurrency().getName()
                + " balance was changed to " + currencyBalanceChangedEvent.getBalance() + ".");
    }

    /**
     * Listens for the given goodBalanceChangedEvent that was fired. Upon receiving such an event,
     * if the debugging setting is on, displays a message stating which good's balance has changed,
     * and what its new balance is.
     *
     * @param goodBalanceChangedEvent the good balance changed event that was fired
     */
    @Subscribe
    public void onGoodBalanceChanged(GoodBalanceChangedEvent goodBalanceChangedEvent) {
        showToastIfDebug("(good) " + goodBalanceChangedEvent.getGood().getName()
                + " balance was changed to " + goodBalanceChangedEvent.getBalance() + ".");
    }

    /**
     * Listens for the given restoreTransactionsFinishedEvent that was fired. Upon receiving such
     * an event, if the debugging setting is on, displays a message stating that
     * restoreTransactions finished successfully or unsuccessfully.
     *
     * @param restoreTransactionsFinishedEvent the restore transactions finished event that was
     *                                         fired
     */
    @Subscribe
    public void onRestoreTransactionsFinished(
            RestoreTransactionsFinishedEvent restoreTransactionsFinishedEvent) {
        showToastIfDebug("restoreTransactions: "
                + restoreTransactionsFinishedEvent.isSuccess() + ".");
    }

    /**
     * Listens for the given restoreTransactionsStartedEvent that was fired. Upon receiving such
     * an event, if the debugging setting is on, displays a message stating that
     * restoreTransactions has started.
     *
     * @param restoreTransactionsStartedEvent the restore transactions started event that was fired
     */
    @Subscribe
    public void onRestoreTransactionsStarted(
            RestoreTransactionsStartedEvent restoreTransactionsStartedEvent) {
        showToastIfDebug("restoreTransactions Started");
    }

    /**
     * Listens for the given storeControllerInitializedEvent that was fired. Upon receiving such
     * an event, if the debugging setting is on, displays a message stating that StoreController
     * has been initialized.
     *
     * @param storeControllerInitializedEvent the store controller initialized event that was fired
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

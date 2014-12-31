package com.soomla.store;

import com.soomla.BusProvider;
import com.soomla.SoomlaApp;
import com.soomla.store.events.MarketPurchaseCancelledEvent;
import com.soomla.store.events.MarketPurchaseEvent;
import com.soomla.store.events.MarketPurchaseStartedEvent;
import com.soomla.store.events.UnexpectedStoreErrorEvent;
import com.squareup.otto.Subscribe;

public class StoreForeground {

    private StoreForeground() {
        BusProvider.getInstance().register(this);
    }

    public static synchronized StoreForeground get() {
        if (sInstance == null) {
            sInstance = new StoreForeground();
        }
        return sInstance;
    }

    private static StoreForeground sInstance;


    @Subscribe
    public void onMarketPurchaseEvent(MarketPurchaseEvent marketPurchaseEvent) {
        if (SoomlaApp.ForegroundService != null) {
            SoomlaApp.ForegroundService.OutsideOperation = false;
        }
    }

    @Subscribe
    public void onMarketPurchaseCancelledEvent(MarketPurchaseCancelledEvent marketPurchaseCancelledEvent) {
        if (SoomlaApp.ForegroundService != null) {
            SoomlaApp.ForegroundService.OutsideOperation = false;
        }
    }

    @Subscribe
    public void onMarketPurchaseStartedEvent(MarketPurchaseStartedEvent marketPurchaseStartedEvent) {

        if (SoomlaApp.ForegroundService != null) {
            SoomlaApp.ForegroundService.OutsideOperation = true;
        }
    }

    @Subscribe
    public void onUnexpectedStoreErrorEvent(UnexpectedStoreErrorEvent unexpectedStoreErrorEvent) {
        if (SoomlaApp.ForegroundService != null) {
            SoomlaApp.ForegroundService.OutsideOperation = false;
        }
    }

}

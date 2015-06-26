package com.soomla.store.events;

import com.soomla.store.billing.IabPurchase;
import com.soomla.store.domain.PurchasableVirtualItem;

/**
 * @author vedi
 *         date 26/05/15
 */
public class MarketPurchaseVerificationEvent {
    private final PurchasableVirtualItem pvi;
    private final boolean verified;
    private final IabPurchase purchase;

    public MarketPurchaseVerificationEvent(PurchasableVirtualItem pvi, boolean verified, IabPurchase purchase) {
        this.pvi = pvi;
        this.verified = verified;
        this.purchase = purchase;
    }

    public PurchasableVirtualItem getPvi() {
        return pvi;
    }

    public boolean isVerified() {
        return verified;
    }

    public IabPurchase getPurchase() {
        return purchase;
    }
}

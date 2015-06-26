package com.soomla.store.billing;

import com.soomla.store.domain.PurchasableVirtualItem;

/**
 * @author vedi
 *         date 25/06/15
 */
public interface IIabVerifiable {
    boolean getVerifyPurchases();
    void setVerifyPurchases(boolean verifyPurchases);
    void verifyPurchase(IabPurchase purchase, PurchasableVirtualItem pvi);
}

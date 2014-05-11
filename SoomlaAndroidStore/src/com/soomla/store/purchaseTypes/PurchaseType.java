/**
 * Copyright (C) 2012-2014 Soomla Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.soomla.store.purchaseTypes;

import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.exceptions.InsufficientFundsException;

/**
 * A <code>PurchaseType</code> is a way to purchase a <code>PurchasableVirtualItem</code>. This
 * abstract class describes basic features of the actual implementations of <code>PurchaseType<code>.
 */
public abstract class PurchaseType {

    /*
     * Buys the purchasable virtual item.
     * Implementation in subclasses will be according to specific type of purchase.
     *
     * @throws com.soomla.store.exceptions.InsufficientFundsException
     */
    public abstract void buy() throws InsufficientFundsException;


    /** Setters and Getters */

    public void setAssociatedItem(PurchasableVirtualItem associatedItem) {
        mAssociatedItem = associatedItem;
    }

    public PurchasableVirtualItem getAssociatedItem() {
        return mAssociatedItem;
    }


    /** Private Members */

    //the PurchasableVirtualItem associated with this PurchaseType
    private PurchasableVirtualItem mAssociatedItem;

}

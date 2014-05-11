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


package com.soomla.store.data;


import com.soomla.store.BusProvider;
import com.soomla.store.domain.VirtualItem;
import com.soomla.store.domain.virtualCurrencies.VirtualCurrency;
import com.soomla.store.events.CurrencyBalanceChangedEvent;

/**
 * This class provides basic storage operations on virtual currencies.
 */
public class VirtualCurrencyStorage extends VirtualItemStorage{

    /**
     * Constructor
     */
    public VirtualCurrencyStorage() {
        mTag = "SOOMLA VirtualCurrencyStorage";
    }

    /**
     * see parent
     *
     * @param itemId id of the virtual item whose balance is to be retrieved
     * @return see parent
     */
    @Override
    protected String keyBalance(String itemId) {
        return KeyValDatabase.keyCurrencyBalance(itemId);
    }

    /**
     * see parent
     *
     * @param item virtual item whose balance has changed
     * @param balance the balance that has changed
     * @param amountAdded the amount added to the item's balance
     */
    @Override
    protected void postBalanceChangeEvent(VirtualItem item, int balance, int amountAdded) {
        BusProvider.getInstance().post(new CurrencyBalanceChangedEvent((VirtualCurrency) item,
                balance, amountAdded));
    }
}

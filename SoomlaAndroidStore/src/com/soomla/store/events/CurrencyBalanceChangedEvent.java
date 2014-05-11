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

package com.soomla.store.events;

import com.soomla.store.domain.virtualCurrencies.VirtualCurrency;

/**
 * This event is fired when the balance of a specific <code>VirtualCurrency</code> has changed.
 *
 * Real Game Example:
 *  Example Inventory: { currency_coin: 100, green_hat: 3, blue_hat: 5 }
 *  Suppose your user buys something for 10 "Coins".
 *  His new balance of currency_coin will now be 90.
 *  A <code>CurrencyBalanceChangedEvent</code> is fired.
 */
public class CurrencyBalanceChangedEvent {

    /**
     * Constructor
     *
     * @param currency
     * @param balance
     * @param amountAdded
     */
    public CurrencyBalanceChangedEvent(VirtualCurrency currency, int balance, int amountAdded) {
        mCurrency = currency;
        mBalance = balance;
        mAmountAdded = amountAdded;
    }


    /** Setters and Getters */

    public VirtualCurrency getCurrency() {
        return mCurrency;
    }

    public int getBalance() {
        return mBalance;
    }

    public int getAmountAdded() {
        return mAmountAdded;
    }


    /** Private Members */

    private VirtualCurrency mCurrency;

    private int mBalance;

    private int mAmountAdded;
}

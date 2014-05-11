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

package com.soomla.store.exceptions;

/**
 * When a user tries to perform an action for which he does not have enough funds, an
 * <code>InsufficientFundsException</code> is thrown.
 *
 * Real Game Example:
 *  Example Inventory: { currency_coin: 100, green_hat: 3, blue_hat: 5 }
 *  Say a blue_hat costs 200 currency_coin.
 *  Suppose that you have a user that wants to buy a blue_hat.
 *  You'll probably call StoreInventory.buy("blue_hat").
 *  <code>InsufficientFundsException</code> will be thrown with "blue_hat" as the itemId.
 *  You can just catch this exception in order to notify the user that he doesn't have enough
 *  coins to buy a blue_hat.
 */
public class InsufficientFundsException extends Exception {

    /**
     * Constructor
     *
     * @param itemId id of the item that was attempted to buy with
     */
    public InsufficientFundsException(String itemId) {
        super("You tried to buy with itemId: " + itemId + " but you don't have enough of it.");

        mItemId = itemId;
    }

    /** Setters and Getters */

    public String getItemId() {
        return mItemId;
    }


    /** Private Members */

    private String mItemId;
}

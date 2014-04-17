/*
 * Copyright (C) 2012 Soomla Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soomla.store.exceptions;

/**
 * When a user tries to perform an action for which he does not have enough funds, an
 * InsufficientFundsException is thrown.
 *
 * Real Game Example:
 *  Example Inventory: { currency_coin: 100, green_hat: 3, blue_hat: 5 }
 *  Say a blue_hat costs 200 currency_coin.
 *  Suppose that you have a user that wants to buy a blue_hat.
 *  You'll probably call StoreInventory.buy("blue_hat").
 *  InsufficientFundsException will be thrown with "blue_hat" as the itemId.
 *  You can just catch this exception in order to notify the user that he doesn't have enough
 *  coins to buy a blue_hat.
 *
 * InsufficientFundsException extends Exception
 */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String itemId) {
        super("You tried to buy with itemId: " + itemId + " but you don't have enough of it.");

        mItemId = itemId;
    }

    /** Setters and Getters */
    public String getItemId() {
        return mItemId;
    }

    private String mItemId;
}

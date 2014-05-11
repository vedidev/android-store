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

import com.soomla.store.domain.virtualGoods.EquippableVG;

/**
 * This event is fired when the balance of a specific EquippableVG has been equipped.
 *
 * Real Game Example:
 *  Example Inventory: { currency_coin: 100, Characters: Robot_X, Robot_Y }
 *  Suppose your user equips Robot_X.
 *  His new balance of Characters will be { Characters: Robot_Y }.
 *  A <code>GoodEquippedEvent</code> is fired.
 */
public class GoodEquippedEvent {
    /**
     * Constructor
     *
     * @param good
     */
    public GoodEquippedEvent(EquippableVG good) {
        mGood = good;
    }


    /** Setters and Getters */

    public EquippableVG getGood() {
        return mGood;
    }


    /** Private Members */

    private EquippableVG mGood;
}

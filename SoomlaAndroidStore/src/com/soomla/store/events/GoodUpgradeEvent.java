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

import com.soomla.store.domain.virtualGoods.UpgradeVG;
import com.soomla.store.domain.virtualGoods.VirtualGood;

/**
 * This event is fired when the balance of a specific <code>EquippableVG</code> has been
 * upgraded/downgraded.
 *
 * Real Game Example:
 *  Example Inventory: { currency_coin: 100, Characters: Robot_X_1 }
 *  Suppose your user upgrades "Characters". Robot_X_1 is the first Robot_X in the series.
 *  After the upgrade, his new balance of Characters will be { Characters: Robot_X_2 }.
 *  A <code>GoodUpgradeEvent</code> is fired.
 */
public class GoodUpgradeEvent {

    /**
     * Constructor
     *
     * @param good good that has been upgraded/downgraded
     * @param upgradeVG upgrade details
     */
    public GoodUpgradeEvent(VirtualGood good, UpgradeVG upgradeVG) {
        mGood = good;
        mCurrentUpgrade = upgradeVG;
    }


    /** Setters and Getters */

    public VirtualGood getGood() {
        return mGood;
    }

    public UpgradeVG getCurrentUpgrade() {
        return mCurrentUpgrade;
    }


    /** Private Members */

    private VirtualGood mGood; //good that has been upgraded/downgraded

    private UpgradeVG   mCurrentUpgrade; //upgrade details

}

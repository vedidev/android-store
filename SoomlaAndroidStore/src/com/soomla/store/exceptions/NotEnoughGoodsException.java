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
 * When a user tries to equip a virtual good which he does not own.
 *
 * Real Game Example:
 *  Example Inventory: { currency_coin: 100, robot_character: 3 }
 *  Suppose that your user would like to equip (LOCAL) a robot_character.
 *  You'll probably call <code>equipVirtualGood</code>("robot_character").
 *  <code>NotEnoughGoodException</code> will be thrown with "robot_character" as the itemId.
 *  You can catch this exception in order to notify the user that he doesn't
 *  own a robot_character (so he cannot equip it!).
 */
public class NotEnoughGoodsException extends Exception{

    /**
     * Constructor
     *
     * @param itemId id of virtual good that was attempted to be equipped
     */
    public NotEnoughGoodsException(String itemId) {
        super("You tried to equip virtual good with itemId: " + itemId
                + " but you don't have any of it.");
    }
}

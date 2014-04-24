package com.soomla.store.exceptions;

/**
 * When a user tries to equip a virtual good which he does not own.
 *
 * Real Game Example:
 *  Example Inventory: { currency_coin: 100, robot_character: 3 }
 *  Suppose that your user would like to equip (LOCAL) a robot_character.
 *  You'll probably call equipVirtualGood("robot_character").
 *  NotEnoughGoodException will be thrown with "robot_character" as the itemId.
 *  You can catch this exception in order to notify the user that he doesn't
 *  own a robot_character (so he cannot equip it!).
 *
 *  NotEnoughGoodsException > Exception
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

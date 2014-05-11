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

/**
 * This class contains all static final String names of the keys/vals in the JSON being parsed all
 * around the sdk.
 */
public class JSONConsts {

    public static final String STORE_CURRENCIES         = "currencies";
    public static final String STORE_CURRENCYPACKS      = "currencyPacks";
    public static final String STORE_GOODS              = "goods";
    public static final String STORE_CATEGORIES         = "categories";
    public static final String STORE_NONCONSUMABLES     = "nonConsumables";
    public static final String STORE_GOODS_SU           = "singleUse";
    public static final String STORE_GOODS_PA           = "goodPacks";
    public static final String STORE_GOODS_UP           = "goodUpgrades";
    public static final String STORE_GOODS_LT           = "lifetime";
    public static final String STORE_GOODS_EQ           = "equippable";

    public static final String ITEM_NAME                = "name";
    public static final String ITEM_DESCRIPTION         = "description";
    public static final String ITEM_ITEMID              = "itemId";

    public static final String CATEGORY_NAME            = "name";
    public static final String CATEGORY_GOODSITEMIDS    = "goods_itemIds";

    public static final String MARKETITEM_PRODUCT_ID    = "productId";
    public static final String MARKETITEM_ANDROID_ID    = "androidId";
    public static final String MARKETITEM_MANAGED       = "consumable";
    public static final String MARKETITEM_PRICE         = "price";

    public static final String EQUIPPABLE_EQUIPPING     = "equipping";

    // VGP = SingleUsePackVG
    public static final String VGP_GOOD_ITEMID          = "good_itemId";
    public static final String VGP_GOOD_AMOUNT          = "good_amount";

    // VGU = UpgradeVG
    public static final String VGU_GOOD_ITEMID          = "good_itemId";
    public static final String VGU_PREV_ITEMID          = "prev_itemId";
    public static final String VGU_NEXT_ITEMID          = "next_itemId";

    public static final String CURRENCYPACK_CURRENCYAMOUNT = "currency_amount";
    public static final String CURRENCYPACK_CURRENCYITEMID = "currency_itemId";

    /** IabPurchase Type **/
    public static final String PURCHASABLE_ITEM         = "purchasableItem";

    public static final String PURCHASE_TYPE            = "purchaseType";
    public static final String PURCHASE_TYPE_MARKET     = "market";
    public static final String PURCHASE_TYPE_VI         = "virtualItem";

    public static final String PURCHASE_MARKET_ITEM     = "marketItem";

    public static final String PURCHASE_VI_ITEMID       = "pvi_itemId";
    public static final String PURCHASE_VI_AMOUNT       = "pvi_amount";


}


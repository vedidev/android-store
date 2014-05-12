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


package com.soomla.example;

import android.content.res.AssetManager;

import com.soomla.store.IStoreAssets;
import com.soomla.store.SoomlaApp;
import com.soomla.store.domain.*;
import com.soomla.store.domain.virtualCurrencies.*;
import com.soomla.store.domain.virtualGoods.*;
import com.soomla.store.purchaseTypes.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class defines our game's economy model, which includes virtual goods, virtual currencies
 * and currency packs, virtual categories, and non-consumable items.
 */
public class MuffinRushAssets implements IStoreAssets {

    /**
     *
     * @return version of <code>MuffinRushAssets</code>
     */
    @Override
    public int getVersion() {
        return 0;
    }

    /**
     *
     * @return virtual currencies
     */
    @Override
    public VirtualCurrency[] getCurrencies(){
        return  new VirtualCurrency[] {
                MUFFIN_CURRENCY
        };
    }

    /**
     *
     * @return virtual goods
     */
    @Override
    public VirtualGood[] getGoods(){
        return new VirtualGood[] {
                MUFFINCAKE_GOOD, PAVLOVA_GOOD,
                CHOCLATECAKE_GOOD, CREAMCUP_GOOD
        };
    }

    /**
     *
     * @return virtual currency packs
     */
    @Override
    public VirtualCurrencyPack[] getCurrencyPacks(){
        return new VirtualCurrencyPack[] {
                TENMUFF_PACK, FIFTYMUFF_PACK, FOURHUNDMUFF_PACK, THOUSANDMUFF_PACK
        };
    }

    /**
     *
     * @return virtual categories
     */
    @Override
    public VirtualCategory[] getCategories() {
        return new VirtualCategory[]{
                GENERAL_CATEGORY
        };
    }

    /**
     *
     * @return non consumable items
     */
    @Override
    public NonConsumableItem[] getNonConsumableItems() {
        final NonConsumableItem[] nonConsumableItems = readCsvNCItems("test_android_iap_import.csv");
        if (nonConsumableItems == null || nonConsumableItems.length < 1) {
            return new NonConsumableItem[]{
                    NO_ADDS_NONCONS
            };
        }

        return nonConsumableItems;
    }

    /**
     *
     * @param csvFile - exported from Google Developer Console for format
     *                and appended to as needed. save in assets.
     * @return
     */
    private NonConsumableItem[] readCsvNCItems(String csvFile) {
        List<NonConsumableItem> nonConsumableItems = new ArrayList<NonConsumableItem>();
        AssetManager assetManager = SoomlaApp.getAppContext().getAssets();
        String contents = "";

        try {
            final InputStream stream = assetManager.open(csvFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            contents = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String[] lines = contents.split("\n");
        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            // skip first line
            if (lineNumber == 1) {
                continue;
            }

            String[] tokens = line.split("[,;]");
            String prodId = tokens[0];
            String name = tokens[5];
            String desc = tokens[6];
            String price = tokens[9].trim().substring(0, 1);//hacky, but fast for now
            final PurchaseWithMarket purchaseWithMarket = new PurchaseWithMarket(
                    new MarketItem(prodId, MarketItem.Managed.MANAGED, Double.valueOf(price)));

            NonConsumableItem item = new NonConsumableItem(name, desc, prodId, purchaseWithMarket);
            nonConsumableItems.add(item);
        }

        return nonConsumableItems.toArray(new NonConsumableItem[nonConsumableItems.size()]);
    }

    /** Static Final Members **/

    public static final String MUFFIN_CURRENCY_ITEM_ID      = "currency_muffin";

    public static final String MUFFINCAKE_ITEM_ID           = "fruit_cake";

    public static final String PAVLOVA_ITEM_ID              = "pavlova";

    public static final String CHOCLATECAKE_ITEM_ID         = "chocolate_cake";

    public static final String CREAMCUP_ITEM_ID             = "cream_cup";

    public static final String TENMUFF_PACK_PRODUCT_ID      = "android.test.refunded";

    public static final String FIFTYMUFF_PACK_PRODUCT_ID    = "android.test.canceled";

    public static final String FOURHUNDMUFF_PACK_PRODUCT_ID = "android.test.purchased";

    public static final String THOUSANDMUFF_PACK_PRODUCT_ID = "android.test.item_unavailable";

    public static final String NO_ADDS_NONCONS_PRODUCT_ID   = "no_ads";


    /** Virtual Currencies **/

    public static final VirtualCurrency MUFFIN_CURRENCY = new VirtualCurrency(
            "Muffins",                                  // name
            "",                                         // description
            MUFFIN_CURRENCY_ITEM_ID                     // item id
    );


    /** Virtual Currency Packs **/

    public static final VirtualCurrencyPack TENMUFF_PACK = new VirtualCurrencyPack(
            "10 Muffins",                               // name
            "Test refund of an item",                   // description
            "muffins_10",                               // item id
            10,                                         // number of currencies in the pack
            MUFFIN_CURRENCY_ITEM_ID,                    // the currency associated with this pack
            new PurchaseWithMarket(TENMUFF_PACK_PRODUCT_ID, 0.99));

    public static final VirtualCurrencyPack FIFTYMUFF_PACK = new VirtualCurrencyPack(
            "50 Muffins",                               // name
            "Test cancellation of an item",             // description
            "muffins_50",                               // item id
            50,                                         // number of currencies in the pack
            MUFFIN_CURRENCY_ITEM_ID,                    // the currency associated with this pack
            new PurchaseWithMarket(FIFTYMUFF_PACK_PRODUCT_ID, 1.99) // purchase type
    );

    public static final VirtualCurrencyPack FOURHUNDMUFF_PACK = new VirtualCurrencyPack(
            "400 Muffins",                              // name
            "Test purchase of an item",                 // description
            "muffins_400",                              // item id
            400,                                        // number of currencies in the pack
            MUFFIN_CURRENCY_ITEM_ID,                    // the currency associated with this pack
            new PurchaseWithMarket(FOURHUNDMUFF_PACK_PRODUCT_ID, 4.99) // purchase type
    );

    public static final VirtualCurrencyPack THOUSANDMUFF_PACK = new VirtualCurrencyPack(
            "1000 Muffins",                             // name
            "Test item unavailable",                    // description
            "muffins_1000",                             // item id
            1000,                                       // number of currencies in the pack
            MUFFIN_CURRENCY_ITEM_ID,                    // the currency associated with this pack
            new PurchaseWithMarket(THOUSANDMUFF_PACK_PRODUCT_ID, 8.99) // purchase type
    );


    /** Virtual Goods **/

    public static final VirtualGood MUFFINCAKE_GOOD = new SingleUseVG(
            "Fruit Cake",                                                   // name
            "Customers buy a double portion on each purchase of this cake", // description
            "fruit_cake",                                                   // item id
            new PurchaseWithVirtualItem(MUFFIN_CURRENCY_ITEM_ID, 225)       // purchase type
    );

    public static final VirtualGood PAVLOVA_GOOD = new SingleUseVG(
            "Pavlova",                                                      // name
            "Gives customers a sugar rush and they call their friends",     // description
            "pavlova",                                                      // item id
            new PurchaseWithVirtualItem(MUFFIN_CURRENCY_ITEM_ID, 175)       // purchase type
    );

    public static final VirtualGood CHOCLATECAKE_GOOD = new SingleUseVG(
            "Chocolate Cake",                                               // name
            "A classic cake to maximize customer satisfaction",             // description
            "chocolate_cake",                                               // item id
            new PurchaseWithVirtualItem(MUFFIN_CURRENCY_ITEM_ID, 250)       // purchase type
    );

    public static final VirtualGood CREAMCUP_GOOD = new SingleUseVG(
            "Cream Cup",                                                    // name
            "Increase bakery reputation with this original pastry",         // description
            "cream_cup",                                                    // item id
            new PurchaseWithVirtualItem(MUFFIN_CURRENCY_ITEM_ID, 50)        // purchase type
    );


    /** Virtual Categories **/

    // The Muffin Rush theme doesn't support categories, so we just put everything under a general
    // category.
    public static final VirtualCategory GENERAL_CATEGORY = new VirtualCategory(
            "General", new ArrayList<String>(Arrays.asList(new String[]
            { MUFFINCAKE_ITEM_ID, PAVLOVA_ITEM_ID, CHOCLATECAKE_ITEM_ID, CREAMCUP_ITEM_ID }))
    );


    /** Market Non Consumable (MANAGED) Items **/

    public static final NonConsumableItem NO_ADDS_NONCONS  = new NonConsumableItem(
            "No Ads",                                                       // name
            "Test purchase of MANAGED item.",                               // description
            "no_ads",                                                       // item id
            new PurchaseWithMarket(new MarketItem(
                   NO_ADDS_NONCONS_PRODUCT_ID, MarketItem.Managed.MANAGED , 1.99)) // purchase type
    );

}

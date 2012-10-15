package com.soomla.example.templeRun;

import com.soomla.store.IStoreAssets;
import com.soomla.store.domain.data.*;
import java.util.HashMap;

public class TempleRunAssets implements IStoreAssets {

    @Override
    public VirtualCurrency[] getVirtualCurrencies(){
        return  new VirtualCurrency[] {
            COINS_CURRENCY
        };
    }

    @Override
    public VirtualGood[] getVirtualGoods(){
        return new VirtualGood[] {
            BOOST_AHEAD_GOOD, EXTRA_HEALTH_GOOD, INVISIBILITY_HAT_GOOD, HORN_OF_WAR_GOOD, FLAGSHIP_GOOD, MYSTERY_BOX_GOOD, WISDOM_GOD_GOOD, WIND_GOD_GOOD, WINTER_GOD_GOOD, STARS_GOD_GOOD, ARGRICULTURE_GOD_GOOD, EAGLE_GOOD, BUTTERFLY_GOOD, FISH_GOOD
        };
    }

    @Override
    public VirtualCurrencyPack[] getVirtualCurrencyPacks(){
        return new VirtualCurrencyPack[] {
            _2_500_COINS_PACK, _25_000_COINS_PACK, _75_000_COINS_PACK, _200_000_COINS_PACK
        };
    }

    @Override
    public VirtualCategory[] getVirtualCategories() {
        return new VirtualCategory[]{
            POWERUPS_CATEGORY, UTILITIES_CATEGORY, GODS_CATEGORY, FRIENDS_CATEGORY
        };
    }


    /** Static Final members **/
    
    public static final String COINS_CURRENCY_ITEM_ID = "currency_muffin";
 
    public static final String BOOST_AHEAD_GOOD_ITEM_ID = "boost_ahead";
    public static final String EXTRA_HEALTH_GOOD_ITEM_ID = "extra_health";
    public static final String INVISIBILITY_HAT_GOOD_ITEM_ID = "invisibility_hat";
    public static final String HORN_OF_WAR_GOOD_ITEM_ID = "horn_of_war";
    public static final String FLAGSHIP_GOOD_ITEM_ID = "flagship";
    public static final String MYSTERY_BOX_GOOD_ITEM_ID = "mystery_box";
    public static final String WISDOM_GOD_GOOD_ITEM_ID = "science_god";
    public static final String WIND_GOD_GOOD_ITEM_ID = "wind_god";
    public static final String WINTER_GOD_GOOD_ITEM_ID = "winter_god";
    public static final String STARS_GOD_GOOD_ITEM_ID = "stars_god";
    public static final String ARGRICULTURE_GOD_GOOD_ITEM_ID = "chocolate_cake";
    public static final String EAGLE_GOOD_ITEM_ID = "eagle";
    public static final String BUTTERFLY_GOOD_ITEM_ID = "butterfly";
    public static final String FISH_GOOD_ITEM_ID = "fish";
 
    public static final String _2_500_COINS_PACK_ITEM_ID = "muffins_10";
    public static final String _25_000_COINS_PACK_ITEM_ID = "muffins_50";
    public static final String _75_000_COINS_PACK_ITEM_ID = "muffins_400";
    public static final String _200_000_COINS_PACK_ITEM_ID = "muffins_1000";
 
    public static final String _2_500_COINS_PACK_PRODUCT_ID = "2500_coins";
    public static final String _25_000_COINS_PACK_PRODUCT_ID = "android.test.purchased";
    public static final String _75_000_COINS_PACK_PRODUCT_ID = "75000_coins";
    public static final String _200_000_COINS_PACK_PRODUCT_ID = "200000_coins";


    /** Virtual Categories **/

    
    public static final VirtualCategory POWERUPS_CATEGORY = new VirtualCategory(
             "POWERUPS", // name
             1, // id
             ""); // title
    public static final VirtualCategory UTILITIES_CATEGORY = new VirtualCategory(
             "UTILITIES", // name
             2, // id
             ""); // title
    public static final VirtualCategory GODS_CATEGORY = new VirtualCategory(
             "GODS", // name
             3, // id
             ""); // title
    public static final VirtualCategory FRIENDS_CATEGORY = new VirtualCategory(
             "FRIENDS", // name
             4, // id
             ""); // title


    /** Virtual Currencies **/

    
    public static final VirtualCurrency COINS_CURRENCY = new VirtualCurrency(
             "Coins", // name
             "", // description
             COINS_CURRENCY_ITEM_ID); // item id


    /** Virtual Goods **/

    
    private static final HashMap<String, Integer> BOOST_AHEAD_PRICE = new HashMap<String, Integer>();
    static {
        BOOST_AHEAD_PRICE.put(COINS_CURRENCY_ITEM_ID, 250);
    }
    public static final VirtualGood BOOST_AHEAD_GOOD = new VirtualGood(
             "Boost Ahead", // name
             "Get a head start of 100 miles", // description
             new StaticPriceModel(BOOST_AHEAD_PRICE), // price
             BOOST_AHEAD_GOOD_ITEM_ID, // item id
             POWERUPS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> EXTRA_HEALTH_PRICE = new HashMap<String, Integer>();
    static {
        EXTRA_HEALTH_PRICE.put(COINS_CURRENCY_ITEM_ID, 500);
    }
    public static final VirtualGood EXTRA_HEALTH_GOOD = new VirtualGood(
             "Extra Health", // name
             "Find hearts in the game to recover your health points", // description
             new StaticPriceModel(EXTRA_HEALTH_PRICE), // price
             EXTRA_HEALTH_GOOD_ITEM_ID, // item id
             POWERUPS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> INVISIBILITY_HAT_PRICE = new HashMap<String, Integer>();
    static {
        INVISIBILITY_HAT_PRICE.put(COINS_CURRENCY_ITEM_ID, 750);
    }
    public static final VirtualGood INVISIBILITY_HAT_GOOD = new VirtualGood(
             "Invisibility Hat", // name
             "Find the invisibility hat to suprise your enemies", // description
             new StaticPriceModel(INVISIBILITY_HAT_PRICE), // price
             INVISIBILITY_HAT_GOOD_ITEM_ID, // item id
             POWERUPS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> HORN_OF_WAR_PRICE = new HashMap<String, Integer>();
    static {
        HORN_OF_WAR_PRICE.put(COINS_CURRENCY_ITEM_ID, 500);
    }
    public static final VirtualGood HORN_OF_WAR_GOOD = new VirtualGood(
             "Horn of War", // name
             "Pass the word to your fleet quicker", // description
             new StaticPriceModel(HORN_OF_WAR_PRICE), // price
             HORN_OF_WAR_GOOD_ITEM_ID, // item id
             UTILITIES_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> FLAGSHIP_PRICE = new HashMap<String, Integer>();
    static {
        FLAGSHIP_PRICE.put(COINS_CURRENCY_ITEM_ID, 1000);
    }
    public static final VirtualGood FLAGSHIP_GOOD = new VirtualGood(
             "Flagship", // name
             "Use the power of the wind to move faster", // description
             new StaticPriceModel(FLAGSHIP_PRICE), // price
             FLAGSHIP_GOOD_ITEM_ID, // item id
             UTILITIES_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> MYSTERY_BOX_PRICE = new HashMap<String, Integer>();
    static {
        MYSTERY_BOX_PRICE.put(COINS_CURRENCY_ITEM_ID, 2000);
    }
    public static final VirtualGood MYSTERY_BOX_GOOD = new VirtualGood(
             "Mystery Box", // name
             "Special utility that will be revealed once you open the box", // description
             new StaticPriceModel(MYSTERY_BOX_PRICE), // price
             MYSTERY_BOX_GOOD_ITEM_ID, // item id
             UTILITIES_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> WISDOM_GOD_PRICE = new HashMap<String, Integer>();
    static {
        WISDOM_GOD_PRICE.put(COINS_CURRENCY_ITEM_ID, 10000);
    }
    public static final VirtualGood WISDOM_GOD_GOOD = new VirtualGood(
             "Wisdom God", // name
             "Fight in the name of Wisdom God and get more powerful weapons", // description
             new StaticPriceModel(WISDOM_GOD_PRICE), // price
             WISDOM_GOD_GOOD_ITEM_ID, // item id
             GODS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> WIND_GOD_PRICE = new HashMap<String, Integer>();
    static {
        WIND_GOD_PRICE.put(COINS_CURRENCY_ITEM_ID, 10000);
    }
    public static final VirtualGood WIND_GOD_GOOD = new VirtualGood(
             "Wind God", // name
             "Fight in the name of the Wind God and the wind will always be at your side", // description
             new StaticPriceModel(WIND_GOD_PRICE), // price
             WIND_GOD_GOOD_ITEM_ID, // item id
             GODS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> WINTER_GOD_PRICE = new HashMap<String, Integer>();
    static {
        WINTER_GOD_PRICE.put(COINS_CURRENCY_ITEM_ID, 10000);
    }
    public static final VirtualGood WINTER_GOD_GOOD = new VirtualGood(
             "Winter God", // name
             "Fight in the name of the Winter God and avoid glaciers and frost", // description
             new StaticPriceModel(WINTER_GOD_PRICE), // price
             WINTER_GOD_GOOD_ITEM_ID, // item id
             GODS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> STARS_GOD_PRICE = new HashMap<String, Integer>();
    static {
        STARS_GOD_PRICE.put(COINS_CURRENCY_ITEM_ID, 10000);
    }
    public static final VirtualGood STARS_GOD_GOOD = new VirtualGood(
             "Stars God", // name
             "Fight in the name of the Stars God and never be lost", // description
             new StaticPriceModel(STARS_GOD_PRICE), // price
             STARS_GOD_GOOD_ITEM_ID, // item id
             GODS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> ARGRICULTURE_GOD_PRICE = new HashMap<String, Integer>();
    static {
        ARGRICULTURE_GOD_PRICE.put(COINS_CURRENCY_ITEM_ID, 10000);
    }
    public static final VirtualGood ARGRICULTURE_GOD_GOOD = new VirtualGood(
             "Argriculture God", // name
             "Fight in the name of Agriculture God and never run out of food", // description
             new StaticPriceModel(ARGRICULTURE_GOD_PRICE), // price
             ARGRICULTURE_GOD_GOOD_ITEM_ID, // item id
             GODS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> EAGLE_PRICE = new HashMap<String, Integer>();
    static {
        EAGLE_PRICE.put(COINS_CURRENCY_ITEM_ID, 5000);
    }
    public static final VirtualGood EAGLE_GOOD = new VirtualGood(
             "Eagle", // name
             "The eagle can tell you about enemies ahead of time up to 100 miles away", // description
             new StaticPriceModel(EAGLE_PRICE), // price
             EAGLE_GOOD_ITEM_ID, // item id
             FRIENDS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> BUTTERFLY_PRICE = new HashMap<String, Integer>();
    static {
        BUTTERFLY_PRICE.put(COINS_CURRENCY_ITEM_ID, 5000);
    }
    public static final VirtualGood BUTTERFLY_GOOD = new VirtualGood(
             "Butterfly", // name
             "The butterfly can help you control your crew and protect against mutiny", // description
             new StaticPriceModel(BUTTERFLY_PRICE), // price
             BUTTERFLY_GOOD_ITEM_ID, // item id
             FRIENDS_CATEGORY, // category
             false);
    
    private static final HashMap<String, Integer> FISH_PRICE = new HashMap<String, Integer>();
    static {
        FISH_PRICE.put(COINS_CURRENCY_ITEM_ID, 5000);
    }
    public static final VirtualGood FISH_GOOD = new VirtualGood(
             "Fish", // name
             "Fish can help you trick your enemy into a trap", // description
             new StaticPriceModel(FISH_PRICE), // price
             FISH_GOOD_ITEM_ID, // item id
             FRIENDS_CATEGORY, // category
             false);
    

    /** Virtual Currency Packs **/
    
    public static final VirtualCurrencyPack _2_500_COINS_PACK = new VirtualCurrencyPack(
             "2,500 Coins", // name
             "", // description
             _2_500_COINS_PACK_ITEM_ID, // item id
             _2_500_COINS_PACK_PRODUCT_ID, // product id in Google Market
             0.99, // actual price in $$
             2500, // number of currencies in the pack
             COINS_CURRENCY); // the associated currency
    
    public static final VirtualCurrencyPack _25_000_COINS_PACK = new VirtualCurrencyPack(
             "25,000 Coins", // name
             "", // description
             _25_000_COINS_PACK_ITEM_ID, // item id
             _25_000_COINS_PACK_PRODUCT_ID, // product id in Google Market
             4.99, // actual price in $$
             25000, // number of currencies in the pack
             COINS_CURRENCY); // the associated currency
    
    public static final VirtualCurrencyPack _75_000_COINS_PACK = new VirtualCurrencyPack(
             "75,000 Coins", // name
             "", // description
             _75_000_COINS_PACK_ITEM_ID, // item id
             _75_000_COINS_PACK_PRODUCT_ID, // product id in Google Market
             9.99, // actual price in $$
             75000, // number of currencies in the pack
             COINS_CURRENCY); // the associated currency
    
    public static final VirtualCurrencyPack _200_000_COINS_PACK = new VirtualCurrencyPack(
             "200,000 Coins", // name
             "", // description
             _200_000_COINS_PACK_ITEM_ID, // item id
             _200_000_COINS_PACK_PRODUCT_ID, // product id in Google Market
             19.99, // actual price in $$
             200000, // number of currencies in the pack
             COINS_CURRENCY); // the associated currency
    

}
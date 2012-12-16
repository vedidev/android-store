*This project is a part of [The SOOMLA Project](http://project.soom.la) which is a series of open source initiatives with a joint goal to help mobile game developers get better stores and more in-app purchases.*

Didn't you ever wanted an in-app purchase one liner that looks like this ?!

```Java
    StoreController.getInstance().buyCurrencyPack("[Product id here]");
```

android-store
---

The android-store is our first open code initiative as part of The SOOMLA Project. It is a Java API that simplifies Google Play's in-app purchasing API and complements it with storage, security and event handling. The project also includes a sample app for reference. 

>If you also want to create a **storefront** you can do that using our [Store Designer](designer.soom.la).


Check out our [Wiki] (https://github.com/soomla/android-store/wiki) for more information about the project and how to use it better.

Getting Started
---
* Before doing anything, SOOMLA recommends that you go through [Android In-app Billing](http://developer.android.com/guide/google/play/billing/index.html).

1. Clone android-store. Copy all files from android-store/SoomlaAndroidStore subfolders to their equivalent folders in your Android project:

 `git clone git@github.com:soomla/android-store.git`

2. Make the following changes to your AndroidManifest.xml:

  Add SoomlaApp as the main Application by placing it in the 'application' tag:

    ```xml
    <application ...
                 android:name="com.soomla.store.SoomlaApp">
    ```

  Add the following permission:

    ```xml
    <uses-permission android:name="com.android.vending.BILLING" />
    ```

  Add the following code into your 'application' element:

    ```xml
    <service android:name="com.soomla.billing.BillingService" />

    <receiver android:name="com.soomla.billing.BillingReceiver">
        <intent-filter>
            <action android:name="com.android.vending.billing.IN_APP_NOTIFY" />
            <action android:name="com.android.vending.billing.RESPONSE_CODE" />
            <action android:name="com.android.vending.billing.PURCHASE_STATE_CHANGED" />
        </intent-filter>
    </receiver>
    ```
    
3. Create your own implementation of _IStoreAssets_ in order to describe your specific game's assets ([example](https://github.com/soomla/android-store/blob/master/SoomlaAndroidExample/src/com/soomla/example/MuffinRushAssets.java)). Initialize _StoreController_ with the class you just created:

      ```Java
       StoreController.getInstance().initialize(new YourStoreAssetsImplementation(),
                                           "[YOUR PUBLIC KEY FROM GOOGLE PLAY]",
                                           "[YOUR CUSTOM GAME SECRET HERE]");
      ```
> The custom secret is your encryption secret data saved in the DB.
> Initialize StoreController ONLY ONCE when your application loads.

4. Now, that you have _StoreController_ loaded, just decide when you want to show/hide your store's UI to the user and let _StoreController_ know about it:

  When you show the store call:

    ```Java
    StoreController.getInstance().storeOpening([your application context], [a handler you just created]);
    ```

  When you hide the store call:

    ```Java
    StoreController.getInstance().storeClosing();
    ```

And that's it ! You have storage and in-app purchasing capabilities... ALL-IN-ONE.


What's next? In App Purchasing.
---

android-store provides you with VirtualCurrencyPacks. VirtualCurrencyPack is a representation of a "bag" of currencies that you want to let your users purchase in Google Play. You define VirtualCurrencyPacks in your game specific assets file which is your implemetation of IStoreAssets ([example](https://github.com/soomla/android-store/blob/master/SoomlaAndroidExample/src/com/soomla/example/MuffinRushAssets.java)). After you do that you can call StoreController to make actual purchases and android-store will take care of the rest.

Example:

Lets say you have a _VirtualCurrencyPack_ you call `TEN_COINS_PACK`, a _VirtualCurrency_ you call `COIN_CURRENCY` and a _VirtualCategory_ you call `CURRENCYPACKS_CATEGORY`:


```Java
VirtualCurrencyPack TEN_COINS_PACK = new VirtualCurrencyPack(
        "10 Coins",                // name
        "A pack of 10 coins",      // description
        "10_coins",                // item id
        TEN_COINS_PACK_PRODUCT_ID, // product id in Google Market
        1.99,                      // actual price in $$
        10,                        // number of currencies in the pack
        COIN_CURRENCY);            // the associated currency
```
     
Now you can use _StoreController_ to call Google Play's in-app purchasing mechanism:

```Java
StoreController.getInstance().buyCurrencyPack(TEN_COINS_PACK.getProductId());
```
    
And that's it! android-store knows how to contact Google Play for you and redirect the user to the purchasing mechanis.
Don't forget to define your _IStoreEventHandler_ in order to get the events of successful or failed purchase (see [Event Handling](https://github.com/soomla/android-store#event-handling)).


Storage & Meta-Data
---

When you initialize _StoreController_, it automatically initializes two other classes: StorageManager and StoreInfo. _StorageManager_ is the father of all stoaage related instances in your game. Use it to access tha balances of virtual currencies and virtual goods (ususally, using their itemIds). _StoreInfo_ is the mother of all meta data information about your specific game. It is initialized with your implementation of IStoreAssets and you can use it to retrieve information about your specific game.

The on-device storage is encrypted and kept in a SQLite database. SOOMLA is preparing a cloud-based storage service that'll allow this SQLite to be synced to a cloud-based repository that you'll define.

**Example Usages**

* Add 10 coins to the virtual currency with itemId "currency_coin":

    ```Java
    VirtualCurrency coin = StoreInfo.getVirtualCurrencyByItemId("currency_coin");
    StorageManager.getVirtualCurrencyStorage().add(coin, 10);
    ```
    
* Remove 10 virtual goods with itemId "green_hat":

    ```Java
    VirtualGood greenHat = StoreInfo.getVirtualGoodByItemId("green_hat");
    StorageManager.getVirtualGoodsStorage().remove(greenHat, 10);
    ```
    
* Get the current balance of green hats (virtual goods with itemId "green_hat"):

    ```Java
    VirtualGood greenHat = StoreInfo.getVirtualGoodByItemId("green_hat");
    int greenHatsBalance = StorageManager.getVirtualGoodsStorage().getBalance(greenHat);
    ```
    
Security
---

If you want to protect your application from 'bad people' (and who doesn't?!), you might want to follow some guidelines:

+ SOOMLA keeps the game's data in an encrypted database. In order to encrypt your data, SOOMLA generates a private key out of several parts of information. The Custom Secret is one of them. SOOMLA recommends that you provide this value when initializing StoreController and before you release your game. BE CAREFUL: You can change this value once! If you try to change it again, old data from the database will become unavailable.
+ Following Google's recommendation, SOOMLA also recommends that you split your public key and construct it on runtime or even use bit manipulation on it in order to hide it. The key itself is not secret information but if someone replaces it, your application might get fake messages that might harm it.

Event Handling
---

SOOMLA lets you create your own event handler and add it to StoreEventHandlers. That way you'll be able to get notifications on various events and implement your own application specific behaviour to those events.

> Your behaviour is an addition to the default behaviour implemented by SOOMLA. You don't replace SOOMLA's behaviour.

In order to create your event handler:

1. create a class that implements IStoreEventHandler.
2. Add the created class to StoreEventHandlers:
 `StoreEventHandlers.getInstance().addEventHandler(new YourEventHandler());`

Contribution
---

We want you!

Fork -> Clone -> Implement -> Test -> Pull-Request. We have great RESPECT for contributors.

SOOMLA, Elsewhere ...
---

+ [Website](http://project.soom.la/)
+ [On Facebook](https://www.facebook.com/pages/The-SOOMLA-Project/389643294427376).
+ [On AngelList](https://angel.co/the-soomla-project)

License
---
MIT License. Copyright (c) 2012 SOOMLA. http://project.soom.la
+ http://www.opensource.org/licenses/MIT


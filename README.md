*This project is a part of [The SOOMLA Project](http://project.soom.la) which is a series of open source initiatives with a joint goal to help mobile game developers get better stores and more in-app purchases.*

android-store
---
The android-store is our first open code initiative as part of The SOOMLA Project. It is a Java API that simplifies Google Play's in-app purchasing API and complements it with storage, security and event handling. The project also includes a sample app for reference. As an optional (and currently EXPERIMENTAL) part of our open-source projects you can also get the store's layout which you can customize with your own game's assets. To use our storefront, refer to [Get your own Storefront](https://github.com/soomla/android-store/wiki/Get-your-own-Storefront-%5BEXPERIMENTAL%5D).


Check out our [Wiki] (https://github.com/soomla/android-store/wiki) for more information about the project and how to use it better.

Getting Started
---
* Before doing anything, SOOMLA recommends that you go through [Android In-app Billing](http://developer.android.com/guide/google/play/billing/index.html).

1. Clone android-store. Copy all files from android-store's subfolders to their equivallent folders in your Android project:

 `git clone git@github.com:soomla/android-store.git`

2. Make the folowing changes to your AndroidManifest.xml:

  Add the following permission:

    ```xml
    <uses-permission android:name="com.android.vending.BILLING" />
    ```

  Add the following code to your 'application' element:

    ```xml
    <service android:name="com.soomla.billing.BillingService" />

    <receiver android:name="com.soomla.billing.BillingReceiver">
        <intent-filter>
            <action android:name="com.android.vending.billing.IN_APP_NOTIFY" />
            <action android:name="com.android.vending.billing.RESPONSE_CODE" />
            <action android:name="com.android.vending.billing.PURCHASE_STATE_CHANGED" />
        </intent-filter>
    </receiver>
    <activity android:name="com.soomla.store.StoreActivity" />
    ```
    
3. Create your own implementation of _IStoreAssets_ in order to describe your specific game's assets. Initialize _StoreController_ with the class you just created:

      ```Java
       StoreController.getInstance().initialize(getApplicationContext(), 
                                           new YourStoreAssetsImplementation(),
                                           "YOUR PUBLIC KEY FROM GOOGLE PLAY",
                                           false);
      ```

And that's it ! You have Storage and in-app purchesing capabilities... ALL-IN-ONE.

Storage & Meta-Data
---

When you initialize _StoreController_, it automatically initializes two other classed: StorageManager and StoreInfo. _StorageManager_ is the father of all stoaage related instances in your game. Use it to access tha balances of virtual currencies and virtual goods (ususally, using their itemIds). _StoreInfo_ is the mother of all meta data information about your specific game. It is initialized with your implementation of IStoreAssets and you can use it to retrieve information about your specific game.

The on-device storage is encrypted and kept in a SQLite database. SOOMLA is preparing a cloud-based storage service that'll allow this SQLite to be synced to a cloud-based repository that you'll define. Stay tuned... this is just one of the goodies we prepare for you.

**Example Usages**

* Add 10 coins to the virtual currency with itemId "currency_coin":

    ```Java
    VirtualCurrency coin = StoreInfo.getInstance().getVirtualCurrencyByItemId("currency_coin");
    StorageManager.getInstance().getVirtualCurrencyStorage().add(coin, 10);
    ```
    
* Remove 10 virtual goods with itemId "green_hat":

    ```Java
    VirtualGood greenHat = StoreInfo.getInstance().getVirtualGoodByItemId("green_hat");
    StorageManager.getInstance().getVirtualGoodsStorage().remove(greenHat, 10);
    ```
    
* Get the current balance of green hats (virtual goods with itemId "green_hat"):

    ```Java
    VirtualGood greenHat = StoreInfo.getInstance().getVirtualGoodByItemId("green_hat");
    int greenHatsBalance = StorageManager.getInstance().getVirtualGoodsStorage().getBalance(greenHat);
    ```
    
Security
---

If you want to protect your application from 'bad people' (and who doesn't?!), you might want to follow some guidelines:

+ SOOMLA keeps the game's data in an encrypted database. In order to encrypt your data, SOOMLA generates a private key out of several parts of information. StoreInfo.customSecret is one of them. SOOMLA recommends that you change this value before you release your game. BE CAREFUL: You can always change this value once! If you try to change it again, old data from the database will become unavailable.
+ Following Google's recommendation, SOOMLA also recommends that you split your public key and construct it on runtime or even use bit manipulation on it in order to hide it. The key itself is not secret information but if someone replaces it, your application might get fake messages that might harm it.

Event Handling
---

SOOMLA lets you create your own event handler and add it to StoreEventHandlers. That way you'll be able to get notifications on various events and implement your own application specific behaviour to those events.

NOTE: Your behaviour is an addition to the default behaviour implemented by SOOMLA. You don't replace SOOMLA's behaviour.

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


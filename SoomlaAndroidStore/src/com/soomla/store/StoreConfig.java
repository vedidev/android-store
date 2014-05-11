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

package com.soomla.store;

import com.soomla.store.billing.IIabService;
import com.soomla.store.billing.google.GooglePlayIabService;

/**
 * This class holds the store's configurations.
 *
 * IMPORTANT: Change <code>SOOM_SEC</code> below !!!
 * IMPORTANT: Change <code>obfuscationSalt</code> below !!!
 * IMPORTANT: Before releasing your game, set <code>DB_DELETE</code> to false !!!
 * IMPORTANT: Before releasing your game, set <code>AllowAndroidTestPurchases</code> to false !!!
 */
public class StoreConfig {

    /**
     * Select your in-app billing service.
     * The default is Google Play using https://github.com/soomla/android-store-google-play
     * If this value is left empty, you will not be able to release your game to the market.
     */
    public static final IIabService InAppBillingService = new GooglePlayIabService();

    //CHANGE THIS SECRET!!!
    public static String SOOM_SEC = "SINC_SSEEKK";

    //Set to true if you want to print out debug messages
    public static final boolean logDebug = false;

    // A friendlyRefunds tells android-store if to let your refunded users keep their VirtualItems
    // after a refund or not. (default: false)
    public static final boolean friendlyRefunds = false;

    /**
     * The obfuscated salt: randomly generated numbers.
     * IMPORTANT: it's recommended that you change these numbers for your specific application,
     * BUT change them only once!
     */
    public static final byte[] obfuscationSalt = new byte[] { 64, -54, -113, -47, 98, -52, 87,
            -102, -65, -127, 89, 51, -11, -35, 30, 77, -45, 75, -26, 3 };

    /**
     * ---NEVER!--- CHANGE THE VALUE FOR THIS VARIABLE !!!
     * This value defines the version of the metadata located in your database.
     */
    public static final int METADATA_VERSION = 3;

    /**
     * If this is true then the database will be deleted whenever the application loads.
     *
     * WARNING: Do NOT release your game with this option set to true!
     * Otherwise, your users will lose all their data every time they load the application.
     *
     * NOTE: this feature can be useful for testing when you want to change stuff in your
     * implementation of <code>IStoreAssets</code> and see what they look like. If you try to
     * change things in <code>IStoreAssets</code> and don't delete the DB then your changes will
     * not be shown.
     */
    public static final boolean DB_DELETE = false;

    /** Shared Preferences **/
    public static final String PREFS_NAME      = "store.prefs";
    public static final String DB_INITIALIZED  = "db_initialized";
    public static final String PUBLIC_KEY      = "PO#SU#SO#GU";
    public static final String CUSTOM_SEC      = "SU#LL#SE#RE";

    /**
     * When set to true, this removes the need to verify purchases when there's no signature.
     * This is useful while you are in development and testing stages of your game.
     *
     * WARNING: Do NOT publish your app with this set to true!!!
     */
    public static boolean AllowAndroidTestPurchases = false;
}

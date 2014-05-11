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

import android.provider.Settings;
import android.util.Log;

/**
 * This class provides Log functions that output debug, warning, or error messages.
 */
public class StoreUtils {

    /**
     * Creates Log Debug message according to given tag and message.
     *
     * @param tag the name of the class whose instance called this function
     * @param message debug message to output to log
     */
    public static void LogDebug(String tag, String message) {
        if (StoreConfig.logDebug) {
            Log.d(tag, message);
        }
    }

    /**
     * Creates Log Warning message according to given tag and message.
     *
     * @param tag the name of the class whose instance called this function
     * @param message warning message to output to log
     */
    public static void LogWarning(String tag, String message) {
        Log.w(tag, message);
    }

    /**
     * Creates Log Error message according to given tag and message.
     *
     * @param tag the name of the class whose instance called this function
     * @param message error message to output to log
     */
    public static void LogError(String tag, String message) {
        Log.e(tag, message);
    }

    /**
     * Retrieves Android device Id.
     *
     * @return androidId which is the id of the device being used
     */
    public static String deviceId() {
        String androidId = Settings.Secure.getString(SoomlaApp.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (androidId == null) {
            // This is a fallback in case the device id cannot be retrieved on the device
            // (happened on some devices !)
            StoreUtils.LogError("SOOMLA ObscuredSharedPreferences",
                    "Couldn't fetch ANDROID_ID. Using fake id.");
            androidId = "SOOMFAKE";
        }

        return androidId;
    }


    /** Private Members **/

    private static String TAG = "SOOMLA StoreUtils"; //used for Log messages
}

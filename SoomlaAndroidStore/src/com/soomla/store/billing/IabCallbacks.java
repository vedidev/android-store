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

package com.soomla.store.billing;

 import java.util.List;

 /**
  * A utility class that defines interfaces for passing callbacks to in-app billing events.
  */
public class IabCallbacks {

     /**
      * Listens for in-app billing service initialization
      */
    public interface IabInitListener {

         /**
          * Performs the following function upon success.
          *
          * @param alreadyInBg true if the listener has already been initialized and is in
          *                    background, false otherwise.
          */
        public void success(boolean alreadyInBg);

         /**
          * Performs the following function upon failure and prints the given message.
          *
          * @param message reason for failure
          */
        public void fail(String message);
    }

     /**
      * Listens for in-app purchases being made
      */
    public interface OnPurchaseListener {

         /**
          * The user has successfully completed a purchase.
          *
          * @param purchase
          */
        public void success(IabPurchase purchase);

         /**
          *
          * @param purchase
          */
        public void cancelled(IabPurchase purchase);

         /**
          *
          * @param purchase
          */
        public void alreadyOwned(IabPurchase purchase);

         /**
          *
          * @param message
          */
        public void fail(String message);
    }

     /**
      * Listens for inventory queries
      */
    public interface OnQueryInventoryListener {

         /**
          *
          * @param purchases
          * @param skuDetails
          */
        public void success(List<IabPurchase> purchases, List<IabSkuDetails> skuDetails);

         /**
          *
          * @param message
          */
        public void fail(String message);
    }


     /**
      * Listens for consumptions of purchases
      */
    public interface OnConsumeListener {
         /**
          *
          * @param purchase
          */
        public void success(IabPurchase purchase);

         /**
          *
          * @param message
          */
        public void fail(String message);
    }

}

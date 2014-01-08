 /*
 * Copyright (C) 2012 Soomla Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soomla.store.billing;

 /**
  * A utility class that defines interfaces for passing callbacks
  * to in-app billing events.
  */
public class IabCallbacks {

    /**
     * A general purpose listener for passing Soomla logic as a callback to
     * any in-app billing events.
     */
    public interface Listener {
        public void callback();
    }

    /**
     * A listener for passing Soomla logic as a callback to
     * purchase related events.  Specifically use it to these events:
     * <ol>
     *     <li>Purchase success</li>
     *     <li>Purchase cancelled</li>
     *     <li>Item already owned</li>
     * </ol>
     */
    public interface OnPurchaseEventListener {
        public void callback(Purchase purchase);
    }


    /**
     * A listener for passing Soomla logic as a callback
     * to purchases that can't be processed due to an unexpected result.
     */
    public interface OnPurchaseUnexpectedResultListener {
        public void callback(IabResult result);
    }
}

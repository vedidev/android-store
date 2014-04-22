/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soomla.store.billing;

/**
 * This exception is thrown when something goes wrong with the in-app billing process.
 * An IabException has an associated IabResult (an error).
 * To get the IabResults that caused this exception to be thrown, call {@link #getResult()}.
 *
 * IabException extends Exception
 */
public class IabException extends Exception {

    // Every IabException has an associated IabResult
    IabResult mResult;

    /**
     * Constructor
     * Creates instance of IabException with given result and no message
     *
     * @param r IabResult associated with this IabException
     */
    public IabException(IabResult r) {
        this(r, null);
    }

    /**
     * Constructor
     * Creates instance of IabException by creating associated IabResult with given response and
     * message (see {@link com.soomla.store.billing.IabResult})
     *
     * @param response response code - will be used for creating IabResult
     * @param message message - will be used for creating IabResult
     */
    public IabException(int response, String message) {
        this(new IabResult(response, message));
    }

    /**
     * Constructor
     *
     * @param r IabResult associated with this IabException
     * @param cause Exception cause
     */
    public IabException(IabResult r, Exception cause) {
        super(r.getMessage(), cause);
        mResult = r;
    }

    /**
     * Constructor
     *
     * @param response response code - will be used for creating IabResult
     * @param message message - will be used for creating IabResult
     * @param cause Exception cause
     */
    public IabException(int response, String message, Exception cause) {
        this(new IabResult(response, message), cause);
    }

    /**
     * Returns the IAB result (error) that this exception signals.
     *
     * @return result
     */
    public IabResult getResult() { return mResult; }
}
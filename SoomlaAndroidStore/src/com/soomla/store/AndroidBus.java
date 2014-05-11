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

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;

/**
 * This class overrides <code>Bus</code>'s functions post, register, and unregister.
 */
public class AndroidBus extends Bus {

    /**
     * Posts the given event so that all of its subscribers will be notified and will handle the
     * event.
     *
     * @param event an instance of any class may be published on the bus
     */
    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }

    /**
     * Registers to the event bus, in order to receive notifications about events.
     *
     * @param object
     */
    @Override
    public void register(final Object object) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.register(object);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    register(object);
                }
            });
        }
    }

    /**
     * Unregisters to the event bus, in order to STOP receiving notifications about events.
     *
     * @param object
     */
    @Override
    public void unregister(final Object object) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.unregister(object);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    unregister(object);
                }
            });
        }
    }


    /** Private Members */

    private final Handler mainThread = new Handler(Looper.getMainLooper());
}

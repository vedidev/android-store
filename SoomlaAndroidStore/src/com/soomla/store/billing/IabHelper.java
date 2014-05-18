package com.soomla.store.billing;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.soomla.store.StoreUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class IabHelper {

    public boolean isSetupDone() {
        return mSetupDone;
    }

    /**
     * Starts the setup process. This will start up the setup process asynchronously.
     * You will be notified through the listener when the setup process is complete.
     * This method is safe to call from a UI thread.
     *
     * @param listener The listener to notify when the setup process is complete.
     */
    public synchronized void startSetup(final OnIabSetupFinishedListener listener) {
        if (mSetupDone)
        {
            StoreUtils.LogDebug(TAG, "The purchasing observer is already started. Just running the post listener.");

            if (listener != null) {
                listener.onIabSetupFinished(new IabResult(IabResult.BILLING_RESPONSE_RESULT_OK, "Setup successful."));
            }
            return;
        }

        if (mSetupFinishedListeners == null) {
            mSetupFinishedListeners = new ArrayList<OnIabSetupFinishedListener>();
        }
        mSetupFinishedListeners.add(listener);

        if (!mSetupStarted) {
            mSetupStarted = true;

            // Connection to IAB service
            StoreUtils.LogDebug(TAG, "Starting in-app billing setup.");

            startSetupInner();
        }
    }

    /**
     * Initiate the UI flow for an in-app purchase. Call this method to initiate an in-app purchase,
     * which will involve bringing up the Google Play screen. The calling activity will be paused while
     * the user interacts with Google Play, and the result will be delivered via the activity's
     * {@link android.app.Activity#onActivityResult} method, at which point you must call
     * this object's {@link #handleActivityResult} method to continue the purchase flow. This method
     * MUST be called from the UI thread of the Activity.
     *
     * @param act The calling activity.
     * @param sku The sku of the item to purchase.
     * @param listener The listener to notify when the purchase process finishes
     * @param extraData Extra data (developer payload), which will be returned with the purchase data
     *     when the purchase completes. This extra data will be permanently bound to that purchase
     *     and will always be returned when the purchase is queried.
     */
    public void launchPurchaseFlow(Activity act, String sku,
                                   OnIabPurchaseFinishedListener listener, String extraData) {
        checkSetupDoneAndThrow("launchPurchaseFlow");
        flagStartAsync("launchPurchaseFlow");

        mPurchaseListener = listener;
        launchPurchaseFlowInner(act, sku, extraData);
    }

    public void restorePurchasesAsync(RestorePurchasessFinishedListener listener) {
        checkSetupDoneAndThrow("restorePurchases");
        flagStartAsync("restore purchases");

        mRestorePurchasessFinishedListener = listener;
        restorePurchasesAsyncInner();
    }

    public void fetchSkusDetailsAsync(List<String> skus, final FetchSkusDetailsFinishedListener listener) {
        checkSetupDoneAndThrow("fetchSkusDetails");
        flagStartAsync("fetch skus details");

        mFetchSkusDetailsFinishedListener = listener;
        fetchSkusDetailsAsyncInner(skus);
    }


    public boolean isAsyncInProgress() {
        return mAsyncInProgress;
    }


    /** Listeners **/

    /**
     * Callback for setup process. This listener's {@link #onIabSetupFinished} method is called
     * when the setup process is complete.
     */
    public interface OnIabSetupFinishedListener {
        /**
         * Called to notify that setup is complete.
         *
         * @param result The result of the setup process.
         */
        public void onIabSetupFinished(IabResult result);
    }

    /**
     * Callback that notifies when a purchase is finished.
     */
    public interface OnIabPurchaseFinishedListener {
        /**
         * Called to notify that an in-app purchase finished. If the purchase was successful,
         * then the sku parameter specifies which item was purchased. If the purchase failed,
         * the sku and extraData parameters may or may not be null, depending on how far the purchase
         * process went.
         *
         * @param result The result of the purchase.
         * @param info The purchase information (null if purchase failed)
         */
        public void onIabPurchaseFinished(IabResult result, IabPurchase info);
    }

    /**
     * Callback for restore purchases.
     */
    public interface RestorePurchasessFinishedListener {
        /**
         * Called to notify that an restore purchases operation completed.
         *
         * @param result The result of the operation.
         * @param inv The inventory.
         */
        public void onRestorePurchasessFinished(IabResult result, IabInventory inv);
    }

    /**
     * Callback for fetching of skus details.
     */
    public interface FetchSkusDetailsFinishedListener {
        /**
         * Called to notify that an fetch skus details operation completed.
         *
         * @param result The result of the operation.
         * @param inv The inventory.
         */
        public void onFetchSkusDetailsFinished(IabResult result, IabInventory inv);
    }



    /** Protected Functions **/

    /**
     * see startSetup
     */
    protected abstract void startSetupInner();

    /**
     * see launchPurchaseFlow
     */
    protected abstract void launchPurchaseFlowInner(Activity act, String sku, String extraData);

    /**
     * see restorePurchasesAsync
     */
    protected abstract void restorePurchasesAsyncInner();

    /**
     * see fetchSkusDetailsAsync
     */
    protected abstract void fetchSkusDetailsAsyncInner(final List<String> skus);

    protected void dispose() {
        mSetupDone = false;
        mSetupFinishedListeners = null;
    }


    /** restore transactions and refresh market items handlers **/

    protected void restorePurchasesSuccess(final IabResult result, final IabInventory inventory) {
        if (mRestorePurchasessFinishedListener != null) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    mRestorePurchasessFinishedListener.onRestorePurchasessFinished(
                            result, inventory);
                }
            });
        }
        // make sure to end the async operation...
        flagEndAsync();
    }

    protected void fetchSkusDetailsSuccess(final IabResult result, final IabInventory inventory) {
        if (mFetchSkusDetailsFinishedListener != null) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    mFetchSkusDetailsFinishedListener.onFetchSkusDetailsFinished(
                            result, inventory);
                }
            });
        }
        // make sure to end the async operation...
        flagEndAsync();
    }

    /** purchase flow handlers **/

    protected void purchaseFailed(final IabResult result, final IabPurchase purchase) {
        final Handler handler = new Handler(Looper.getMainLooper());
        if (mPurchaseListener != null) {
            handler.post(new Runnable() {


                @Override
                public void run() {
                    mPurchaseListener.onIabPurchaseFinished(result, purchase);
                }
            });
        }
        // make sure to end the async operation...
        flagEndAsync();
    }

    protected void purchaseSucceeded(final IabPurchase purchase) {
        if (mPurchaseListener != null) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {


                @Override
                public void run() {
                    mPurchaseListener.onIabPurchaseFinished(
                            new IabResult(IabResult.BILLING_RESPONSE_RESULT_OK, "Success"),
                            purchase);
                }
            });
        }
        // make sure to end the async operation...
        flagEndAsync();
    }


    /** setup related checkers and handlers **/

    protected void setupSuccess() {
        mSetupDone = true;
        if (mSetupFinishedListeners != null) {
            final Handler handler = new Handler(Looper.getMainLooper());
            for (final OnIabSetupFinishedListener listener : mSetupFinishedListeners) {
                handler.post(new Runnable() {


                    @Override
                    public void run() {
                        listener.onIabSetupFinished(new IabResult(IabResult.BILLING_RESPONSE_RESULT_OK, "Setup successful."));
                    }
                });
            }
        }
    }
    protected void setupFailed(final IabResult result) {
        mSetupDone = false;
        if (mSetupFinishedListeners != null) {
            final Handler handler = new Handler(Looper.getMainLooper());
            for (final OnIabSetupFinishedListener listener : mSetupFinishedListeners) {
                handler.post(new Runnable() {


                    @Override
                    public void run() {
                        listener.onIabSetupFinished(result);
                    }
                });
            }
        }
    }

    // Checks that setup was done; if not, throws an exception.
    protected void checkSetupDoneAndThrow(String operation) {
        if (!isSetupDone()) {
            StoreUtils.LogError(TAG, "Illegal state for operation (" + operation + "): IAB helper is not set up.");
            throw new IllegalStateException("IAB helper is not set up. Can't perform operation: " + operation);
        }
    }



    /** Async related functions **/

    protected synchronized void flagStartAsync(String operation) {
        if (mAsyncInProgress) throw new IllegalStateException("Can't start async operation (" +
                operation + ") because another async operation(" + mAsyncOperation + ") is in progress.");
        mAsyncOperation = operation;
        mAsyncInProgress = true;
        StoreUtils.LogDebug(TAG, "Starting async operation: " + operation);
    }

    protected synchronized void flagEndAsync() {
        StoreUtils.LogDebug(TAG, "Ending async operation: " + mAsyncOperation);
        mAsyncOperation = "";
        mAsyncInProgress = false;
    }



    /** Private Members **/

    private static String TAG = "SOOMLA PurchaseObserver";

    // This tells us if we're on production or sandbox environment (for server validation)
    private boolean mRvsProductionMode = false;
    // Is setup done?
    private boolean mSetupDone = false;
    // Is setup started?
    private boolean mSetupStarted = false;
    // Is an asynchronous operation in progress?
    // (only one at a time can be in progress)
    private boolean mAsyncInProgress = false;
    // (for logging/debugging)
    // if mAsyncInP!?*.java;!?*.form;!?*.class;!?*.groovy;!?*.scala;!?*.flex;!?*.kt;!?*.cljrogress == true, what asynchronous operation is in progress?
    private String mAsyncOperation = "";
    // The listeners registered on setup, which we have to call back when
    // the purchase finishes
    private List<OnIabSetupFinishedListener> mSetupFinishedListeners;
    // The listener registered on launchPurchaseFlow, which we have to call back when
    // the purchase finishes.
    // We only keep one and not a list b/c the purchase operation can only run one-at-a-time
    private OnIabPurchaseFinishedListener mPurchaseListener;
    // The listener registered on restore purchases, which we have to call back when
    // the restore process finishes.
    private RestorePurchasessFinishedListener mRestorePurchasessFinishedListener;
    // The listener registered on restore purchases, which we have to call back when
    // the restore process finishes.
    private FetchSkusDetailsFinishedListener mFetchSkusDetailsFinishedListener;

}

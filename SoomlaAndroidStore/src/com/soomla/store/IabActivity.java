package com.soomla.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.soomla.store.events.UnexpectedStoreErrorEvent;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

public class IabActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String productId = intent.getStringExtra(StoreController.PROD_ID);
        String payload = intent.getStringExtra(StoreController.EXTRA_DATA);

        try {
            StoreController.getInstance().buyWithGooglePlayInner(this, productId, payload);
        } catch (IllegalStateException e) {
            StoreUtils.LogError(TAG, "Error purchasing item " + e.getMessage());
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
            finish();
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "Couldn't find a purchasable item with productId: " + productId);
            BusProvider.getInstance().post(new UnexpectedStoreErrorEvent());
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!StoreController.getInstance().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            finish();
        }
    }

    private static String TAG = "SOOMLA StoreActivity";
}
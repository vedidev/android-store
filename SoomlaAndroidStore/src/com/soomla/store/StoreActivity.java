package com.soomla.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StoreActivity extends Activity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!StoreController.getInstance().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StoreController.getInstance().setCurrentActivity(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        StoreController.getInstance().setCurrentActivity(this);
    }
    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    /* make sure we don't reference a dead activity */
    private void clearReferences() {
        Activity activity = StoreController.getInstance().getCurrentActivity();
        if (activity != null && activity.equals(this))
            StoreController.getInstance().setCurrentActivity(null);
    }
}
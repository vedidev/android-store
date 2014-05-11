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


package com.soomla.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.soomla.store.BusProvider;
import com.soomla.store.StoreController;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.domain.virtualGoods.VirtualGood;
import com.soomla.store.events.CurrencyBalanceChangedEvent;
import com.soomla.store.events.GoodBalanceChangedEvent;
import com.soomla.store.exceptions.InsufficientFundsException;
import com.soomla.store.purchaseTypes.PurchaseWithVirtualItem;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class represents Muffin Rush's store of available goods.
 */
public class StoreGoodsActivity extends Activity {

    /**
     * Starts <code>StorePacksActivity</code>.
     * This function is called when the button "Wants to buy more muffins" is clicked.
     *
     * @param v View
     * @throws IOException
     */
    public void wantsToBuyPacks(View v) throws IOException {
        Intent intent = new Intent(getApplicationContext(), StorePacksActivity.class);
        startActivity(intent);
    }

    /**
     * Queries Google Play Store's inventory. Upon success, returns a list of all metadata stored
     * there (the items that have been purchased). The metadata includes each item's name,
     * description, price, product id, etc...  Upon failure, returns error message.
     *
     * @param v View
     * @throws IOException
     */
    public void restoreTransactions(View v) throws IOException{
        StoreController.getInstance().refreshInventory(false);
    }

    /**
     * Receives the given <code>onCurrencyBalanceChanged</code>. Upon notification, fetches the
     * currency balance and places it in the balance label.
     *
     * @param currencyBalanceChangedEvent the event received
     */
    @Subscribe
    public void onCurrencyBalanceChanged(CurrencyBalanceChangedEvent currencyBalanceChangedEvent) {
        TextView muffinsBalance = (TextView)findViewById(R.id.balance);
        muffinsBalance.setText("" + currencyBalanceChangedEvent.getBalance());
    }

    /**
     * Receives the given <code>goodBalanceChangedEvent</code>. Upon notification, fetches the
     * good associated with the given <code>goodBalanceChangedEvent</code> and displays its price
     * and the balance.
     *
     * @param goodBalanceChangedEvent the event received
     */
    @Subscribe
    public void onGoodBalanceChanged(GoodBalanceChangedEvent goodBalanceChangedEvent) {
        VirtualGood good = goodBalanceChangedEvent.getGood();
        int id = 0;
        for(int i=0; i<StoreInfo.getGoods().size(); i++) {
            if (StoreInfo.getGoods().get(i).getItemId().equals(good.getItemId())) {
                id = i;
                break;
            }
        }
        ListView list = (ListView) findViewById(R.id.list);
        TextView info = (TextView)list.getChildAt(id).findViewById(R.id.item_info);
        PurchaseWithVirtualItem pwvi = (PurchaseWithVirtualItem) good.getPurchaseType();
        info.setText("price: " + pwvi.getAmount() +
                " balance: " + goodBalanceChangedEvent.getBalance());
    }

    /**
     * Called when the activity starts.
     * Displays the list view of the game, where users can see the available goods for purchase.
     *
     * @param savedInstanceState if the activity should be re-initialized after previously being
     *                           shut down then this <code>Bundle</code> will contain the most
     *                           recent data, otherwise it will be null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        StoreController.getInstance().startIabServiceInBg();
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Virtual Goods");
        mImages = generateImagesHash();
        mStoreAdapter = new StoreAdapter();

        /* configuring the list with an adapter */
        final Activity activity = this;
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(mStoreAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               /*
                The user decided to make an actual purchase of virtual goods. We try to buy() the
                user's desired good and StoreController tells us if the user has enough funds to
                make the purchase. If he doesn't have enough then an InsufficientFundsException
                will be thrown.
                */
                VirtualGood good = StoreInfo.getGoods().get(i);
                try {
                    good.buy();
                } catch (InsufficientFundsException e) {
                    AlertDialog ad = new AlertDialog.Builder(activity).create();
                    ad.setCancelable(false);
                    ad.setMessage("You don't have enough muffins.");
                    ad.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();

                }
            }
        });

    }

    /**
     * Called after the activity has been paused, and now this activity will start interacting with
     * your user once again.
     * Fetches the currency balance and places it in the balance label.
     */
    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        TextView muffinsBalance = (TextView)findViewById(R.id.balance);
        muffinsBalance.setText("" + StorageManager.getVirtualCurrencyStorage().
                getBalance(StoreInfo.getCurrencies().get(0)));
    }

    /**
     * Called when your user leaves your activity but does not quit, or in other words, upon a call
     * to <code>onPause()</code> your activity goes to the background.
     */
    @Override
    protected void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    /**
     * Stops the in-app billing service before the activity gets destroyed.
     */
    @Override
    protected void onDestroy() {
        StoreController.getInstance().stopIabServiceInBg();
        super.onDestroy();
    }

    /**
     * Creates a hashmap of images of all goods in Muffin Rush.
     *
     * @return hashmap of dessert images needed in Muffin Rush
     */
    private HashMap<String, Object> generateImagesHash() {
        final HashMap<String, Object> images = new HashMap<String, Object>();
        images.put(MuffinRushAssets.CHOCLATECAKE_ITEM_ID, R.drawable.chocolate_cake);
        images.put(MuffinRushAssets.CREAMCUP_ITEM_ID, R.drawable.cream_cup);
        images.put(MuffinRushAssets.MUFFINCAKE_ITEM_ID, R.drawable.fruit_cake);
        images.put(MuffinRushAssets.PAVLOVA_ITEM_ID, R.drawable.pavlova);
        return images;
    }

    private class StoreAdapter extends BaseAdapter {

        public StoreAdapter() {
        }

        public int getCount() {
            return mImages.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if(convertView == null){
                vi = getLayoutInflater().inflate(R.layout.list_item, null);
            }

            TextView title = (TextView)vi.findViewById(R.id.title);
            TextView content = (TextView)vi.findViewById(R.id.content);
            ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);
            TextView info = (TextView)vi.findViewById(R.id.item_info);

            VirtualGood good = StoreInfo.getGoods().get(position);//VirtualGood) data.get(position).get(StoreGoodsActivity.KEY_GOOD);

            // Setting all values in listview
            vi.setTag(good.getItemId());
            title.setText(good.getName());
            content.setText(good.getDescription());
            thumb_image.setImageResource((Integer)mImages.get(good.getItemId()));
            PurchaseWithVirtualItem pwvi = (PurchaseWithVirtualItem) good.getPurchaseType();
            info.setText("price: " + pwvi.getAmount() +
                    " balance: " + StorageManager.getVirtualGoodsStorage().getBalance(good));

            return vi;
        }
    }


    /** Private Members */

    private StoreAdapter mStoreAdapter;

    private HashMap<String, Object> mImages;

}
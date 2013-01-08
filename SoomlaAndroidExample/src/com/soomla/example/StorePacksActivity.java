package com.soomla.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.soomla.store.BusProvider;
import com.soomla.store.StoreController;
import com.soomla.store.data.StorageManager;
import com.soomla.store.domain.data.GoogleMarketItem;
import com.soomla.store.domain.data.VirtualCurrency;
import com.soomla.store.domain.data.VirtualCurrencyPack;
import com.soomla.store.domain.data.VirtualGood;
import com.soomla.store.events.CurrencyBalanceChangedEvent;
import com.soomla.store.events.GoodBalanceChangedEvent;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

public class StorePacksActivity extends Activity {

    private StoreAdapter mStoreAdapter;
    private ArrayList<HashMap<String, Object>> mData;

    static final String KEY_THUMB        = "thumb_url";
    static final String KEY_PACK         = "virtual_pack";
    static final String KEY_GOOGLE_ITEM  = "google_market_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        LinearLayout getMore = (LinearLayout)findViewById(R.id.getMore);
        TextView title = (TextView)findViewById(R.id.title);

        getMore.setVisibility(View.INVISIBLE);
        title.setText("Virtual Currency Packs");

        mData = generateDataHash();

        mStoreAdapter = new StoreAdapter(mData);


        /* configuring the list with an adapter */

        final Activity activity = this;
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(mStoreAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               /*
                * the user decided to make and actual purchase of virtual goods. we try to purchase and
                * StoreController tells us if the user has enough funds to make the purchase. If he won't
                * have enough than an InsufficientFundsException will be thrown.
                */

                HashMap<String, Object> item = mData.get(i);
                if (item.containsKey(StorePacksActivity.KEY_PACK)){
                    // purchasing a currency pack
                    VirtualCurrencyPack pack = (VirtualCurrencyPack) item.get(StorePacksActivity.KEY_PACK);
                    try {
                        StoreController.getInstance().buyGoogleMarketItem(pack.getProductId());
                    } catch (VirtualItemNotFoundException e) {
                        AlertDialog ad = new AlertDialog.Builder(activity).create();
                        ad.setCancelable(false); // This blocks the 'BACK' button
                        ad.setMessage("Can't continue with purchase (the given product id did not match any actual product... Fix IStoreAssets)");
                        ad.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        ad.show();

                    }
                } else {
                    // purchasing a MANAGED item
                    GoogleMarketItem gmi = (GoogleMarketItem) item.get(StorePacksActivity.KEY_GOOGLE_ITEM);
                    try {
                        StoreController.getInstance().buyGoogleMarketItem(gmi.getProductId());
                    } catch (VirtualItemNotFoundException e) {
                        AlertDialog ad = new AlertDialog.Builder(activity).create();
                        ad.setCancelable(false); // This blocks the 'BACK' button
                        ad.setMessage("Can't continue with purchase (the given product id did not match any actual product... Fix IStoreAssets)");
                        ad.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        ad.show();
                    }
                }

                /* fetching the currency balance and placing it in the balance label */
                TextView muffinsBalance = (TextView)activity.findViewById(R.id.balance);
                muffinsBalance.setText("" + StorageManager.getVirtualCurrencyStorage().
                        getBalance(MuffinRushAssets.MUFFIN_CURRENCY));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);

        /* fetching the currency balance and placing it in the balance label */
        TextView muffinsBalance = (TextView)findViewById(R.id.balance);
        muffinsBalance.setText("" + StorageManager.getVirtualCurrencyStorage().
                getBalance(MuffinRushAssets.MUFFIN_CURRENCY));
    }

    @Override
    protected void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    private ArrayList<HashMap<String, Object>> generateDataHash() {
        final ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> item = new HashMap<String, Object>();

        item.put(StorePacksActivity.KEY_GOOGLE_ITEM, MuffinRushAssets.NO_ADDS_NONCONS);
        item.put(StorePacksActivity.KEY_THUMB, R.drawable.no_ads);
        data.add(item);
        item.put(StorePacksActivity.KEY_PACK, MuffinRushAssets.TENMUFF_PACK);
        item.put(StorePacksActivity.KEY_THUMB, R.drawable.muffins01);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put(StorePacksActivity.KEY_PACK, MuffinRushAssets.FIFTYMUFF_PACK);
        item.put(StorePacksActivity.KEY_THUMB, R.drawable.muffins02);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put(StorePacksActivity.KEY_PACK, MuffinRushAssets.FORTYMUFF_PACK);
        item.put(StorePacksActivity.KEY_THUMB, R.drawable.muffins03);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put(StorePacksActivity.KEY_PACK, MuffinRushAssets.THOUSANDMUFF_PACK);
        item.put(StorePacksActivity.KEY_THUMB, R.drawable.muffins04);
        data.add(item);
        return data;
    }

    @Subscribe
    public void onCurrencyBalanceChanged(CurrencyBalanceChangedEvent currencyBalanceChangedEvent) {
        /* fetching the currency balance and placing it in the balance label */
        TextView muffinsBalance = (TextView)findViewById(R.id.balance);
        muffinsBalance.setText("" + currencyBalanceChangedEvent.getBalance());
    }

    private class StoreAdapter extends BaseAdapter {

        public StoreAdapter(ArrayList<HashMap<String, Object>> d) {
        }

        public int getCount() {
            return mData.size();
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
            TextView info = (TextView)vi.findViewById(R.id.item_info);
            ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);

            // Setting all values in listview
            HashMap<String, Object> item = mData.get(position);
            if (item.containsKey(StorePacksActivity.KEY_PACK)){
                VirtualCurrencyPack pack = (VirtualCurrencyPack) item.get(StorePacksActivity.KEY_PACK);
                title.setText(pack.getName());
                content.setText(pack.getDescription());
                info.setText("price: $" + pack.getPrice());
                thumb_image.setImageResource((Integer)mData.get(position).get(KEY_THUMB));
            } else {
                title.setText("Remove Ads!");
                content.setText("Test purchase of MANAGED item.");
                info.setText("");
                thumb_image.setImageResource((Integer)mData.get(position).get(KEY_THUMB));
            }

            return vi;
        }
    }

}
package com.soomla.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.soomla.store.StoreController;
import com.soomla.store.data.StorageManager;
import com.soomla.store.domain.data.VirtualGood;
import com.soomla.store.exceptions.InsufficientFundsException;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;

public class StoreGoodsActivity extends Activity {

    private StoreAdapter mStoreAdapter;
    private ArrayList<HashMap<String, Object>> mData;

    static final String KEY_THUMB = "thumb_url";
    static final String KEY_GOOD = "virtual_good";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        StoreController.getInstance().storeOpening(this, new Handler());

        TextView title = (TextView)findViewById(R.id.title);

        title.setText("Virtual Goods");

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

                VirtualGood good = (VirtualGood) mData.get(i).get(StoreGoodsActivity.KEY_GOOD);
                try {
                    StoreController.getInstance().buyVirtualGood(good.getItemId());

                    /* fetching the currency balance and placing it in the balance label */
                    TextView muffinsBalance = (TextView)activity.findViewById(R.id.balance);
                    muffinsBalance.setText("" + StorageManager.getInstance().getVirtualCurrencyStorage().
                            getBalance(MuffinRushAssets.MUFFIN_CURRENCY));

                    HashMap<String, Integer> currencyValues = good.getCurrencyValues();
                    TextView info = (TextView)view.findViewById(R.id.item_info);
                    String balanceStr = info.getText().toString().substring(info.getText().toString().lastIndexOf(":") + 2);
                    info.setText("price: " + currencyValues.get(MuffinRushAssets.MUFFIN_CURRENCY_ITEM_ID) +
                            " balance: " + (Integer.parseInt(balanceStr) + 1));

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

                } catch (VirtualItemNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        /* fetching the currency balance and placing it in the balance label */
        TextView muffinsBalance = (TextView)findViewById(R.id.balance);
        muffinsBalance.setText("" + StorageManager.getInstance().getVirtualCurrencyStorage().
                getBalance(MuffinRushAssets.MUFFIN_CURRENCY));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        StoreController.getInstance().storeClosing();
    }

    private ArrayList<HashMap<String, Object>> generateDataHash() {
        final ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put(StoreGoodsActivity.KEY_GOOD, MuffinRushAssets.CHOCLATECAKE_GOOD);
        item.put(StoreGoodsActivity.KEY_THUMB, R.drawable.chocolate_cake);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put(StoreGoodsActivity.KEY_GOOD, MuffinRushAssets.CREAMCUP_GOOD);
        item.put(StoreGoodsActivity.KEY_THUMB, R.drawable.cream_cup);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put(StoreGoodsActivity.KEY_GOOD, MuffinRushAssets.MUFFINCAKE_GOOD);
        item.put(StoreGoodsActivity.KEY_THUMB, R.drawable.fruit_cake);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put(StoreGoodsActivity.KEY_GOOD, MuffinRushAssets.PAVLOVA_GOOD);
        item.put(StoreGoodsActivity.KEY_THUMB, R.drawable.pavlova);
        data.add(item);
        return data;
    }

    private class StoreAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> data;

        public StoreAdapter(ArrayList<HashMap<String, Object>> d) {
            data=d;
        }

        public int getCount() {
            return data.size();
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

            VirtualGood good = (VirtualGood) data.get(position).get(StoreGoodsActivity.KEY_GOOD);

            // Setting all values in listview
            title.setText(good.getName());
            content.setText(good.getDescription());
            thumb_image.setImageResource((Integer)data.get(position).get(KEY_THUMB));
            HashMap<String, Integer> currencyValues = good.getCurrencyValues();
            info.setText("price: " + currencyValues.get(MuffinRushAssets.MUFFIN_CURRENCY_ITEM_ID) +
                    " balance: 0");

            return vi;
        }
    }

    public void wantsToBuyPacks(View v) {
        Intent intent = new Intent(getApplicationContext(), StorePacksActivity.class);
        startActivity(intent);
    }


}
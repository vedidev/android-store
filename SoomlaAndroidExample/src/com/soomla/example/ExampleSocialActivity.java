/*
 * Copyright (C) 2012 Soomla Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.soomla.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soomla.blueprint.rewards.Reward;
import com.soomla.blueprint.rewards.VirtualItemReward;
import com.soomla.profile.IContextProvider;
import com.soomla.profile.SoomlaProfile;
import com.soomla.profile.events.UserProfileUpdatedEvent;
import com.soomla.profile.events.auth.LoginFinishedEvent;
import com.soomla.profile.exceptions.ProviderNotFoundException;
import com.soomla.store.BusProvider;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExampleSocialActivity extends Activity {

    private static final String TAG = "MainSocialActivity";

    private Button mBtnShare;

    private ViewGroup mProfileBar;
    private ImageView mProfileAvatar;
    private TextView mProfileName;

    private ViewGroup mPnlStatusUpdate;
    private Button mBtnUpdateStatus;
    private EditText mEdtStatus;

    private ViewGroup mPnlStoryUpdate;
    private Button mBtnUpdateStory;
    private EditText mEdtStory;

    private ProgressDialog mProgressDialog;

    private String mItemId = "no_ads";
    private String mItemName = "No Ads";
    private int mItemAmount = 1;
    private int mItemResId = R.drawable.ic_launcher;

    private String mProvider = "SocialAuth.facebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_social);

        mProgressDialog = new ProgressDialog(this);

        final Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mItemId = extras.getString("id");
            mItemAmount = extras.getInt("amount", 1);
            mItemName = extras.getString("name");
            mItemResId = extras.getInt("iconResId", R.drawable.ic_launcher);
        }

        final TextView vItemDisplay = (TextView) findViewById(R.id.vItem);
        if(vItemDisplay != null) {
            vItemDisplay.setText(mItemName);
            vItemDisplay.setCompoundDrawablesWithIntrinsicBounds(
                    null, getResources().getDrawable(mItemResId), null, null);
        }

        mProfileBar = (ViewGroup) findViewById(R.id.profile_bar);
        mProfileAvatar = (ImageView) findViewById(R.id.prof_avatar);
        mProfileName = (TextView) findViewById(R.id.prof_name);

        mPnlStatusUpdate = (ViewGroup) findViewById(R.id.pnlStatusUpdate);
        mEdtStatus = (EditText) findViewById(R.id.edtStatusText);
        mBtnUpdateStatus = (Button) findViewById(R.id.btnStatusUpdate);
        mBtnUpdateStatus.setEnabled(false);
        mBtnUpdateStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String message = mEdtStatus.getText().toString();

                // create social action


                // optionally attach rewards to it
                Reward gameReward = new VirtualItemReward("blabla", "Update Status for VG", mItemAmount, mItemId);

                // perform social action
                try {
                    SoomlaProfile.getInstance().getSocialController().updateStatus(mProvider, message, gameReward);
                } catch (ProviderNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        mPnlStoryUpdate = (ViewGroup) findViewById(R.id.pnlStoryUpdate);
        mEdtStory = (EditText) findViewById(R.id.edtStoryText);
        mBtnUpdateStory = (Button) findViewById(R.id.btnStoryUpdate);
        mBtnUpdateStory.setEnabled(false);
//        mBtnUpdateStory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String message = mEdtStory.getText().toString();
//                // another example
//                UpdateStoryAction updateStoryAction = new UpdateStoryAction(
//                        ISocialCenter.FACEBOOK,
//                        message, "name", "caption", "description",
//                        "http://soom.la",
//                        "https://s3.amazonaws.com/soomla_images/website/img/500_background.png");
//
//                // optionally attach rewards to it
//                Reward muffinsReward = new SocialVirtualItemReward("Update Story for VG",
//                        mItemId, mItemAmount);
//                updateStoryAction.getRewards().add(muffinsReward);
//
//                try {
//                    socialAuthFacebookProvider.updateStoryAsync(updateStoryAction);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        mBtnShare = (Button) findViewById(R.id.btnShare);
//        soomlaSocialAuthCenter.registerShareButton(mBtnShare);

        try {



            SoomlaProfile.getInstance().getSocialController().login(this, mProvider, true);
        } catch (ProviderNotFoundException e) {
            e.printStackTrace();
        }


        mProgressDialog.setMessage("logging in...");
        mProgressDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe public void onSocialLoginEvent(LoginFinishedEvent loginFinishedEvent) {
        // Variable to receive message status
        Log.d(TAG, "Authentication Successful");

        if(mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        // Get name of provider after authentication
        final String providerName = loginFinishedEvent.getProvider();
        Log.d(TAG, "Provider Name = " + providerName);
        Toast.makeText(this, providerName + " connected", Toast.LENGTH_SHORT).show();

        // Please avoid sending duplicate message. Social Media Providers
        // block duplicate messages.


        showView(mProfileBar, true);
        new DownloadImageTask(mProfileAvatar).execute(loginFinishedEvent.UserProfile.getAvatarLink());
        mProfileName.setText(loginFinishedEvent.UserProfile.getFullName());

        updateUIOnLogin(providerName);
    }
//
//    @Subscribe public void onSocialLoginErrorEvent(SocialLoginErrorEvent socialLoginErrorEvent) {
//        if(mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
//        Log.e(TAG, "login error:" + socialLoginErrorEvent.mException);
//    }

//    @Subscribe public void onSocialActionPerformedEvent(
//            SocialActionPerformedEvent socialActionPerformedEvent) {
//        final ISocialAction socialAction = socialActionPerformedEvent.socialAction;
//        final String msg = socialAction.getName() + " on " +
//                socialAction.getProviderName() + " performed successfully";
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//        finish(); // nothing much to do here in this example, go back to parent activity
//    }
//
    private void updateUIOnLogin(final String providerName) {
        mBtnShare.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(android.R.drawable.ic_lock_power_off),
                null);
        mBtnShare.setVisibility(View.VISIBLE);

        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SoomlaProfile.getInstance().getSocialController().logout(mProvider);
                } catch (ProviderNotFoundException e) {
                    e.printStackTrace();
                }
                updateUIOnLogout();

                // re-enable share button login
//                soomlaSocialAuthCenter.registerShareButton(mBtnShare);
            }
        });

        showView(mPnlStatusUpdate, true);
//        showView(mPnlStoryUpdate, true);
        mBtnShare.setEnabled(true);

        mBtnUpdateStatus.setEnabled(true);
//        mBtnUpdateStory.setEnabled(true);
    }

    private void updateUIOnLogout() {

        mBtnUpdateStatus.setEnabled(false);
        mBtnUpdateStory.setEnabled(false);

        showView(mProfileBar, false);
        showView(mPnlStatusUpdate, false);
        showView(mPnlStoryUpdate, false);

        mProfileAvatar.setImageBitmap(null);
        mProfileName.setText("");

        mBtnShare.setVisibility(View.INVISIBLE);
        mBtnShare.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(android.R.drawable.ic_menu_share),
                null);
    }

    private void showView(final View view, boolean show) {
        final Animation animation = show ?
                AnimationUtils.makeInAnimation(view.getContext(), true) :
                AnimationUtils.makeOutAnimation(view.getContext(), true);
        animation.setFillAfter(true);
        animation.setDuration(500);
        view.startAnimation(animation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bmp = downloadBitmapWithClient(url);

            return bmp;
        }

        // doesn't follow https redirect!
        private Bitmap downloadBitmap(String stringUrl) {
            URL url = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;

            try {
                url = new URL(stringUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(true);
                inputStream = connection.getInputStream();

                return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
            } catch (Exception e) {
                Log.w(TAG, "Error while retrieving bitmap from " + stringUrl, e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return null;
        }

        private Bitmap downloadBitmapWithClient(String url) {
            final AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
            HttpClientParams.setRedirecting(httpClient.getParams(), true);
            final HttpGet request = new HttpGet(url);

            try {
                HttpResponse response = httpClient.execute(request);
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Header[] headers = response.getHeaders("Location");

                    if (headers != null && headers.length != 0) {
                        String newUrl = headers[headers.length - 1].getValue();
                        // call again with new URL
                        return downloadBitmap(newUrl);
                    } else {
                        return null;
                    }
                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = entity.getContent();

                        // do your work here
                        return BitmapFactory.decodeStream(inputStream);
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                request.abort();
            } finally {
                if (httpClient != null) {
                    httpClient.close();
                }
            }

            return null;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int byteValue = read();
                    if (byteValue < 0) {
                        break; // we reached EOF
                    } else
                    {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}

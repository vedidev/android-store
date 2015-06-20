package com.soomla.store;

import com.soomla.BusProvider;
import com.soomla.SoomlaUtils;
import com.soomla.store.billing.IabPurchase;
import com.soomla.store.domain.PurchasableVirtualItem;
import com.soomla.store.events.MarketPurchaseVerificationEvent;
import com.soomla.store.events.UnexpectedStoreErrorEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author vedi
 *         date 26/05/15
 */
public class SoomlaVerification {

    private static final String VERIFY_URL = "https://verify.soom.la/verify_android";
    private static final String TAG = "SOOMLA SoomlaVerification";

    private final IabPurchase purchase;
    private final PurchasableVirtualItem pvi;

    public SoomlaVerification(IabPurchase purchase, PurchasableVirtualItem pvi) {

        if (purchase == null || pvi == null) {
            throw new IllegalArgumentException();
        }

        this.purchase = purchase;
        this.pvi = pvi;
    }

    public void verifyData() {
        String purchaseToken = this.purchase.getToken();

        if (purchaseToken != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("purchaseToken", purchaseToken);
                jsonObject.put("packageName", this.purchase.getPackageName());
                jsonObject.put("sku", this.purchase.getSku());
                SoomlaUtils.LogDebug(TAG, String.format("verifying purchase on server: %s", VERIFY_URL));

                HttpResponse resp = doPost(jsonObject);

                if (resp != null) {
                    int statusCode = resp.getStatusLine().getStatusCode();
                    if (statusCode >= 200 && statusCode <= 299) {
                        StringBuilder stringBuilder = new StringBuilder();
                        InputStream inputStream = resp.getEntity().getContent();
                        Reader reader = new BufferedReader(new InputStreamReader(inputStream));
                        final char[] buffer = new char[1024];
                        int bytesRead;
                        while ((bytesRead = reader.read(buffer, 0, buffer.length)) > 0) {
                            stringBuilder.append(buffer, 0, bytesRead);
                        }
                        JSONObject resultJsonObject = new JSONObject(stringBuilder.toString());
                        boolean verified = resultJsonObject.optBoolean("verified", false);
                        if (verified) {
                            // I did this according, how we have this in iOS, however `verified` will be always `true` in the event.
                            BusProvider.getInstance().post(new MarketPurchaseVerificationEvent(pvi, verified, purchase));
                        }
                    } else {
                        fireError("There was a problem when verifying. Will try again later.");
                    }

                } else {
                    fireError("Failed to connect to verification server. Not doing anything ... the purchasing process will happen again next time the service is initialized.");
                }


            } catch (JSONException e) {
                fireError("Cannot build up json for verification: " + e);
            } catch (Exception e) {
                fireError(e.getMessage());
            }
        } else {
            fireError("An error occurred while trying to get receipt purchaseToken. Stopping the purchasing process for: " + purchase.getSku());
        }
    }

    private HttpResponse doPost(JSONObject jsonObject) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(VERIFY_URL);
        post.setHeader("Content-type", "application/json");

        String body = jsonObject.toString();
        post.setEntity(new StringEntity(body, "UTF8"));

        return client.execute(post);
    }

    private void fireError(String message) {
        SoomlaUtils.LogError(TAG, message);
        BusProvider.getInstance().post(new UnexpectedStoreErrorEvent(message));
    }
}

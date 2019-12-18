package com.huyanh.base;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.github.piasy.safelyandroid.component.support.SafelyAppCompatActivity;
import com.huyanh.base.custominterface.PopupListener;
import com.huyanh.base.util.IabHelper;
import com.huyanh.base.util.IabResult;
import com.huyanh.base.util.Inventory;
import com.huyanh.base.util.Purchase;
import com.huyanh.base.utils.BaseConstant;
import com.huyanh.base.utils.BaseUtils;
import com.huyanh.base.utils.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class BaseActivity extends SafelyAppCompatActivity implements PopupListener {
    public BaseApplication baseApplication;
    public BaseActivity baseActivity;
    public Handler handler = new Handler();
    private Runnable runnableAddPopupListener = new Runnable() {
        @Override
        public void run() {
            if (baseApplication.getPopup() != null) {
                baseApplication.getPopup().addPopupListtener(baseActivity);
                return;
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseApplication = (BaseApplication) getApplication();
        baseActivity = this;
    }

    private IabHelper mHelper;

    public void initInappBilling() {
        mHelper = new IabHelper(this, BaseConstant.base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d("Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    return;
                }

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
//                mBroadcastReceiver = new IabBroadcastReceiver(HomeActivity.this);
//                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
//                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d("Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.e("Error querying inventory. Another async operation in progress. " + e.getMessage());
                }
            }
        });
    }

    public void purchase() {
        try {
            mHelper.launchPurchaseFlow(baseActivity, BaseConstant.skuId, BaseConstant.REQUEST_IN_APP_BILLING,
                    mPurchaseFinishedListener, "HuyAnhPayload");
        } catch (Exception e) {
            Log.e("error launch Purchase: " + e.getMessage());
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d("Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
                Log.e("Failed to query inventory: " + result);
                return;
            }

            Log.d("Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(BaseConstant.skuId);
            if (premiumPurchase != null) {
                baseApplication.isPurchase = true;
                Log.i("isPurchase = true");
            } else {
                Log.e("premiumPurchase = null");

//                try {
//                    mHelper.launchPurchaseFlow(baseActivity, BaseConstant.skuId, BaseConstant.REQUEST_IN_APP_BILLING,
//                            mPurchaseFinishedListener, "HuyAnhPayload");
//                } catch (Exception e) {
//                    Log.e("error launch Purchase: " + e.getMessage());
//                }
            }
            onDoneQuerryInappBilling();
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d("Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                Log.e("Error purchasing: " + result);
//                setWaitScreen(false);
                return;
            }
//            if (!verifyDeveloperPayload(purchase)) {
//                complain("Error purchasing. Authenticity verification failed.");
//                setWaitScreen(false);
//                return;
//            }

            Log.d("Purchase successful.");

            if (purchase.getSku().equals(BaseConstant.skuId)) {
                // bought the premium upgrade!
                Log.d("Purchase is premium upgrade. Congratulating user.");
                baseApplication.isPurchase = true;
                Toast.makeText(baseActivity, getString(R.string.succes_purchase_message), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Purchase is faile.");
            }
        }
    };


    @Override
    public void onDoneQuerryInappBilling() {
    }

    @Override
    public void onClose(Object object) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(runnableAddPopupListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (baseApplication.getPopup() != null)
            baseApplication.getPopup().removePopupListener(this);
    }

    public boolean showPopup(Object object, boolean withOutCondition) {
        if (baseApplication.getPopup() != null)
            return baseApplication.getPopup().showPopup(this, object, withOutCondition);
        return false;
    }

    public void processUpdate() {
        String version_manifest = "0";
        try {
            version_manifest = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (System.currentTimeMillis() - baseApplication.pref
                .getLong(BaseConstant.KEY_LASTTIME_SHOW_UPDATEPOPUP, 0) <= (baseApplication.getBaseConfig().getUpdate().getOffset_show() * 1000)) {
            Log.d("offset time show update popup");
        } else if (baseApplication.getBaseConfig().getUpdate().getStatus() == 1) {
            if (Integer.parseInt(version_manifest) < Integer.parseInt(baseApplication.getBaseConfig().getUpdate().getVersion())) {
                AlertDialog dialog = Dialog_Update(true);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();
                baseApplication.editor.putLong(BaseConstant.KEY_LASTTIME_SHOW_UPDATEPOPUP, System.currentTimeMillis());
                baseApplication.editor.apply();
            } else
                Log.d("version manifest lon hơn. khong update");
        } else if (baseApplication.getBaseConfig().getUpdate().getStatus() == 2) {
            AlertDialog dialog = Dialog_Update(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.show();
            baseApplication.editor.putLong(BaseConstant.KEY_LASTTIME_SHOW_UPDATEPOPUP,
                    System.currentTimeMillis());
            baseApplication.editor.apply();
        }
    }

    private AlertDialog Dialog_Update(boolean two_button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(baseApplication.getBaseConfig().getUpdate().getTitle());
        builder.setMessage(baseApplication.getBaseConfig().getUpdate().getDescription());
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.base_update_bt_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (baseApplication.getBaseConfig().getUpdate().getType().equals("market")) {
                    BaseUtils.gotoUrl(BaseActivity.this, baseApplication.getBaseConfig().getUpdate().getUrl_store());
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(baseActivity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(baseActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(baseActivity,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    BaseConstant.REQUEST_PERMISSION_STORAGE);
                            return;
                        }
                    }
                    new DownloadFileApk().execute(baseApplication.getBaseConfig().getUpdate().getUrl_store());
                }
                dialog.dismiss();
            }
        });
        if (two_button) {
            builder.setNegativeButton(getResources().getString(R.string.base_update_bt_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        return builder.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BaseConstant.REQUEST_SHOW_POPUP_CUSTOM) {
            onClose(baseApplication.getPopup().getTempObject());
        } else if (requestCode == BaseConstant.REQUEST_IN_APP_BILLING) {
            if (mHelper == null) return;
            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.d("onActivityResult handled by IABUtil.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BaseConstant.REQUEST_PERMISSION_STORAGE) {
            if (ContextCompat.checkSelfPermission(baseActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(baseActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                BaseUtils.gotoUrl(BaseActivity.this, baseApplication.getBaseConfig().getUpdate().getUrl_store());
                return;
            }
            new DownloadFileApk().execute(baseApplication.getBaseConfig().getUpdate().getUrl_store());
        }
    }

    private class DownloadFileApk extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(baseActivity);
            pDialog.setMessage(getString(R.string.base_downloading));
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(1);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String filePath = Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".apk";
                URL url = new URL(params[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 0x2000);
                OutputStream output = new FileOutputStream(filePath);
                byte[] data = new byte[1024];
                long total = 0;
                while (true) {
                    int count = input.read(data);
                    if (count == -1) {
                        break;
                    }
                    total += (long) count;
                    publishProgress((int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return filePath;
            } catch (Exception e) {
                Log.e("Error download file: " + e.getMessage());
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (pDialog != null)
                pDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog != null) pDialog.dismiss();

            if (!s.equals(""))
                baseActivity.startActivity(new Intent("android.intent.action.VIEW").setDataAndType(
                        Uri.parse("file://" + s), "application/vnd.android.package-archive"));
            else
                Toast.makeText(baseActivity, getResources().getString(R.string.base_download_fail), Toast.LENGTH_SHORT).show();
        }
    }
}

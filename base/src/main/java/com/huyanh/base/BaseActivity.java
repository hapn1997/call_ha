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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import com.github.piasy.safelyandroid.component.support.SafelyAppCompatActivity;
import com.huyanh.base.custominterface.PopupListener;
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

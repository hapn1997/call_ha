package com.huyanh.base.ads;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.huyanh.base.R;
import com.huyanh.base.utils.BaseUtils;
import com.huyanh.base.utils.Log;


public class OtherAppLauncher extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_app);

        Bundle args = getIntent().getExtras();

        if (args == null) finish();
        else {
            String url_store = args.getString("link");
            String listPackageName[];
            if (getIntent().getExtras().getString("package_name").contains(",")) {
                listPackageName = getIntent().getExtras().getString("package_name").split(",");
            } else {
                listPackageName = new String[]{getIntent().getExtras().getString("package_name")};
            }

            boolean isInstalled = false;
            String packageNameInstalled = "";

            for (String packageName : listPackageName) {
                Log.d("packageName: " + packageName);
                if (BaseUtils.isInstalled(this, packageName)) {
                    isInstalled = true;
                    packageNameInstalled = packageName;
                    break;
                }
            }

            if (isInstalled) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageNameInstalled);
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                } else BaseUtils.gotoUrl(this, url_store);
            } else {
                BaseUtils.gotoUrl(this, url_store);
            }

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
    }
}

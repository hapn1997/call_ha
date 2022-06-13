package com.huyanh.base.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huyanh.base.BaseActivity;
import com.huyanh.base.R;

public class UpgradePremiumActivity extends BaseActivity {


    private TextView tvTitleUpgradePremium;

    private RelativeLayout rlOk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_premium);

        tvTitleUpgradePremium = (TextView) findViewById(R.id.tvTitleUpgradePremium);
        tvTitleUpgradePremium.setText("Upgrade Now\nTo\nPremium Version");

        rlOk = (RelativeLayout) findViewById(R.id.rlOk);
        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseApplication.isPurchase) {
                    onBackPressed();
                } else {
                    purchase();
                }
            }
        });
        findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initInappBilling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendBroadcast(new Intent("show_chathead_icon"));
    }
}

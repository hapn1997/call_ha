package com.huyanh.base.ads;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.huyanh.base.BaseApplication;
import com.huyanh.base.R;
import com.huyanh.base.dao.BaseConfig;
import com.huyanh.base.utils.BaseUtils;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class PopupCustom extends AppCompatActivity {

    private BaseConfig.more_apps more_app = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_custom);

        BaseApplication baseApplication = (BaseApplication) getApplication();
        findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (baseApplication.getBaseConfig().getMore_apps().size() > 0) {
            more_app = baseApplication.getBaseConfig().getMore_apps().get(new Random().nextInt(baseApplication.getBaseConfig().getMore_apps().size()));
            ImageView iv = (ImageView) findViewById(R.id.ivPopup);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseUtils.gotoUrl(PopupCustom.this, more_app.getUrl_store());
                }
            });
            if (!more_app.getPopup().equals(""))
                Picasso.with(PopupCustom.this).load(more_app.getPopup()).into(iv);
            else onBackPressed();
        } else {
            onBackPressed();
        }
    }
}

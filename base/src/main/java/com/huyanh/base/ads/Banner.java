package com.huyanh.base.ads;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huyanh.base.BaseApplication;
import com.huyanh.base.R;
import com.huyanh.base.dao.BaseConfig;
import com.huyanh.base.utils.BaseUtils;
import com.huyanh.base.utils.Log;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class Banner extends RelativeLayout {
    private LinearLayout mLnrAdview;
    private BaseApplication baseApplication;

    private boolean isThumbnail = false;

    public Banner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAttributes(attrs);
        initView();
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        initView();
    }

    public Banner(Context context, boolean isThumbnail) {
        super(context);
        initView();
    }

    private void setAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.Banner);
            if (a.hasValue(R.styleable.Banner_isThumbnail)) {
                isThumbnail = a.getBoolean(
                        R.styleable.Banner_isThumbnail,
                        false);
            }
            a.recycle();
        }
    }

    private void initView() {
        View mView;
        mView = inflate(getContext(), R.layout.ads_banner, null);
        addView(mView);
        mLnrAdview = (LinearLayout) mView.findViewById(R.id.ads_banner_ll);
        mLnrAdview.removeAllViews();

        baseApplication = (BaseApplication) getContext().getApplicationContext();
        loadAds();
    }

    private ImageView ivAdsVietmobi = null;


    public void loadAds() {
        if (baseApplication.isPurchase) {
            mLnrAdview.removeAllViews();
            return;
        }

        try {
            if (new Random().nextInt(100) < (baseApplication.getBaseConfig().getThumnail_config().getRandom_show_thumbai_hdv()) && baseApplication.getBaseConfig().getMore_apps().size() > 0) {
                ivAdsVietmobi = new ImageView(getContext());
                if (isThumbnail)
                    ivAdsVietmobi.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                else
                    ivAdsVietmobi.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, BaseUtils.genpx(getContext(), 50)));

                ivAdsVietmobi.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivAdsVietmobi.setAdjustViewBounds(true);

                BaseConfig.more_apps more_apps = baseApplication.getBaseConfig().getMore_apps().get(new Random().nextInt(baseApplication.getBaseConfig().getMore_apps().size()));
                if (isThumbnail) {
                    if (!more_apps.getThumbai().equals(""))
                        Picasso.with(getContext()).load(more_apps.getThumbai()).into(ivAdsVietmobi, new Callback() {
                            @Override
                            public void onSuccess() {
                                mLnrAdview.removeAllViews();
                                mLnrAdview.addView(ivAdsVietmobi);
                            }

                            @Override
                            public void onError() {
                            }
                        });
                } else {
                    if (!more_apps.getBanner().equals(""))
                        Picasso.with(getContext()).load(more_apps.getBanner()).into(ivAdsVietmobi, new Callback() {
                            @Override
                            public void onSuccess() {
                                mLnrAdview.removeAllViews();
                                mLnrAdview.addView(ivAdsVietmobi);
                            }

                            @Override
                            public void onError() {
                            }
                        });
                }
                mLnrAdview.setOnClickListener(new impleOnClick(more_apps.getUrl_store()));
            } else {
                loadAdmob();
            }
        } catch (Exception e) {
            Log.e("error show adsview HDV: " + e.getMessage());
        }
    }

    private class impleOnClick implements OnClickListener {
        String url_store = "";

        impleOnClick(String url_store) {
            this.url_store = url_store;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse(url_store));
            getContext().startActivity(i);
        }
    }

    private void loadAdmob() {
        Log.v("load admob " + (isThumbnail ? "thumbnail" : "banner") + " " + baseApplication.getBaseConfig().getKey().getAdmob().getBanner());
    }

}

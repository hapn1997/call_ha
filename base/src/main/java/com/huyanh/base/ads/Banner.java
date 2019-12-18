package com.huyanh.base.ads;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.huyanh.base.BaseApplication;
import com.huyanh.base.R;
import com.huyanh.base.dao.BaseConfig;
import com.huyanh.base.utils.BaseConstant;
import com.huyanh.base.utils.BaseUtils;
import com.huyanh.base.utils.Log;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class Banner extends RelativeLayout {
    private AdView mAdViewAbmob;
    private com.facebook.ads.AdView mAdViewFacebook;
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
                if (baseApplication.getBaseConfig().getAds_network_new().getBanner().equals("admob")) {
                    loadAdmob();
                } else {
                    loadFacebook();
                }
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
        AdRequest mAdRequest;
        if (BaseConstant.isDebugging) {
            String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceId = BaseUtils.md5(android_id).toUpperCase();
            mAdRequest = new Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(deviceId)
                    .build();
        } else {
            mAdRequest = new Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
        }
        this.mAdViewAbmob = new AdView(getContext());
        if (isThumbnail)
            mAdViewAbmob.setAdSize(AdSize.MEDIUM_RECTANGLE);
        else
            mAdViewAbmob.setAdSize(AdSize.SMART_BANNER);

        this.mAdViewAbmob.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.mAdViewAbmob
                .setAdUnitId(baseApplication.getBaseConfig().getKey().getAdmob().getBanner());
        this.mAdViewAbmob.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                super.onAdFailedToLoad(errorCode);
                Log.e("admob banner fail: " + errorCode);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("onAdLoaded banner");
                mLnrAdview.removeAllViews();
                mLnrAdview.addView(Banner.this.mAdViewAbmob);
            }

            public void onAdOpened() {
            }
        });
        this.mAdViewAbmob.loadAd(mAdRequest);
    }

    private void loadFacebook() {
        Log.v("load facebook banner: " + baseApplication.getBaseConfig().getKey().getFacebook().getBanner());
        mAdViewFacebook = new com.facebook.ads.AdView(getContext(), baseApplication.getBaseConfig().getKey().getFacebook().getBanner(), isThumbnail ? com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250 : com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        mAdViewFacebook.setAdListener(new AbstractAdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                Log.e("facebook banner fail: " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                mLnrAdview.removeAllViews();
                mLnrAdview.addView(Banner.this.mAdViewFacebook);
            }
        });
        // Request to load an ad
        mAdViewFacebook.loadAd();
    }

    public void destroy() {
        if (mAdViewFacebook != null) mAdViewFacebook.destroy();
        if (mAdViewAbmob != null) mAdViewAbmob.destroy();
    }

    public void pause() {
        if (mAdViewAbmob != null) mAdViewAbmob.pause();
    }

    public void resume() {
        if (mAdViewAbmob != null) mAdViewAbmob.resume();
    }

}

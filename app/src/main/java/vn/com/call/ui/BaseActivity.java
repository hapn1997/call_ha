package vn.com.call.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.piasy.safelyandroid.component.support.SafelyAppCompatActivity;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import vn.com.call.R;
import vn.com.call.db.Settings;
import vn.com.call.utils.ColorUtils;


/**
 * Created by ngson on 12/06/2017.
 */

public abstract class BaseActivity extends com.huyanh.base.BaseActivity {
    private final String TAG = super.getClass().getSimpleName();

    public final static String EXTRA_FROM_SERVICE = "from_service";

    private Unbinder mUnbinder;

    private boolean isFromService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFromService = getIntent().getBooleanExtra(EXTRA_FROM_SERVICE, false);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setStatusBarColor(ColorUtils.getDarkColor(ContextCompat.getColor(this, R.color.colorPrimary)));

        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        sendBroadcast(new Intent(TouchBarService.ACTION_REMOVE_CHATHEAD_ICON));
//        sendBroadcast(new Intent(TouchBarService.ACTION_DISMISS_TOUCHBAR));
//
//        if (isNeedCheckLockApp() && AppLock.isLock(LockSession.LOCK_APP)) {
//            Intent lockIntent = new Intent(this, LockScreenActivity.class);
//            lockIntent.putExtra(LockScreenActivity.EXTRA_LOCK_SESSION, LockSession.LOCK_APP);
//
//            startActivity(lockIntent);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Settings.getInstance(this).setCurrentConversation(-1l);
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(color);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Settings settings = Settings.getInstance(newBase);

        String language;

        if (settings.isUseDeviceLanguage()) {
            if (Build.VERSION.SDK_INT >= 24)
                language = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
            else language = Resources.getSystem().getConfiguration().locale.getLanguage();
        } else language = settings.getLanguage();

        Configuration config = newBase.getResources().getConfiguration();
        Locale locale = new Locale(language);
        if (Build.VERSION.SDK_INT >= 24) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= 25) {
            Context context = newBase.createConfigurationContext(config);
            super.attachBaseContext(context);
        } else {
            newBase.getResources().updateConfiguration(config, newBase.getResources().getDisplayMetrics());
            super.attachBaseContext(newBase);
        }
    }

    protected abstract int getLayoutId();

    protected boolean isNeedCheckLockApp() {
        return true && Settings.getInstance(this).enableLockApp() && !isFromService;
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }
}

package vn.com.call.ui.intro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import android.telecom.TelecomManager;

import com.huyanh.base.utils.BaseUtils;

import com.dialer.ios.iphone.contacts.R;
import vn.com.call.db.Settings;
import vn.com.call.ui.BaseActivity;
import vn.com.call.utils.PermissionUtils;


/**
 * Created by ngson on 29/06/2017.
 */

public class IntroActivity extends BaseActivity implements IntroCallback {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PermissionUtils.isAllPermissionGrantedAtIntro(this)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, new SplashFragment());
            safeCommit(ft);
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, new IntroFragment());
            safeCommit(ft);
        }

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        // create default notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String channelId = getString(R.string.default_notification_channel_id);
            final String channelName = getString(R.string.default_floatingview_channel_name);
            final NotificationChannel defaultChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN);

            NotificationChannel newMessageChannel = new NotificationChannel(getString(R.string.new_message_channel_id), getString(R.string.new_message_channel_name), NotificationManager.IMPORTANCE_HIGH);
            final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(defaultChannel);
                manager.createNotificationChannel(newMessageChannel);
            }
        }
    }

    @Override
    protected boolean isNeedCheckLockApp() {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == IntroFragment.PERMISSION_REQUEST_CODE) {
            Settings.getInstance(this).setRequestedPermissionIntro(true);

            if (PermissionUtils.isAllPermissionGrantedAtIntro(this)) completeRequestPermission();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_intro;
    }

    @Override
    public void completeRequestPermission() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, new SplashFragment());
        ft.addToBackStack(null);
        safeCommit(ft);
    }

}

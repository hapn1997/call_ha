package vn.com.call.ui.intro;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import android.telecom.TelecomManager;
import android.widget.Button;


import butterknife.BindView;
import butterknife.OnClick;
import com.phone.thephone.call.dialer.R;
import vn.com.call.db.Settings;
import vn.com.call.ui.BaseFragment;
import vn.com.call.utils.PermissionUtils;

/**
 * Created by ngson on 30/06/2017.
 */

public class IntroFragment extends BaseFragment {
    private static final String TAG = IntroActivity.class.getSimpleName();

    public static final int PERMISSION_REQUEST_CODE = 68;
    private boolean missingManageSettings = false;

    @BindView(R.id.continues)
    Button mContinues;

    @OnClick(R.id.continues) void continues() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onRequestDefaultPhone();
        }
        if (PermissionUtils.isAllPermissionGrantedAtIntro(getContext())) {
            goMain();
        } else if (mSettings.isRequestedPermissionIntro() && isNeedToSystemSettingsPermissions()) {
            goSystemSettingPermissionsApp();
        } else {
            requestPermission();
        }
    }

    private Settings mSettings;

    private IntroCallback mIntroCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettings = Settings.getInstance(getContext());
        if (savedInstanceState != null) {
            this.missingManageSettings = savedInstanceState.getBoolean("MANAGE_SETTINGS_MISSING");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onRequestDefaultPhone();
        }

       // canWriteSettings();
       // startWriteSettingsActivity();
    }
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("MANAGE_SETTINGS_MISSING", this.missingManageSettings);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestDefaultPhone() {
        if (isDefaultDialer()) {
            makeDefaultDialer();
            // moveToNextPageOrFinish();
        } else {
            makeDefaultDialer();
        }
    }
    private void makeDefaultDialer()
    {
        Intent localIntent = new Intent("android.telecom.action.CHANGE_DEFAULT_DIALER");
        localIntent.putExtra("android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME", getContext().getPackageName());
        try
        {
            startActivityForResult(localIntent, 1121);
            return;
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
            for (;;) {}
        }
        //showActivityNotFoundAlertDialog();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private String getDefaultDialerPackage() {
        return ((TelecomManager) getContext().getSystemService(Context.TELECOM_SERVICE)).getDefaultDialerPackage();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isDefaultDialer() {
        return getContext().getPackageName().equals(getDefaultDialerPackage());
    }
    private boolean canWriteSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!android.provider.Settings.System.canWrite(getContext())) {
                if (!this.missingManageSettings) {
                    return false;
                }
            }
        }
        return true;
    }
//    private void startWriteSettingsActivity()
//    {
//        Intent localIntent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
//        StringBuilder localStringBuilder = new StringBuilder();
//        localStringBuilder.append("package:");
//        localStringBuilder.append(getApplicationContext().getPackageName());
//        localIntent.setData(Uri.parse(localStringBuilder.toString()));
//        try
//        {
//            startActivityForResult(localIntent, 1123);
//            return;
//        }
//        catch (ActivityNotFoundException localActivityNotFoundException)
//        {
//            for (;;) {}
//        }
//       // this.missingManageSettings = true;
//       // moveToNextPageOrFinish();
//    }
    @Override
    protected void onCreateView(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_intro;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null) mIntroCallback = (IntroCallback) getActivity();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), PermissionUtils.getPermissionsNeedGrantAtIntro(getContext()), PERMISSION_REQUEST_CODE);
    }

    private boolean isNeedToSystemSettingsPermissions() {
        return  !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), PermissionUtils.PERMISSION_PHONE)
                && !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), PermissionUtils.PERMISSION_CONTACTS)
                && !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), PermissionUtils.PERMISSION_READ_CONTACTS)
                && !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), PermissionUtils.PERMISSION_READ_PHONE_STATE)
                && !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), PermissionUtils.PERMISSION_CALL_PHONE);
    }

    private void goSystemSettingPermissionsApp() {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void goMain() {
        if (mIntroCallback != null) mIntroCallback.completeRequestPermission();
    }
}

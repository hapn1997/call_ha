package vn.com.call.ui.main;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.telecom.TelecomManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.huyanh.base.activity.UpgradePremiumActivity;
import com.huyanh.base.utils.BaseConstant;
import com.huyanh.base.utils.Log;
import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavTransactionOptions;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.schedulers.Schedulers;
import com.dialer.ios.iphone.contacts.R;
import vn.com.call.bus.ShowDialpad;
import vn.com.call.db.SmsHelper;
import vn.com.call.db.cache.CallLogHelper;
import vn.com.call.global.DialogAppCallback;
import vn.com.call.global.DialogPermission;
import vn.com.call.ui.BaseActivity;
import vn.com.call.ui.search.SearchFragment;


public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    @BindView(R.id.search)
    ImageButton mSearch;
    @BindView(R.id.input_search)
    EditText mInputSearch;
    @BindView(R.id.clear)
    ImageButton mClear;
    @BindView(R.id.bottom_bar)
    BottomBar mBottomBar;
    @BindView(R.id.search_content)
    FrameLayout mSearchContainer;



    private void goDialer() {
        if (mFragNavController.getCurrentStackIndex() != FragNavController.TAB4) {
            mFragNavController.switchTab(FragNavController.TAB4);
        }
    }

    @OnClick(R.id.search)
    void search() {
        if (mSearchContainer.getVisibility() == View.GONE) {
            mInputSearch.requestFocus();
        } else {
            mInputSearch.clearFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mInputSearch.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.clear)
    void clearSearch() {
        mInputSearch.setText("");
    }

    private FragNavController mFragNavController;

    private SearchFragment mSearchFragment;

    private boolean isSavedInstanceState;

    private vn.com.call.db.Settings mSettings;

    //private BottomDialogGrantPermissionFragment mDialogGrantDrawPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettings = vn.com.call.db.Settings.getInstance(this);

        setupFragNav(savedInstanceState);
        setupBottomBar();
        initSearch();

//        loadCallLog();
        //boquyen cacheAllSms();
       //hasua
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
//            setDefaultCallAppApi30();
//        }else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                checkDefaultHandler();
//            }
//        }
//
//        AppShortcutUtils.createAppShortcuts(this);
//
//        initInappBilling();
//
//        ((App) getApplication()).loadData();
    }

    @Override
    public void onPause() {
        super.onPause();

        mFragNavController.onActivityPause();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
//            setDefaultCallAppApi30();
//        }else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                checkDefaultHandler();
//            }
//        }
        mFragNavController.onResumeFragments();

        isSavedInstanceState = false;

        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (baseApplication.isPurchase) {
           // ivUpgrade.setVisibility(View.GONE);
        } else {
          //  ivUpgrade.setVisibility(View.VISIBLE);
        }

        showTouchBar();

//        if (!isPermissionToReadHistory()) {
//            DialogPermission.showDialog(this, new DialogAppCallback() {
//                @Override
//                public void cancelDialog() {
//                }
//
//                @Override
//                public void okDialog() {
//                    requestReadHistoryAccess();
//                }
//            });
//        }
    }


    @Subscribe
    public void onEvent(ShowDialpad event) {
        mBottomBar.selectTabAtPosition(2);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mFragNavController != null) {
            mFragNavController.onSaveInstanceState(outState);
        }

        isSavedInstanceState = true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public void commit(FragmentTransaction ft) {
        safeCommit(ft);
    }

    private void setupFragNav(Bundle savedInstanceState) {
        FragNavController.Builder builder = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_main);

        FragNavTransactionOptions.Builder builderOptions = FragNavTransactionOptions.newBuilder();
        builderOptions.customAnimations(com.ncapdevi.fragnav.R.anim.fade_in, com.ncapdevi.fragnav.R.anim.fade_out);
        builder.defaultTransactionOptions(builderOptions.build());

        List<Fragment> fragments = new ArrayList<>();

        fragments.add(FavoriteFragment.newInstance());
        fragments.add(CallLogFragment.newInstance());
        fragments.add(ContactFragment.newInstance());
        fragments.add(new DialpadFragment());


//        fragments.add(ListThreadSmsFragment.newInstance());
//        fragments.add(CallLogFragment.newInstance());
//        fragments.add(new DialpadFragment());
//        fragments.add(ContactFragment.newInstance());
//        fragments.add(SettingsFragment.newInstance());

        builder.rootFragments(fragments);

        mFragNavController = builder.build();
        mFragNavController.switchTab(FragNavController.TAB1);

        mFragNavController.onActivityCreate();
    }

    private class runnableWaitResume implements Runnable {
        private Object object;

        runnableWaitResume(Object object) {
            this.object = object;
        }

        @Override
        public void run() {
            Log.v("runnableWaitResume run");
            if (isCommitterResumed()) {
                switch ((int) object) {
                    case FragNavController.TAB1:
                        mFragNavController.switchTab(FragNavController.TAB1);
                        break;
                    case FragNavController.TAB2:
                        mFragNavController.switchTab(FragNavController.TAB2);
                        break;
                    case FragNavController.TAB4:
                        goDialer();
                        break;
                    case FragNavController.TAB3:
                        mFragNavController.switchTab(FragNavController.TAB3);
                        break;
//                    case FragNavController.TAB5:
//                        mFragNavController.switchTab(FragNavController.TAB5);
//                        break;
                }
            } else {
                handler.postDelayed(this, 200);
            }
        }
    }

    @Override
    public void onDoneQuerryInappBilling() {
//        super.onDoneQuerryInappBilling();
//        if (mClear == null || ivUpgrade == null) return;
//
//        ivUpgrade.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, UpgradePremiumActivity.class));
//            }
//        });
//        if (!mClear.isShown()) {
//            if (baseApplication.isPurchase) {
//                ivUpgrade.setVisibility(View.GONE);
//            } else {
//                ivUpgrade.setVisibility(View.VISIBLE);
//            }
//        } else ivUpgrade.setVisibility(View.GONE);

    }

    @Override
    public void onClose(Object object) {
        super.onClose(object);
        if (object == null) return;
        if (object instanceof Integer)
            handler.postDelayed(new runnableWaitResume(object), 250);
    }

    private void setupBottomBar() {
        mBottomBar.setOnTabSelectListener(

                new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_message && mFragNavController.getCurrentStackIndex() != FragNavController.TAB1) {
                    if (!showPopup(FragNavController.TAB1, false)) {
                        mFragNavController.switchTab(FragNavController.TAB1);
                    }
                } else if (tabId == R.id.tab_call_log && mFragNavController.getCurrentStackIndex() != FragNavController.TAB2) {
                    if (!showPopup(FragNavController.TAB2, false)) {
                        mFragNavController.switchTab(FragNavController.TAB2);
                    }
                } else if (tabId == R.id.tab_dialpad) {
                    if (!showPopup(FragNavController.TAB4, false)) {
                        goDialer();
                    }
                } else if (tabId == R.id.tab_contact && mFragNavController.getCurrentStackIndex() != FragNavController.TAB3) {
                    if (!showPopup(FragNavController.TAB3, false)) {
                        mFragNavController.switchTab(FragNavController.TAB3);
                    }
                }
//                else if (tabId == R.id.tab_settings && mFragNavController.getCurrentStackIndex() != FragNavController.TAB5) {
//                    if (!showPopup(FragNavController.TAB5, false)) {
//                        mFragNavController.switchTab(FragNavController.TAB5);
//                    }
//                }
            }
        });
    }

    private void initSearch() {
        mInputSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSearch.setImageResource(R.drawable.ic_arrow_back_black_24dp);
                    mSearchContainer.setVisibility(View.VISIBLE);
                    mClear.setVisibility(View.VISIBLE);
                    //ivUpgrade.setVisibility(View.GONE);
                    mInputSearch.setHint(R.string.hint_search_main);

                    mSearchFragment = new SearchFragment();

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_content, mSearchFragment);
                    safeCommit(ft);
                } else {
                    mSearch.setImageResource(R.drawable.ic_search_black_24dp);
                    mSearchContainer.setVisibility(View.GONE);
                    mInputSearch.setHint(R.string.search);
                    mInputSearch.setText("");
                    mClear.setVisibility(View.GONE);
                    if (!baseApplication.isPurchase) {
                     //   ivUpgrade.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mSearchFragment != null) mSearchFragment.changeKeyword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void cacheAllSms() {
        SmsHelper.cacheAllMessage(this);
    }

    private void loadCallLog() {
        CallLogHelper.queryAllCallLog(this)
                .subscribeOn(Schedulers.newThread())
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribe();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkDefaultHandler() {
        if (isAlreadyDefaultDialer()) {
            return;
        }
        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else{
            throw new RuntimeException("Default phone functionality not found");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isAlreadyDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        return getPackageName().equals(telecomManager.getDefaultDialerPackage());
    }
    private void setDefaultCallAppApi30() {
        RoleManager roleManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            roleManager = getApplicationContext().getSystemService(RoleManager.class);
            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                if (roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
//                    Toast.makeText(getApplicationContext(), "PrismApp set as default.", Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
//                    startActivity(i);
                } else {
                    Intent roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                    startActivityForResult(roleRequestIntent, 2);
                }
            }
        }
    }

    private void showTouchBar() {
//        if (mSettings.isEnableChatHeads()) {
//            if ((Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this)) || Build.VERSION.SDK_INT < 23) {
//                Intent intent = new Intent(this, TouchBarService.class);
//                intent.putExtra("from", "MainActivity showChathead");
//                startService(intent);
//            } else {
//                mInputSearch.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showDialogGrantPermissionDrawOverApp();
//                    }
//                }, 100);
//            }
//        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void showDialogGrantPermissionDrawOverApp() {
//        if (!isSavedInstanceState && (mDialogGrantDrawPermission == null || !mDialogGrantDrawPermission.isVisible())) {
//            mDialogGrantDrawPermission = new BottomDialogGrantPermissionFragment();
//            mDialogGrantDrawPermission.setOnClickGoSetting(new BottomDialogGrantPermissionFragment.OnClickGoSetting() {
//                @Override
//                public void goSetting() {
//                    checkPermission();
//                }
//            });
//            mDialogGrantDrawPermission.show(getSupportFragmentManager(), "grant_permission");
//        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                mSettings.setEnableChatHeads(true);

             //   startService(new Intent(this, TouchBarService.class));
            }
        } else if (requestCode == BaseConstant.REQUEST_CODE_SHOW_POPUP) {
            Log.d("onActivityResult MainActivity REQUEST_CODE_SHOW_POPUP");
            showPopup(null, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mSearchContainer.getVisibility() == View.VISIBLE) mInputSearch.clearFocus();
        else finish();
    }


    private boolean isPermissionToReadHistory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        return false;
    }

    public void requestReadHistoryAccess() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        getApplicationContext().getPackageName(),
                        new AppOpsManager.OnOpChangedListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onOpChanged(String op, String packageName) {
                                try {
                                    int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                            android.os.Process.myUid(), getPackageName());
                                    if (mode != AppOpsManager.MODE_ALLOWED) {
                                        return;
                                    }
                                    appOps.stopWatchingMode(this);
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    if (getIntent().getExtras() != null) {
                                        intent.putExtras(getIntent().getExtras());
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(intent);
                                } catch (Exception e) {
                                }
                            }
                        });
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        } catch (Exception e) {
        }
    }
}
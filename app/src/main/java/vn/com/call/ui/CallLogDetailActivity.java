package vn.com.call.ui;

import android.Manifest;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import com.phone.thephone.call.dialer.R;
import vn.com.call.adapter.CallLogDetailAdapter;
import vn.com.call.editCall.CallerHelper;
import vn.com.call.model.calllog.CallLog;
import vn.com.call.utils.CallUtils;
import vn.com.call.widget.AvatarView;

/**
 * Created by ngson on 04/07/2017.
 */

@RuntimePermissions
public class CallLogDetailActivity extends BaseActivity {
    private final String TAG = CallLogDetailActivity.class.getSimpleName();

    public static final String EXTRA_CALL_LOG = "call_log";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.avatar)
    AvatarView mAvatar;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.number)
    TextView mNumber;
    @BindView(R.id.list)
    RecyclerView mList;

    @OnClick(R.id.call)
    void callNow() {
        CallLogDetailActivityPermissionsDispatcher.makeCallWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void makeCall() {
//        CallUtils.makeCall(this, mCallLog.getNumber());
        CallerHelper.startPhoneAccountChooseActivity(this, mCallLog.getNumber());
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    void showRationaleCallPhone(PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied(Manifest.permission.CALL_PHONE)
    void onDeniedCallPhone() {

    }

    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    void onNeverAskAgainCallPhone() {

    }

    private CallLog mCallLog;

    private CallLogDetailAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallLog = getIntent().getParcelableExtra(EXTRA_CALL_LOG);

        setupToolbar();
        showInfoPeople();
        showDetails();
    }

    private void setupToolbar() {
        mToolbar.setTitle(R.string.title_activity_call_log_detail);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mToolbar.inflateMenu(R.menu.activity_detail_call_log);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.delete) {
                   // mCallLog.delete(CallLogDetailActivity.this, true, true,null);
                }

                return false;
            }
        });
    }

    private void showInfoPeople() {
        mAvatar.loadAvatar(mCallLog.getPhotoContact(), mCallLog.getNameContact(), mCallLog.getNumber());
        mNumber.setText(mCallLog.getNumber());
        mName.setText(mCallLog.getNameContact() == null ? mCallLog.getNumber() : mCallLog.getNameContact());
    }

    private void showDetails() {
        mAdapter = new CallLogDetailAdapter(CallLogDetailActivity.this, mCallLog.getDetails());
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_call_log_detail;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        CallLogDetailActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}


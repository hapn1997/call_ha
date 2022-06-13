package vn.com.call.ui;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import com.dialer.ios.iphone.contacts.R;
import vn.com.call.adapter.CallLogByContactAdapter;
import vn.com.call.db.cache.CallLogHelper;
import vn.com.call.model.calllog.CallLogDetail;

/**
 * Created by ngson on 13/07/2017.
 */

public class ListCallLogByContactActivity extends BaseActivity implements ActionMode.Callback {
    private static final String TAG = ListCallLogByContactActivity.class.getSimpleName();

    public final static String EXTRA_LIST_CALL_LOG_DETAIL = "list_call_log_detail";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.list) RecyclerView mList;

    private List<CallLogDetail> mCallLogDetails;

    private CallLogByContactAdapter mAdapter;

    private Subscription mSubscriptionDeleteCallLog;

    private ActionMode mActionMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallLogDetails = getIntent().getParcelableArrayListExtra(EXTRA_LIST_CALL_LOG_DETAIL);

        initToolbar();
        initListCallLog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_call_log_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) deleteItems(mCallLogDetails);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_call_log_by_contact;
    }

    private void initToolbar() {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_primary_color_detail_contact_24dp);
        mToolbar.setTitle(R.string.call_history);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.primary_color_detail_contact));
        setSupportActionBar(mToolbar);
    }

    private void initListCallLog() {
        mAdapter = new CallLogByContactAdapter(R.layout.item_call_log_detail_list_calllog_by_contact, mCallLogDetails);
        mList.setAdapter(mAdapter);
        mList.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Log.wtf(TAG, "onItemLongClick " + position);
                if (mActionMode == null) startSupportActionMode(ListCallLogByContactActivity.this);
                return false;
            }
        });
    }

    private void deleteItems(final List<CallLogDetail> details) {
        new MaterialDialog.Builder(this)
                .backgroundColor(Color.WHITE)
                .content(getString(R.string.delete_items).replace("{number}", Integer.toString(details.size())))
                .contentColor(Color.BLACK)
                .negativeText(R.string.cancel)
                .negativeColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .positiveText(R.string.ok)
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startDeleteItems(details);
                    }
                }).show();
    }

    private void startDeleteItems(final List<CallLogDetail> details) {
        mSubscriptionDeleteCallLog = CallLogHelper.deleteCallLog(this, mCallLogDetails)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        CallLogHelper.deleteCallLog(ListCallLogByContactActivity.this, details);

                        if (details.size() == mCallLogDetails.size()) finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mSubscriptionDeleteCallLog != null) mSubscriptionDeleteCallLog.unsubscribe();
        super.onDestroy();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.wtf(TAG, "onCreateActionMode");
        mActionMode = mode;
        mActionMode.getMenuInflater().inflate(R.menu.action_mode_delete, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
    }
}

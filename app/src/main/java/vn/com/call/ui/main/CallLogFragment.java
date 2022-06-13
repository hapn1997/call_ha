package vn.com.call.ui.main;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huyanh.base.utils.BaseConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import com.dialer.ios.iphone.contacts.R;
import vn.com.call.adapter.CallLogAdapter;
import vn.com.call.adapter.listener.OnClickViewCallLogListener;
import vn.com.call.bus.ShowDialpad;
import vn.com.call.db.SharePref;
import vn.com.call.db.cache.CallLogCache;
import vn.com.call.db.cache.CallLogHelper;
import vn.com.call.model.calllog.CallLog;
import vn.com.call.model.contact.Contact;
import vn.com.call.ui.BaseFragment;
import vn.com.call.ui.callback.CallMaker;
import vn.com.call.ui.callback.SwipeToDeleteCallback;
import vn.com.call.utils.CallUtils;
import vn.com.call.utils.TimeUtils;
import vn.com.call.widget.FabBottomRecyclerView;

/**
 * Created by ngson on 28/06/2017.
 */

@RuntimePermissions
public class CallLogFragment extends BaseFragment implements CallMaker {
    private static final String TAG = CallLogFragment.class.getSimpleName();

    @BindView(R.id.list)
    FabBottomRecyclerView mList;
//    @BindView(R.id.dial)
//    FloatingActionButton mDial;
//
//    @OnClick(R.id.dial)
//    void showDial() {
//        EventBus.getDefault().post(new ShowDialpad());
//    }
    @BindView(R.id.norecents)
    TextView norecents;
    @BindView(R.id.btnLastHeaderDeleteAll)
    TextView btnLastHeaderDeleteAll;
    @BindView(R.id.btnLastHeaderEdit)
    TextView btnLastHeaderEdit;
    @BindView(R.id.btnLastHeaderAll)
     Button addlistCall;
    @BindView(R.id.btnLastHeaderMissed)
    Button btnLastHeaderMissed ;
    private CallLogAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<CallLogSectionEntity> mCallLogs = new ArrayList<>();
    private OnClickViewCallLogListener mOnClickViewCallLogListener;

    protected Subscription mSubscription;
    private  boolean checkaddMiss;
    public static CallLogFragment newInstance() {

        Bundle args = new Bundle();

        CallLogFragment fragment = new CallLogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreateView(Bundle savedInstanceState) {
//        mOnClickViewCallLogListener = new OnClickViewCallLogListener(getContext());
        mOnClickViewCallLogListener = new extOnClickViewCallLogListener(getContext());
        mAdapter = new CallLogAdapter(mOnClickViewCallLogListener, mCallLogs,this);
        addlistCall.setSelected(true);
        btnLastHeaderMissed.setSelected(false);
        addlistCall.setTextColor(Color.WHITE);

        addlistCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnLastHeaderMissed.setTextColor(getContext().getResources().getColor(R.color.colorBlue));
                addlistCall.setTextColor(Color.WHITE);

                addlistCall.setSelected(true);
                    btnLastHeaderMissed.setSelected(false);

                checkaddMiss = true;
                loadAndShowData();
            }
        });
        btnLastHeaderMissed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLastHeaderMissed.setTextColor(Color.WHITE);
               addlistCall.setTextColor(getContext().getResources().getColor(R.color.colorBlue));
                addlistCall.setSelected(false);
                btnLastHeaderMissed.setSelected(true);
                checkaddMiss = false;
                loadandShowMiss();
            }
        });
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mList.setLayoutManager(mLayoutManager);
        mList.setAdapter(mAdapter);
        if(SharePref.check(getContext(),"checkitem") ==true ){
            mAdapter.checkRemoveLog(true);
            btnLastHeaderDeleteAll.setVisibility(View.VISIBLE);
            btnLastHeaderEdit.setText("Done");

        }else{
            btnLastHeaderDeleteAll.setVisibility(View.INVISIBLE);
            btnLastHeaderEdit.setText("Edit");
            mAdapter.checkRemoveLog(false);
        }
        btnLastHeaderEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter.check()){
                    setCancel();
                    return;
                }else {
                    setEdit();
                    return;
                }
            }
        });
        btnLastHeaderDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetting(CallLogFragment.this);
            }
        });

      //  mAdapter.enableSwipeToDeleteAndUndo1(mCallLogs);
    }


    public  void setEdit(){
        btnLastHeaderDeleteAll.setVisibility(View.VISIBLE);
        btnLastHeaderEdit.setText("Done");
        mAdapter.checkRemoveLog(true);
        if(SharePref.check(getContext(),"checkitem") ==false){
            SharePref.putKey(getContext(),"checkitem", String.valueOf(mAdapter.check()));
        }

        mList.getAdapter().notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();



    }
    public  void setCancel(){
        btnLastHeaderDeleteAll.setVisibility(View.INVISIBLE);
        btnLastHeaderEdit.setText("Edit");
        SharePref.remove(getContext(),"checkitem");

        mAdapter.checkRemoveLog(false);
        mList.getAdapter().notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();


    }

    public void showSetting(final Fragment fragment){
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View deleteDialogView = factory.inflate(R.layout.dialog_remove_fav, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getContext()).create();

//        TextView txt_message = deleteDialogView.findViewById(R.id.txt_message);
//        txt_message.setText("");
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        deleteDialog.getWindow().setLayout(-1, -1);
        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.relative_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();


            }
        });
        deleteDialogView.findViewById(R.id.txt_mess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // controller.deleteDevice(contact,holder.callorMess.getText().toString());
                new CallLog().deleteAllCallLogs12(getContext(),false);
                deleteDialog.dismiss();
                btnLastHeaderDeleteAll.setVisibility(View.INVISIBLE);
                btnLastHeaderEdit.setText("Edit");
                SharePref.remove(getContext(),"checkitem");

                mAdapter.checkRemoveLog(false);
                mList.getAdapter().notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();
                fragment.getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();


            }
        });

        deleteDialogView.findViewById(R.id.txt_cancel_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();

            }
        });


        deleteDialog.show();
    }
    private class extOnClickViewCallLogListener extends OnClickViewCallLogListener {

        public extOnClickViewCallLogListener(Context context) {
            super(context);
        }

        @Override
        public void onClickAvatar(CallLog callLog, View avatar) {
            super.onClickAvatar(callLog, avatar);
            //onClickAvatar(getActivity(), callLog, avatar, BaseConstant.REQUEST_CODE_SHOW_POPUP);
        }

        @Override
        public void onClickCall(String number) {
            super.onClickCall(number);
//            onClickCall(getActivity(), number, BaseConstant.REQUEST_CODE_SHOW_POPUP);
        }

        @Override
        public void onClickInfo(CallLog callLog) {
//            super.onClickInfo(callLog);
            goCallLogDetail(getActivity(), callLog, BaseConstant.REQUEST_CODE_SHOW_POPUP);
        }
    }


//    @Override
//    public void onClose(Object object) {
//        super.onClose(object);
//        if (object == null) return;
//        if (object instanceof String) {
//            CallUtils.makeCall(getActivity(), String.valueOf(object));
//        }
//        if (object instanceof CallLog) {
//            mOnClickViewCallLogListener.goCallLogDetail((CallLog) object);
//        }
//    }

    protected void commitData(List<CallLog> callLogs) {
        mCallLogs.clear();

        if (callLogs.size() > 0) {
            int numberCallLog = callLogs.size();
            int index = 0;
            CallLog callLog = callLogs.get(index);

            if (TimeUtils.isToday(callLog.getDetails().get(0).getDate())) {
                mCallLogs.add(new CallLogSectionEntity(true, getString(R.string.today)));

                while (index < numberCallLog) {
                    callLog = callLogs.get(index);
                    if (TimeUtils.isToday(callLog.getDetails().get(0).getDate())) {
                        mCallLogs.add(new CallLogSectionEntity(callLog));
                        index++;
                    } else {
                        break;
                    }
                }
            }

            if (TimeUtils.isYesterday(callLog.getDetails().get(0).getDate())) {
                mCallLogs.add(new CallLogSectionEntity(true, getString(R.string.yesterday)));

                while (index < numberCallLog) {
                    callLog = callLogs.get(index);

                    if (TimeUtils.isYesterday(callLog.getDetails().get(0).getDate())) {
                        mCallLogs.add(new CallLogSectionEntity(callLog));
                        index++;
                    } else break;
                }
            }

            if (index < numberCallLog) {
                mCallLogs.add(new CallLogSectionEntity(true, getString(R.string.older)));
                while (index < numberCallLog) {
                    callLog = callLogs.get(index);
                    mCallLogs.add(new CallLogSectionEntity(callLog));
                    index++;
                }
            }

        }
        if(mCallLogs.size() ==0){
            norecents.setVisibility(View.VISIBLE);
        }else {
            norecents.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAndShowData();
    }

    public void hideDial() {
      //  mDial.setVisibility(View.GONE);
    }

    public void loadAndShowData() {
        showCacheCallLog();

        CallLogFragmentPermissionsDispatcher.queryCallLogWithCheck(this);
    }
    public void loadandShowMiss(){
        showCacheCallLog();
        CallLogFragmentPermissionsDispatcher.queryCallLogMisWithCheck(this);

    }

    private void showCacheCallLog() {
        commitData(CallLogCache.getCallLogs());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        CallLogFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_call_log;
    }

    @NeedsPermission({Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS})
    void queryCallLog() {
        mSubscription = CallLogHelper.queryAllCallLog(getContext())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CallLog>>() {
                    @Override
                    public void call(List<CallLog> callLogs) {
                        commitData(callLogs);
                    }

                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
    @NeedsPermission({Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS})
    void queryCallLogMis(){
        mSubscription = CallLogHelper.queryMissCallLog(getContext())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CallLog>>() {
                    @Override
                    public void call(List<CallLog> callLogs) {
                        commitData(callLogs);
                    }

                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
    @OnShowRationale({Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS})
    void showRationaleReadCallLog(final PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied({Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS})
    void onDeniedForReadCallLog() {

    }

    @OnNeverAskAgain({Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS})
    void onNeverAskForCallLog() {

    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    public void makeCall(String number) {
        CallUtils.makeCall(getContext(), number);
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    public void onShowRationaleCallPhone(PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied(Manifest.permission.CALL_PHONE)
    public void onDeniedCallPhone() {

    }

    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    public void onNeverAskAgainCallPhone() {

    }

    @Override
    public void onDestroyView() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void callNow(String number) {
        CallLogFragmentPermissionsDispatcher.makeCallWithCheck(this, number);
    }
}

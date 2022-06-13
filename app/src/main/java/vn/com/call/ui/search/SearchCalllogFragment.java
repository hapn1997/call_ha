package vn.com.call.ui.search;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import vn.com.call.bus.ChangeKeyword;
import vn.com.call.db.cache.CallLogCache;
import vn.com.call.model.calllog.CallLog;
import vn.com.call.ui.main.CallLogFragment;


/**
 * Created by ngson on 28/07/2017.
 */

public class SearchCalllogFragment extends CallLogFragment {
    private final static String EXTRA_KEYWORD = "keyword";

    private String mKeyword;

    public static SearchCalllogFragment newInstance(String keyword) {
        Bundle args = new Bundle();
        args.putString(EXTRA_KEYWORD, keyword);

        SearchCalllogFragment fragment = new SearchCalllogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mKeyword = getArguments().getString(EXTRA_KEYWORD);
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.onCreateView(savedInstanceState);
        }

        hideDial();
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ChangeKeyword keyword) {
        mKeyword = keyword.keyword;
        loadAndShowData();
    }

    @Override
    public void loadAndShowData() {
        mSubscription = CallLogCache.findCalllogByKeyword(mKeyword)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CallLog>>() {
                    @Override
                    public void call(List<CallLog> callLogs) {
                        commitData(callLogs);
                    }
                });
    }
}

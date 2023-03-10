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
import vn.com.call.db.cache.ContactCache;
import vn.com.call.model.contact.Contact;
import vn.com.call.ui.main.ContactFragment;

/**
 * Created by ngson on 28/07/2017.
 */

public class SearchContactFragment extends ContactFragment {
    private final static String EXTRA_KEYWORD = "keyword";

    private String mKeyword;

    public static SearchContactFragment newInstance(String keyword) {
        Bundle args = new Bundle();
        args.putString(EXTRA_KEYWORD, keyword);

        SearchContactFragment fragment = new SearchContactFragment();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.onCreateView(savedInstanceState);
        }
        hideButtonAddContact();
        //loadAndShowData();
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
    protected boolean hasHeader() {
        return false;
    }

    @Override
    protected void loadAndShowData() {
        mLoadContact = ContactCache.findContactByKeyword(mKeyword)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Contact>>() {
                    @Override
                    public void call(List<Contact> contacts) {
                        commitData(contacts);
                    }
                });
    }
}

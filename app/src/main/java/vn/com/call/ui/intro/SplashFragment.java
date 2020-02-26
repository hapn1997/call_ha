package vn.com.call.ui.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;


import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import com.dialer.ios.iphone.contacts.R;
import vn.com.call.db.ContactHelper;
import vn.com.call.db.Db;
import vn.com.call.db.cache.ContactCache;
import vn.com.call.db.cache.RecipientIdsCache;
import vn.com.call.model.contact.Contact;
import vn.com.call.ui.BaseFragment;
import vn.com.call.ui.main.MainActivity;

/**
 * Created by ngson on 30/06/2017.
 */

public class SplashFragment extends BaseFragment {
    private Subscription mSubscription;

    private Db db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new Db(getContext());
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
       //boquyen RecipientIdsCache.init(getContext());

        if (ContactCache.existCache()) {
            startMainActivity();

            loadContact();
        } else {
            mSubscription = ContactHelper.getListContact(getContext().getApplicationContext())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<Contact>>() {
                        @Override
                        public void call(List<Contact> contacts) {
                            startMainActivity();
                        }
                    });
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_splash;
    }

    private void startMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void loadContact() {
        mSubscription = ContactHelper.getListContact(getContext().getApplicationContext())
                .subscribeOn(Schedulers.newThread())
                .subscribe();
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.onDestroy();
    }
}

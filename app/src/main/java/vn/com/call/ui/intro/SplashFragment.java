package vn.com.call.ui.intro;

import static android.content.Context.TELECOM_SERVICE;

import android.app.role.RoleManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import com.phone.thephone.call.dialer.R;
import vn.com.call.db.ContactHelper;
import vn.com.call.db.Db;
import vn.com.call.db.cache.ContactCache;
import vn.com.call.db.cache.RecipientIdsCache;
import vn.com.call.model.contact.Contact;
import vn.com.call.ui.BaseFragment;
import vn.com.call.ui.main.MainActivity;
import vn.com.call.ui.main.PermisstionActivity;

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
    private void setDefaultCallAppApi30() {
        RoleManager roleManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            roleManager = getContext().getSystemService(RoleManager.class);
            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                if (roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Intent intent = new Intent(getContext(), PermisstionActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        }
    }
    private void startMainActivity() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
            setDefaultCallAppApi30();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkDefaultHandler();
            }
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isAlreadyDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getContext().getSystemService(TELECOM_SERVICE);
        return getContext().getPackageName().equals(telecomManager.getDefaultDialerPackage());
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkDefaultHandler() {
        if (isAlreadyDefaultDialer()) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else {
            Intent intent = new Intent(getContext(), PermisstionActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

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

package vn.com.call.ui.main;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
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
import vn.com.call.R;
import vn.com.call.adapter.HorizontalContactAdapter;
import vn.com.call.db.ContactHelper;
import vn.com.call.db.SharePref;
import vn.com.call.db.cache.ContactCache;
import vn.com.call.favController;
import vn.com.call.model.contact.Contact;
import vn.com.call.ui.BaseFragment;
import vn.com.call.widget.FabBottomRecyclerView;

import static vn.com.call.db.ContactHelper.convertToSectionEntity;

@RuntimePermissions

public class FavoriteFragment extends BaseFragment {
    @BindView(R.id.norecents)
    TextView norecents;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.relative_tool_bar)
    RelativeLayout relative_tool_bar;
    @BindView(R.id.look)
    FrameLayout look;
    @BindView(R.id.new_message1)
    ImageView addFavourite;
    @BindView(R.id.list_fav)
    FabBottomRecyclerView mlist;
    private List<Contact> mFavoriteContacts = new ArrayList<>();
    private List<ContactSectionEntity> contactSectionEntities = new ArrayList<>();
    FavoritesAddFragment mCurrentOverlay;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LinearLayoutManager mLayoutManager;
    favController controller ;
    @BindView(R.id.tvTabEdit)
    TextView tvTabEdit;
    HorizontalContactAdapter horizontalContactAdapter;
    // TODO: Rename and change types of parameters
    @BindView(R.id.tvTabTitle1)
    TextView tvTabTitle1;

    protected Subscription mLoadContact;
    public FavoriteFragment() {
        // Required empty public constructor
    }



    // TODO: Rename and change types and number of parameters
    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        if(R.id.look != 0){
        }
    }


    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        controller  = new favController(getContext());
        final FavoritesAddFragment favoritesAddFragment = new FavoritesAddFragment();
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mlist.setLayoutManager(mLayoutManager);
        loadAndShowData();
       horizontalContactAdapter=  new HorizontalContactAdapter(this,mFavoriteContacts);
       horizontalContactAdapter.notifyDataSetChanged();
        mlist.setAdapter(horizontalContactAdapter);
        mlist.getAdapter().notifyDataSetChanged();

        addFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().replace(R.id.frmen ,favoritesAddFragment).addToBackStack("FavoriteFragment").commitAllowingStateLoss();
                 relative_tool_bar.setVisibility(View.GONE);
            }
        });


        if(SharePref.check(getContext(),"checkitem1") ==true ){
            horizontalContactAdapter.checkRemove(true);
            tvTabEdit.setText("Done");

        }else{
            tvTabEdit.setText("Edit");
            horizontalContactAdapter.checkRemove(false);
        }
        tvTabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(horizontalContactAdapter.check()){
                setCancel();
                Toast.makeText(getContext(),"ccddcdd",Toast.LENGTH_LONG).show();
                return;

            }else{
                Toast.makeText(getContext(),"edeefef",Toast.LENGTH_LONG).show();
                setEdit();
                return;


            }

            }
        });

        appBarLayout.getTotalScrollRange();
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if(i== (-appBarLayout.getTotalScrollRange())){
                    tvTabTitle1.setVisibility(View.VISIBLE);

                    return;
                }
                tvTabTitle1.setVisibility(View.GONE);

            }
        });
        if(mFavoriteContacts.size() ==0){
            norecents.setVisibility(View.VISIBLE);
        }else {
            norecents.setVisibility(View.GONE);
        }

    }
    public  void setEdit(){
        tvTabEdit.setText("Done");
        horizontalContactAdapter.checkRemove(true);
        if(SharePref.check(getContext(),"checkitem1") ==false){
            SharePref.putKey(getContext(),"checkitem1", String.valueOf(horizontalContactAdapter.check()));
        }
        if(mFavoriteContacts.size() ==0){
            norecents.setVisibility(View.VISIBLE);
        }else {
            norecents.setVisibility(View.GONE);
        }
        mlist.getAdapter().notifyDataSetChanged();
        horizontalContactAdapter.notifyDataSetChanged();


    }
public  void setCancel(){
        tvTabEdit.setText("Edit");
    horizontalContactAdapter.checkRemove(false);
    SharePref.remove(getContext(),"checkitem1");
    if(mFavoriteContacts.size() ==0){
        norecents.setVisibility(View.VISIBLE);
    }else {
        norecents.setVisibility(View.GONE);
    }
    mlist.getAdapter().notifyDataSetChanged();
    horizontalContactAdapter.notifyDataSetChanged();


    }


    @Override
    public void onPause() {
        super.onPause();
        getChildFragmentManager().popBackStack();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favorite;

    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onDestroyView() {
        if (mLoadContact != null) mLoadContact.unsubscribe();
        super.onDestroyView();

        loadAndShowData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loadAndShowData();
    }

    protected void commitData(List<Contact> contacts) {
        contactSectionEntities.clear();

        contactSectionEntities.addAll(convertToSectionEntity(contacts));
            mFavoriteContacts.clear();
           mFavoriteContacts.addAll(ContactCache.getFavoriteContacts());
            // mFavoriteContacts.addAll(controller.getAllDevice());


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        FavoriteFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
    @Override
    public void onResume() {
        super.onResume();
        loadAndShowData();
    }
    protected void loadAndShowData() {
        commitData(ContactCache.getContacts());
        FavoriteFragmentPermissionsDispatcher.readContactWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.WRITE_CONTACTS)
    public void readContact() {
        mLoadContact = ContactHelper.getListContact(getContext())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Contact>>() {
                    @Override
                    public void call(List<Contact> contacts) {
                        commitData(contacts);
                    }
                });
    }
    @OnShowRationale(Manifest.permission.WRITE_CONTACTS)
    public void showRationale(PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_CONTACTS)
    public void onPermissionContactDenied() {

    }

    @OnNeverAskAgain(Manifest.permission.WRITE_CONTACTS)
    public void onNeverAskAgainContact() {

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}

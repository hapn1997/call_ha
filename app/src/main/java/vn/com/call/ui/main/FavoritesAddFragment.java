package vn.com.call.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.piasy.safelyandroid.fragment.SupportFragmentTransactionDelegate;
import com.github.piasy.safelyandroid.fragment.TransactionCommitter;
import com.huyanh.base.custominterface.OnClickContactListener;
import com.huyanh.base.dao.CallOrSms;
import com.huyanh.base.utils.BaseConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import vn.com.call.R;
import vn.com.call.adapter.ContactFavAdapter;
import vn.com.call.adapter.HorizontalContactAdapter;
import vn.com.call.db.ContactHelper;
import vn.com.call.db.cache.ContactCache;
import vn.com.call.model.contact.Contact;
import vn.com.call.ui.BaseFragment;
import vn.com.call.ui.search.searchF;
import vn.com.call.widget.FabBottomRecyclerView;
import vn.com.call.adapter.ContactAdapter;
import vn.com.call.model.sms.Conversation;
import vn.com.call.utils.CallUtils;

/**
 * Created by ngson on 28/06/2017.
 */

@RuntimePermissions
public class FavoritesAddFragment extends BaseFragment {
    private final static String TAG = FavoritesAddFragment.class.getSimpleName();

    public static FavoritesAddFragment newInstance() {

        Bundle args = new Bundle();

        FavoritesAddFragment fragment = new FavoritesAddFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @BindView(R.id.search_content1)
    FrameLayout search_content1;
    @BindView(R.id.cancelfragment)
    TextView cancelfragment;
    @BindView(R.id.contacttext)
    TextView contacttext;
    @BindView(R.id.ưqwqwqw)
    RelativeLayout choosecontact;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.edt_search_thread_messagechinh)
    TextView edt_message;
    @BindView(R.id.relative_change)
    RelativeLayout relativeLayout_change;
    @BindView(R.id.tvTabCancel)
    TextView tvTabCancel;
    @BindView(R.id.relative_search)
    RelativeLayout relative_search;
    @BindView(R.id.relative_tool_bar)
    RelativeLayout relative_tool_bar;
    @BindView(R.id.relative_searchchinh)
    RelativeLayout relativeLayout_search;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.list)
    FabBottomRecyclerView mList;
    @BindView(R.id.edt_search_thread_message)
    EditText mInputSearch;
    @BindView(R.id.search_content)
    FrameLayout mSearchContainer;
    @BindView(R.id.img_search_1)
    ImageView img_search_1;
    @BindView(R.id.img_cancel)
    ImageView mClear;
    @BindView(R.id.tabHome)
    RelativeLayout tabHome;
    @OnClick(R.id.img_cancel)
    void clearSearch() {
        mInputSearch.setText("");
    }
    private searchF msearchF;

    private List<ContactSectionEntity> contactSectionEntities = new ArrayList<>();
    private ContactFavAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    protected Subscription mLoadContact;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initRecyclerView();
        initSearch();
        appBarLayout.getTotalScrollRange();
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

            }
        });
        edt_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                relative_tool_bar.setVisibility(View.GONE);
                relative_search.setVisibility(View.VISIBLE);
                appBarLayout.setExpanded(false,true);
                relativeLayout_change.setVisibility(View.GONE);
                edt_message.setVisibility(View.GONE);
               // mSearchContainer.setVisibility(View.VISIBLE);



            }
        });
        relativeLayout_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative_tool_bar.setVisibility(View.GONE);
                relative_search.setVisibility(View.VISIBLE);
                appBarLayout.setExpanded(false,true);
                relativeLayout_change.setVisibility(View.GONE);
                edt_message.setVisibility(View.GONE);


            }
        });
        tvTabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative_tool_bar.setVisibility(View.VISIBLE);
                relative_search.setVisibility(View.GONE);
                appBarLayout.setExpanded(true,true);
                relativeLayout_change.setVisibility(View.VISIBLE);
                edt_message.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        mInputSearch.getWindowToken(), 0);
                FavoritesAddFragment.this.getFragmentManager().beginTransaction().detach(FavoritesAddFragment.this).attach(FavoritesAddFragment.this).commit();            }
        });
        final FavoriteFragment favoriteFragment = new FavoriteFragment();
        cancelfragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().replace(R.id.search_content1 ,favoriteFragment).addToBackStack("FavoriteFragment").commitAllowingStateLoss();

            }
        });


    }

    private void initRecyclerView() {
        mAdapter = new ContactFavAdapter(contactSectionEntities,this);
        mAdapter = new ContactFavAdapter(contactSectionEntities, this,new OnClickContactListener() {
            @Override
            public void onClick(CallOrSms callOrSms) {
                if (!showPopup(callOrSms, false)) {
                if (callOrSms.isCall()) {
                    CallUtils.makeCall(getActivity(), callOrSms.getPhoneNumber());
                } else {
                    Conversation conversation = new Conversation(getActivity(), new String[]{callOrSms.getPhoneNumber()});
                    //hasua ConversationActivity.launch(getActivity(), conversation, BaseConstant.REQUEST_CODE_SHOW_POPUP);
                }
                }
            }
        });
//        mAdapter = new ContactAdapter(contactSectionEntities, new OnClickViewConversationListener());
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mList.setLayoutManager(mLayoutManager);
        mList.setAdapter(mAdapter);

        //mList.setupWithView(mAdd);
    }
    private void initSearch() {
        mInputSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    img_search_1.setImageResource(R.drawable.ic_search_gray);
                   // mSearchContainer.setVisibility(View.VISIBLE);
                    mClear.setVisibility(View.VISIBLE);
                    mInputSearch.setHint("Search");
                    msearchF = new searchF();
                    mInputSearch.setTextColor(R.color.avatar);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_content, msearchF);

                    SupportFragmentTransactionDelegate supportFragmentTransactionDelegate = new SupportFragmentTransactionDelegate();

                    supportFragmentTransactionDelegate.safeCommit(new TransactionCommitter() {
                        @Override
                        public boolean isCommitterResumed() {
                            return true;
                        }
                    }, ft);
                } else {


                    //img_search_1.setImageResource(R.drawable.ic_search_gray);
                   // mSearchContainer.setVisibility(View.GONE);
                    mInputSearch.setHint(R.string.search);
                    mInputSearch.setText("");
                    mClear.setVisibility(View.GONE);

                }
            }
        });

        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (msearchF != null) msearchF.changeKeyword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    protected void commitData(List<Contact> contacts) {
        contactSectionEntities.clear();

        contactSectionEntities.addAll(convertToSectionEntity(contacts));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadAndShowData();
    }

    protected void loadAndShowData() {
        commitData(ContactCache.getContacts());
        FavoritesAddFragmentPermissionsDispatcher.readContactWithCheck(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private List<ContactSectionEntity> convertToSectionEntity(List<Contact> contacts) {
        List<ContactSectionEntity> contactSectionEntities = new ArrayList<>();

        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact, Contact t1) {
                return contact.getName().compareTo(t1.getName());
            }
        });

        String lastHeader = null;
        for (Contact contact : contacts) {
            String header = contact.getName().substring(0, 1).toUpperCase();
            if (header.charAt(0) < 'A' || header.charAt(0) > 'Z') header = "#";

            if (lastHeader == null || !header.equals(lastHeader)) {
                contactSectionEntities.add(new ContactSectionEntity(true, header));
                contactSectionEntities.add(new ContactSectionEntity(contact));

                lastHeader = header;
            } else {
                contactSectionEntities.add(new ContactSectionEntity(contact));
            }
        }

        return contactSectionEntities;
    }

    public void hideButtonAddContact() {
        search_content1.setVisibility(View.GONE);
       // contacttext.setText("");
        choosecontact.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.GONE);
        tabHome.setVisibility(View.GONE);
        relativeLayout_search.setVisibility(View.GONE);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.content_favorites_add_fragment;
    }

    @Override
    public void onDestroyView() {
        if (mLoadContact != null) mLoadContact.unsubscribe();
        super.onDestroyView();
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
}

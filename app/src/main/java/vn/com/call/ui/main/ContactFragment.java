package vn.com.call.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
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
import com.dialer.ios.iphone.contacts.R;
import vn.com.call.RecyclerSectionItemDecoration;
import vn.com.call.widget.SideBar;
import vn.com.call.adapter.HorizontalContactAdapter;
import vn.com.call.db.ContactHelper;
import vn.com.call.db.cache.ContactCache;
import vn.com.call.model.contact.Contact;
import vn.com.call.ui.BaseFragment;
import vn.com.call.ui.search.SearchFragment;
import vn.com.call.widget.FabBottomRecyclerView;
import vn.com.call.adapter.ContactAdapter;
import vn.com.call.model.sms.Conversation;
import vn.com.call.utils.CallUtils;

/**
 * Created by ngson on 28/06/2017.
 */

@RuntimePermissions
public class ContactFragment extends BaseFragment implements SideBar.OnTouchingLetterChangedListener {
    private final static String TAG = ContactFragment.class.getSimpleName();

    public static ContactFragment newInstance() {

        Bundle args = new Bundle();

        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.sideBar)
    SideBar  msideBar;
    @BindView(R.id.scrollletter)
    FrameLayout scrollletter;
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
    @BindView(R.id.tvTabTitle1)
    TextView tvTabTitle;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.list)
    FabBottomRecyclerView mList;
    @BindView(R.id.new_message1)
    ImageView newMessageTitle;
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

    @BindView(R.id.add)
    ImageView mAdd;

    @OnClick(R.id.add)
    void addContact() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivity(intent);
    }
    private SearchFragment mSearchFragment;
    //header
    private View mRecentLayout;
    private RecyclerView mRecentList;

    private View mFavoriteLayout;
    private RecyclerView mFavoriteList;

    private List<ContactSectionEntity> contactSectionEntities = new ArrayList<>();
    private List<Contact> mRecentContacts = new ArrayList<>();
    private List<Contact> mFavoriteContacts = new ArrayList<>();

    private ContactAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    RecyclerSectionItemDecoration sectionItemDecoration;
    protected Subscription mLoadContact;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initRecyclerView();
         initSearch();
        appBarLayout.getTotalScrollRange();
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if(i== (-appBarLayout.getTotalScrollRange())){
                    tvTabTitle.setVisibility(View.VISIBLE);

                    newMessageTitle.setVisibility(View.VISIBLE);

                    return;
                }
                tvTabTitle.setVisibility(View.GONE);

                newMessageTitle.setVisibility(View.GONE);
            }
        });
        newMessageTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivity(intent);
            }
        });
        msideBar.setOnTouchingLetterChangedListener(this);

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
                Fragment fragment = new ContactFragment();
                relative_tool_bar.setVisibility(View.VISIBLE);
                relative_search.setVisibility(View.GONE);
                appBarLayout.setExpanded(true,true);
                relativeLayout_change.setVisibility(View.VISIBLE);
                edt_message.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        mInputSearch.getWindowToken(), 0);
                  ContactFragment.this.getFragmentManager().beginTransaction().detach(ContactFragment.this).attach(ContactFragment.this).commit();



            }
        });



    }



    private void initRecyclerView() {

        mAdapter = new ContactAdapter(contactSectionEntities);
        mAdapter = new ContactAdapter(contactSectionEntities, new OnClickContactListener() {
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
    //   mAdapter = new ContactAdapter(contactSectionEntities, new OnClickViewConversationListener(getContext()));
        if (hasHeader()) mAdapter.addHeaderView(getHeader());
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mList.setLayoutManager(mLayoutManager);
        mList.setAdapter(mAdapter);
         sectionItemDecoration =
                new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._20sdp),
                        true,
                        getSectionCallback(getData()));
        mList.addItemDecoration(sectionItemDecoration);

    }
    public ArrayList<String> getData() {
       List<Contact> contacts = ContactCache.getContacts();
        ArrayList<String> myDataset = new ArrayList<String>();
        for(int i=0; i<contacts.size(); i++) {
            myDataset.add(contacts.get(i).getName());
       }
        return myDataset;
    }
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<String> people) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                if(position<people.size()){
                    return position == 0
                            || people.get(position)
                            .charAt(0) != people.get(position-1)
                            .charAt(0);
                }else {
                    return position == 0;
                }

            }

            @Override
            public CharSequence getSectionHeader(int position) {
                //Log.d("rfvfvfv",people.get(3).substring(0,1).toUpperCase() );
                  if(position<people.size()){
                      return people.get(position)
                              .subSequence(0,
                                      1);
                  }else {
                      return "#";
                  }


            }
        };
    }
    private void initSearch() {
        mInputSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    msideBar.setVisibility(View.GONE);
                    img_search_1.setImageResource(R.drawable.ic_search_gray);
                 //   mSearchContainer.setVisibility(View.VISIBLE);
                    mClear.setVisibility(View.VISIBLE);
                    //ivUpgrade.setVisibility(View.GONE);
                    mInputSearch.setHint(R.string.hint_search_main);
                    mSearchFragment = new SearchFragment();
                    mInputSearch.setTextColor(R.color.avatar);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_content, mSearchFragment);
                    SupportFragmentTransactionDelegate supportFragmentTransactionDelegate = new SupportFragmentTransactionDelegate();

                    supportFragmentTransactionDelegate.safeCommit(new TransactionCommitter() {
                        @Override
                        public boolean isCommitterResumed() {
                            return true;
                        }
                    }, ft);
                } else {
                    img_search_1.setImageResource(R.drawable.ic_search_gray);
                  //  mSearchContainer.setVisibility(View.GONE);
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
                if (mSearchFragment != null){
                    mSearchFragment.changeKeyword(s.toString());

                }


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClose(Object object) {
        super.onClose(object);
        if (object == null) return;

        if (object instanceof CallOrSms) {
            CallOrSms callOrSms = (CallOrSms) object;
            if (callOrSms.isCall()) {
                CallUtils.makeCall(getActivity(), callOrSms.getPhoneNumber());
            } else {
                Conversation conversation = new Conversation(getActivity(), new String[]{callOrSms.getPhoneNumber()});
               // ConversationActivity.launch(getActivity(), conversation);
            }
        }
    }

    private View getHeader() {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.header_list_contact, null);

        mRecentLayout = header.findViewById(R.id.recents);
        mRecentList = header.findViewById(R.id.list_recents);
        mRecentList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecentList.setAdapter(new HorizontalContactAdapter(this,mRecentContacts));


        mFavoriteLayout = header.findViewById(R.id.favorites);
        mFavoriteList = header.findViewById(R.id.list_favorites);
        mFavoriteList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mFavoriteList.setAdapter(new HorizontalContactAdapter(this,mFavoriteContacts));

        refreshHeader();

        return header;
    }

    private void refreshHeader() {
        mRecentList.getAdapter().notifyDataSetChanged();
       //hasua mRecentLayout.setVisibility(mRecentContacts.size() == 0 ? View.GONE : View.VISIBLE);

        mFavoriteList.getAdapter().notifyDataSetChanged();
        //hasua mFavoriteLayout.setVisibility(mFavoriteContacts.size() == 0 ? View.GONE : View.VISIBLE);
    }

    protected boolean hasHeader() {
        return true;
    }

    protected void commitData(List<Contact> contacts) {
        contactSectionEntities.clear();

        mAdapter.notifyDataSetChanged();
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact, Contact t1) {
                return contact.getName().compareTo(t1.getName());
            }
        });
        for (Contact contact : contacts) {
            contactSectionEntities.add(new ContactSectionEntity(contact));
        }
        //contactSectionEntities.addAll(contactSectionEntities);





    }

    @Override
    public void onResume() {
        super.onResume();

        loadAndShowData();

    }

    protected void loadAndShowData() {
        commitData(ContactCache.getContacts());

        ContactFragmentPermissionsDispatcher.readContactWithCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ContactFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
           // String header = contact.getName().substring(0, 1).toUpperCase();
//            if (header.charAt(0) < 'A' || header.charAt(0) > 'Z') header = "#";
//
//            if (lastHeader == null || !header.equals(lastHeader)) {
//                //contactSectionEntities.add(new ContactSectionEntity(true, header));
//                contactSectionEntities.add(new ContactSectionEntity(contact));
//
//                lastHeader = header;
//                Log.d("fdfdfdfd",contact.getName());
//            } else {
                contactSectionEntities.add(new ContactSectionEntity(contact));
           // }
        }

        return contactSectionEntities;
    }

    public void hideButtonAddContact() {
        appBarLayout.setVisibility(View.GONE);
        msideBar.setVisibility(View.GONE);
        mList.removeItemDecoration(sectionItemDecoration);

       //hasua mAdd.setVisibility(View.GONE);
        tabHome.setVisibility(View.GONE);
        relativeLayout_search.setVisibility(View.GONE);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
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

    @Override
    public void onTouchingLetterChanged(String s) {
        List<Contact> contacts;
        contacts = ContactCache.getContacts();
        int position =mAdapter.getPositionForSection(s.charAt(0),contacts);
        if (position != -1) {
               appBarLayout.setExpanded(false);

            mList.smoothScrollToPosition(position);
        }
    }
}

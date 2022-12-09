package vn.com.call.ui;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.phone.thephone.call.dialer.R;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import vn.com.call.db.cache.CallLogHelper;
import vn.com.call.editCall.CallerHelper;
import vn.com.call.ui.main.FavoritesAddFragment;
import vn.com.call.widget.AvatarView;
import vn.com.call.db.ContactHelper;
import vn.com.call.model.calllog.CallLogDetail;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.EmailContact;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.model.sms.Conversation;
import vn.com.call.utils.CallUtils;
import vn.com.call.utils.TimeUtils;

/**
 * Created by ngson on 07/07/2017.
 */

public class ContactDetailActivity extends BaseActivity {
    public static final String EXTRA_CALL_LOG = "call_log";

    public static final String EXTRA_CONTACT = "contact";
    private static final String TAG = ContactDetailActivity.class.getSimpleName();
    @OnClick(R.id.edit3)
    void edit3(){
        if (mContact.getId() != null) {
            Uri uriContact = ContactsContract.Contacts.getLookupUri(Long.parseLong(mContact.getId()), mContact.getLookupKey());

            Intent editIntent = new Intent(Intent.ACTION_EDIT);
            editIntent.setDataAndType(uriContact, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            startActivity(editIntent);
        } else {
            Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT);
            intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            intentInsertEdit.putExtra(ContactsContract.Intents.Insert.PHONE, mContact.getNumbers().get(0).getNumber())
                    .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

            startActivity(intentInsertEdit);
        }
    }
    @BindView(R.id.checklogfav)
    LinearLayout linearLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tools)
    View mToolsView;
    @BindView(R.id.avatar_layout)
    View mCoverLayout;
    @BindView(R.id.scroll)
    NestedScrollView mScrollView;
    @BindView(R.id.avatar)
    AvatarView mAvatar;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.edit)
    LinearLayout mEdit;
    @BindView(R.id.saveedit)
    TextView saveedit;
    @BindView(R.id.phone_email)
    LinearLayout mLayoutPhoneAndEmail;
    @BindView(R.id.another_info)
    LinearLayout mLayoutAnotherInfo;
    @BindView(R.id.recents)
    LinearLayout mLayoutCallLog;
    @BindView(R.id.add_favorite)
    TextView mAddToFavorite;
    @BindView(R.id.delete_contact)
     LinearLayout deleteContact;
    @BindView(R.id.share_contact)
     LinearLayout share_contact;
    @BindView(R.id.email)
    ImageView email;
    @BindView(R.id.desc_mail)
    TextView desc_mail;
    @BindView(R.id.ll_email)
    LinearLayout ll_email;
    @BindView(R.id.et_note)
    EditText et_note;
    @BindView(R.id.new_contact)
     LinearLayout new_contact;
    @OnClick(R.id.new_contact)
    void addContact(){
        Intent intent12 = new Intent(Intent.ACTION_INSERT);
        intent12.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent12.putExtra(ContactsContract.Intents.Insert.PHONE,mContact.getNumbers().get(0).getNumber())
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

        startActivity(intent12);
    }
    @OnClick(R.id.call)
    void call() {
        if (mContact.getNumbers().size() > 1) {
            ChooseNumberDialogFragment dialog = ChooseNumberDialogFragment.newInstance(getString(R.string.call) + " - " + mContact.getName(), mContact.getNumbers());
            dialog.setOnChooseNumberListener(new ChooseNumberDialogFragment.OnChooseNumberListener() {
                @Override
                public void onChoosePhoneNumber(String number) {
//                    CallUtils.makeCall(ContactDetailActivity.this, number);
                    CallerHelper.startPhoneAccountChooseActivity(ContactDetailActivity.this, number);
                }
            });

            dialog.show(getSupportFragmentManager(), "choose_number_call");
        } else {
            if (mContact.getNumbers().size() > 0)
//                CallUtils.makeCall(ContactDetailActivity.this, mContact.getNumbers().get(0).getNumber());
            CallerHelper.startPhoneAccountChooseActivity(ContactDetailActivity.this, mContact.getNumbers().get(0).getNumber());
        }
    }

    @OnClick({R.id.sms, R.id.send_sms})
//    void sendSMS() {
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(mContact.getNumbers().get(0).getNumber(), null, null, null, null);
//            Toast.makeText(getApplicationContext(), "Message Sent",
//                    Toast.LENGTH_LONG).show();
//        } catch (Exception ex) {
//            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
//                    Toast.LENGTH_LONG).show();
//            ex.printStackTrace();
//        }
//    }
    void sms() {
//        if (mContact.getNumbers().size() > 1) {
//            ChooseNumberDialogFragment dialog = ChooseNumberDialogFragment.newInstance(getString(R.string.send_sms) + " - " + mContact.getName(), mContact.getNumbers());
//            dialog.setOnChooseNumberListener(new ChooseNumberDialogFragment.OnChooseNumberListener() {
//                @Override
//                public void onChoosePhoneNumber(String number) {
//                    sendSms(number);
//                }
//            });
//
//            dialog.show(getSupportFragmentManager(), "choose_number_send_sms");
//        } else {
//            if (mContact.getNumbers().size() > 0) sendSms(mContact.getNumbers().get(0).getNumber());
//        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sms:");
        stringBuilder.append(mContact.getNumbers().get(0).getNumber());
        this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
    }

    @OnClick(R.id.share_contact)
    void shareContact() {
        String content = mContact.getName() + " :" + mContact.getNumbersAsString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);

        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_contact)));
    }

    @OnClick(R.id.add_favorite)
    void changeFavorite() {
        if(mContact.isFavorite())
        {
            mContact.setFavorite(false);
            ContactHelper.removeFavorite(this, mContact.getId());
            mAddToFavorite.setText(mContact.isFavorite() ? R.string.detail_activity_title_menu_remove_favorite : R.string.detail_activity_title_menu_add_favorite);
            String toast = getString(R.string.detail_activity_title_toast_remove_favorite).replace("{name}", getNameContact());
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

        }else {
            mContact.setFavorite(true);
            showSetting(this,mContact,mContact.getNumbers().get(0).getNumber(),mContact.getPhoto());

        }

    }
    public void showSetting(final Context context, final Contact contact, final String phoneNumber, final String photo){
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.custom_diglog_favourite, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(context).create();


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
                mAddToFavorite.setText(mContact.isFavorite() ? R.string.detail_activity_title_menu_remove_favorite : R.string.detail_activity_title_menu_add_favorite);
                ContactHelper.setFavorite(context, contact.getId());
                String toast = getString(R.string.detail_activity_title_toast_add_favorite).replace("{name}", getNameContact());
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                deleteDialog.dismiss();

            }
        });
        deleteDialogView.findViewById(R.id.txt_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(controller.checkcallorsms(contact,"Call") ==null){
//                    boolean check =  controller.insertDevice(contact,"Call",phoneNumber,photo);
//                }else;
                mAddToFavorite.setText(mContact.isFavorite() ? R.string.detail_activity_title_menu_remove_favorite : R.string.detail_activity_title_menu_add_favorite);
                ContactHelper.setFavorite(context, contact.getId());
                String toast = getString(R.string.detail_activity_title_toast_add_favorite).replace("{name}", getNameContact());
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                deleteDialog.dismiss();
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

    @OnClick(R.id.edit)
    void editOrCreateContact() {
//        Intent videocall= new Intent("com.android.phone.videocall");
//        videocall.putExtra("videocall", true);
//        videocall.setData(Uri.parse("tel:" + mContact.getNumbers().get(0).getNumber()));
//        startActivity(videocall);
//        if (mContact.getId() != null) {
//            Uri uriContact = ContactsContract.Contacts.getLookupUri(Long.parseLong(mContact.getId()), mContact.getLookupKey());
//
//            Intent editIntent = new Intent(Intent.ACTION_EDIT);
//            editIntent.setDataAndType(uriContact, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
//            startActivity(editIntent);
//        } else {
//            Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT);
//            intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
//            intentInsertEdit.putExtra(ContactsContract.Intents.Insert.PHONE, mContact.getNumbers().get(0).getNumber())
//                    .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
//
//            startActivity(intentInsertEdit);
//        }
    }

    @OnClick(R.id.block)
    void block() {

    }

    private int mHeightToolbar;
    private int mHeightCoverLayout;

    private Contact mContact;

    private Subscription mSubscriptionLoadInfoContact;
    private Subscription mSubscriptionLoadCallLog;

    public static void launch(Activity activity, View avatar, Contact contact) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, new Pair<>(avatar, "avatar"));
        Intent intent = new Intent(activity, ContactDetailActivity.class);
        intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, contact);
        intent.putExtra(ContactDetailActivity.EXTRA_CALL_LOG, "ddvdvdvdv");


        if (Build.VERSION.SDK_INT >= 23)


            ActivityCompat.startActivity(activity, intent, options.toBundle());
        else {
            activity.startActivity(intent);
        }

    }

    public static void launch(Activity activity, View avatar, Contact contact, int requestCode) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, new Pair<>(avatar, "avatar"));

        Intent intent = new Intent(activity, ContactDetailActivity.class);
        intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, contact);

        if (Build.VERSION.SDK_INT >= 23) {
           // ActivityCompat.startActivity(activity, intent, options.toBundle());
            intent.putExtras(options.toBundle());
            activity.startActivityForResult(intent, requestCode);
        } else {
           // activity.startActivity(intent);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static void launch(Context context, Contact contact) {
        Intent intent = new Intent(context.getApplicationContext(), ContactDetailActivity.class);
        intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, contact);
        intent.putExtra(ContactDetailActivity.EXTRA_CALL_LOG, "ddvdvdvdv");

        if (context instanceof Service) {
            intent.putExtra(BaseActivity.EXTRA_FROM_SERVICE, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    public static void launch(Activity activity, Contact contact, int requestCode) {
        Intent intent = new Intent(activity, ContactDetailActivity.class);
        intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, contact);
        activity.startActivityForResult(intent, requestCode);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContact = getIntent().getParcelableExtra(EXTRA_CONTACT);
        ViewCompat.setTransitionName(mAvatar, "avatar");

        initToolbar();
        initCoverLayout();
        initScrollView();

        showCardViewPhoneAndEmail();
        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContact.delete(ContactDetailActivity.this,true,true);
            }
        });
        mAddToFavorite.setVisibility(mContact.getId() == null ? View.GONE : View.VISIBLE);
        linearLayout.setVisibility(mContact.getId() == null ? View.GONE : View.VISIBLE);
        deleteContact.setVisibility(mContact.getId() == null ? View.GONE : View.VISIBLE);
        share_contact.setVisibility(mContact.getId() == null ? View.GONE : View.VISIBLE);
        new_contact.setVisibility(mContact.getId() == null ? View.VISIBLE : View.GONE);


    }

    @Override
    protected void onResume() {
        super.onResume();

        loadInfoContact();

        loadCallLog();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_detail;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mContact != null) {
            if (mAddToFavorite != null)
                mAddToFavorite.setText(mContact.isFavorite() ? R.string.detail_activity_title_menu_remove_favorite : R.string.detail_activity_title_menu_add_favorite);

            //menu.findItem(R.id.favorite).setTitle(mContact.isFavorite() ? R.string.detail_activity_title_menu_remove_favorite : R.string.detail_activity_title_menu_add_favorite);
//            menu.findItem(R.id.remove_contact).setVisible(mContact.getId() != null);
//            menu.findItem(R.id.favorite).setVisible(mContact.getId() != null);
//            menu.findItem(R.id.copy_contact).setVisible(mContact.getId() != null);
//            menu.findItem(R.id.copy_name).setVisible(mContact.getId() != null);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.activity_detail_contact, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) onBackPressed();
        else if (id == R.id.favorite) {
            changeFavorite();
            invalidateOptionsMenu();
        } else if (id == R.id.copy_name) {
            mContact.copyName(this);
        } else if (id == R.id.copy_number) {
            mContact.copyNumber(this);
        } else if (id == R.id.copy_contact) {
            mContact.copyContact(this);
        } else if (id == R.id.remove_contact) {
            mContact.delete(this, true, true);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initCoverLayout() {
        mCoverLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onGlobalLayout() {
                mHeightCoverLayout = mCoverLayout.getHeight();

                initToolView();

                mCoverLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        List<PhoneNumber> phoneNumbers = mContact.getNumbers();
        String numberCacheColor = phoneNumbers != null && phoneNumbers.size() > 0 ? phoneNumbers.get(0).getNumber() : "";

       mAvatar.loadAvatar(mContact.getPhoto(), mContact.getName(), numberCacheColor);

        mName.setText(getNameContact());
    }

    private void initToolbar() {
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mToolbar.setTitle(getResources().getString(R.string.contacts));
        mToolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeightToolbar = mToolbar.getHeight();

                mToolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        mToolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        setSupportActionBar(mToolbar);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initToolView() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mToolsView.getLayoutParams();
        params.topMargin = mHeightCoverLayout;
        params.gravity = Gravity.TOP;
        mToolsView.setLayoutParams(params);

//        initButtonsInToolView();
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void initButtonsInToolView() {
//        if (mContact.getId() == null) {
//            mEdit.setImageDrawable(getDrawable(R.drawable.ic_person_add_primary_color_24dp));
//            saveedit.setText("save");
//           // mEdit.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_person_add_primary_color_24dp), null, null);
//           // mEdit.setText(R.string.save);
//        } else {
//           // mEdit.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_edit_primary_color_24dp), null, null);
//            //mEdit.setText(R.string.edit);
//            mEdit.setImageDrawable(getDrawable(R.drawable.ic_edit_primary_color_24dp));
//            saveedit.setText("edit");
//        }
//    }

    private void initScrollView() {
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int maxScrollToolView = mHeightCoverLayout - mHeightToolbar;

                int scroll;

                if (scrollY < maxScrollToolView) scroll = scrollY;
                else scroll = maxScrollToolView;

                mToolsView.setTranslationY(-scroll);

                if (scroll < maxScrollToolView * 0.66f) {
                    mToolbar.setBackgroundColor(Color.TRANSPARENT);
                    mToolbar.setTitle("");
                } else {
                    mToolbar.setBackgroundColor(Color.WHITE);
                    mToolbar.setTitle(mContact.getName());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showCardViewPhoneAndEmail() {
        mLayoutPhoneAndEmail.removeAllViews();

        if ((mContact.getNumbers() != null && mContact.getNumbers().size() > 0)
                || (mContact.getEmailContacts() != null && mContact.getEmailContacts().size() > 0) || mContact.getNote() !=null && mContact.getNote().length() > 0) {
            showPhoneNumbers();
            showEmails();
            showAddress();
            showNote();

        } else {
            showContactEmpty();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showPhoneNumbers() {
        List<PhoneNumber> phoneNumbers = mContact.getNumbers();

        if (phoneNumbers != null) {
            int size = phoneNumbers.size();
            for (int i = 0; i < size; i++) {
                final PhoneNumber phoneNumber = phoneNumbers.get(i);

                View layoutPhoneNumber = LayoutInflater.from(this).inflate(R.layout.sub_item_phone_number, mLayoutPhoneAndEmail, false);

                ImageView call = layoutPhoneNumber.findViewById(R.id.call);
                ImageButton sms = layoutPhoneNumber.findViewById(R.id.sms);
                TextView number = layoutPhoneNumber.findViewById(R.id.number);
                TextView typeNumber = layoutPhoneNumber.findViewById(R.id.type_number);

                number.setText(phoneNumber.getNumber());
                typeNumber.setText(getResources().getString(R.string.mobile1));
                int color = ContextCompat.getColor(this,R.color.colorBlue);
               number.setTextColor(color);
               // call.setVisibility(i == 0 ? View.VISIBLE : View.INVISIBLE);

                layoutPhoneNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        CallUtils.makeCall(ContactDetailActivity.this, phoneNumber.getNumber());
                        CallerHelper.startPhoneAccountChooseActivity(ContactDetailActivity.this, phoneNumber.getNumber());
                    }
                });

                sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendSms(phoneNumber.getNumber());
                    }
                });

                mLayoutPhoneAndEmail.addView(layoutPhoneNumber);
            }
        }
    }

    private void showEmails() {
        List<EmailContact> emails = mContact.getEmailContacts();

        if (emails != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && emails.size() > 0) {
                ImageViewCompat.setImageTintList(email, ColorStateList.valueOf(getApplicationContext().getColor(R.color.blue_ios)));

//                email.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getColor(R.color.blue_ios)));

                desc_mail.setTextColor(getApplicationContext().getColor(R.color.blue_ios));
                ll_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", emails.get(0).getEmail(), null));

                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
            }else {
                ll_email.setOnClickListener(null);
            }
            if (mContact.getNumbers() != null && mContact.getNumbers().size() > 0)
                mLayoutPhoneAndEmail.addView(getLineView(mLayoutPhoneAndEmail));

            int size = emails.size();
            for (int i = 0; i < size; i++) {
                final EmailContact email = emails.get(i);

                View layoutEmail = LayoutInflater.from(this).inflate(R.layout.sub_item_mail, mLayoutPhoneAndEmail, false);

                ImageButton emailButton = layoutEmail.findViewById(R.id.email_icon);
                TextView emailText = layoutEmail.findViewById(R.id.email_text);
                TextView emailType = layoutEmail.findViewById(R.id.type_email);

                emailText.setText(email.getEmail());
                emailType.setText(email.getType());
//                emailButton.setVisibility(i == 0 ? View.VISIBLE : View.INVISIBLE);

                layoutEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", email.getEmail(), null));

                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });

                mLayoutPhoneAndEmail.addView(layoutEmail);
            }
        }
    }

    private void showNote() {
        String note = mContact.getNote();
        if (note != null && note.length() > 0) {
            et_note.setText(note);

        }
//        content://com.android.contacts/display_photo/22
        if (mContact.getPhoto() !=null && !mContact.getPhoto().contains("/contacts/") ){
            mAvatar.loadAvatar(mContact.getPhoto(),ContactHelper.getContactName(getApplicationContext(),mContact.getNumbers().get(0).getNumber()),mContact.getNumbers().get(0).getNumber());
        }

    }


    private void showAddress() {
        String address = mContact.getAddress();

        if (address != null && address.length() > 0) {
            if (mContact.getEmailContacts().size() > 0 || mContact.getNumbers().size() > 0)
                mLayoutPhoneAndEmail.addView(getLineView(mLayoutPhoneAndEmail));

            View layoutAddress = LayoutInflater.from(this).inflate(R.layout.sub_item_place, mLayoutPhoneAndEmail, false);

            ImageView map = layoutAddress.findViewById(R.id.map);
            TextView place = layoutAddress.findViewById(R.id.place);

            place.setText(address);
            Glide.with(this)
                    .load("http://maps.google.com/maps/api/staticmap?center=" + address + "&zoom=15&size=1000x200&sensor=false&visible=true")
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .centerCrop()
                    .into(map);

            layoutAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            mLayoutPhoneAndEmail.addView(layoutAddress);
        }
    }

    private void showContactEmpty() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.sub_item_empty, mLayoutPhoneAndEmail, false);

        emptyView.findViewById(R.id.add_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrCreateContact();
            }
        });

        emptyView.findViewById(R.id.add_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrCreateContact();
            }
        });

        mLayoutPhoneAndEmail.addView(emptyView);
    }

    private void showCallLog(final List<CallLogDetail> callLogDetails) {
        mLayoutCallLog.removeAllViews();
        Intent intent1 =getIntent();
        String calllof= intent1.getStringExtra(ContactDetailActivity.EXTRA_CALL_LOG);
        if(calllof != null) {


            if (callLogDetails.size() > 0) {
                final int MAX_CALLLOG_SHOW = 3;

                mLayoutCallLog.setVisibility(View.VISIBLE);

                View header = LayoutInflater.from(this).inflate(R.layout.head_recents_layout_contact_detail, mLayoutCallLog, false);
                header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ContactDetailActivity.this, ListCallLogByContactActivity.class);
                        intent.putParcelableArrayListExtra(ListCallLogByContactActivity.EXTRA_LIST_CALL_LOG_DETAIL, (ArrayList<CallLogDetail>) callLogDetails);
                        startActivity(intent);
                    }
                });
                // mLayoutCallLog.addView(header);

                int max = callLogDetails.size() > MAX_CALLLOG_SHOW ? MAX_CALLLOG_SHOW : callLogDetails.size();

                for (int i = 0; i < max; i++) {
                    CallLogDetail callLogDetail = callLogDetails.get(i);

                    View callLogView = LayoutInflater.from(this).inflate(R.layout.item_call_log_detail_contact_detail, mLayoutCallLog, false);

                    TextView number = callLogView.findViewById(R.id.number);
                    ImageView typeCallLog = callLogView.findViewById(R.id.type_call_log);
                    TextView date = callLogView.findViewById(R.id.date);

                    //number.setText(callLogDetail.getNumber());
                    if (callLogDetail.getType() == CallLog.Calls.INCOMING_TYPE) {
                        typeCallLog.setImageResource(R.drawable.ic_call_received_primary_color_18dp);
                        number.setText(R.string.incoming);

                    } else if (callLogDetail.getType() == CallLog.Calls.OUTGOING_TYPE) {
                        typeCallLog.setImageResource(R.drawable.ic_call_made_primary_color_18dp);
                        number.setText(R.string.outgoing);
                    } else {
                        typeCallLog.setImageResource(R.drawable.ic_call_received_red_700_18dp);
                        number.setText(R.string.missed);
                    }
                    typeCallLog.setVisibility(View.GONE);
                    date.setText(TimeUtils.getTimeFormatCallLogDetail(callLogDetail.getDate()));

                    mLayoutCallLog.addView(callLogView);
                }
            } else {
                mLayoutCallLog.setVisibility(View.GONE);
            }
        } else {
            mLayoutCallLog.setVisibility(View.GONE);


        }

        }

    private View getLineView(ViewGroup parent) {
        return LayoutInflater.from(this).inflate(R.layout.line_horizontal_detail_contact, parent, false);
    }

    private void showCardViewAnotherInfo() {

    }

    private void sendSms(String number) {
        Conversation conversation = new Conversation(ContactDetailActivity.this, new String[]{number});
       //hasus ConversationActivity.launch(ContactDetailActivity.this, conversation);
    }

    private String getNameContact() {
        return mContact.getName() != null ? mContact.getName() : mContact.getNumbers().get(0).getNumber();
    }

    private void loadInfoContact() {
        mSubscriptionLoadInfoContact = ContactHelper.getInfoContact(this, mContact)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Contact>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void call(Contact contact) {
                        showCardViewPhoneAndEmail();
                    }
                });
    }

    private void loadCallLog() {
        Observable<List<CallLogDetail>> observable = mContact.getId() != null
                ? CallLogHelper.getCallLogDetailByContact(this, mContact)
                : CallLogHelper.getCallLogDetailByNumber(this, mContact.getNumbers().get(0).getNumber());

        mSubscriptionLoadCallLog = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CallLogDetail>>() {
                    @Override
                    public void call(List<CallLogDetail> details) {
                        Log.wtf("loadCallLog", details.size() + "");
                        showCallLog(details);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mSubscriptionLoadInfoContact != null) mSubscriptionLoadInfoContact.unsubscribe();
        if (mSubscriptionLoadCallLog != null) mSubscriptionLoadCallLog.unsubscribe();

        super.onDestroy();
    }
}


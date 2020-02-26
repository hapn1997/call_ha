package vn.com.call.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.huyanh.base.custominterface.OnClickContactListener;
import com.huyanh.base.dao.CallOrSms;
import com.huyanh.base.utils.BaseConstant;

import java.util.ArrayList;
import java.util.List;

import com.dialer.ios.iphone.contacts.R;
import vn.com.call.adapter.listener.OnClickViewConversationListener;
import vn.com.call.adapter.listener.OnClickViewFavoritesListener;
import vn.com.call.adapter.viewholder.ContactViewHolder;
import vn.com.call.call.adapter.CallFlashSelectAdapter;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.model.sms.Conversation;
import vn.com.call.ui.ChooseNumberPopupWindow;
import vn.com.call.ui.ContactDetailActivity;
import vn.com.call.ui.main.ContactSectionEntity;
import vn.com.call.utils.CallUtils;


/**
 * Created by ngson on 07/07/2017.
 */

public class ContactAdapter extends BaseSectionQuickAdapter<ContactSectionEntity, ContactViewHolder>   {
    private List<ContactSectionEntity> data;



    private String idContactExpand = null;
     private boolean checkb = true;
     OnClickViewFavoritesListener onClickViewFavoritesListener;
    private OnClickViewConversationListener onClickViewConversation;
    private OnClickContactListener onClickContactListener;

    public ContactAdapter(List<ContactSectionEntity> data) {
        super(R.layout.item_contact, R.layout.item_header_contact, data);

        this.data = data;

    }

    public ContactAdapter(List<ContactSectionEntity> data, OnClickContactListener onClickContactListener) {
        super(R.layout.item_contact, R.layout.item_header_contact, data);
        this.data = data;
        this.onClickContactListener = onClickContactListener;
    }




    @Override
    protected void convertHead(ContactViewHolder helper, ContactSectionEntity item) {
        helper.setText(R.id.header, item.header);
    }

    @Override
    protected void convert(final ContactViewHolder helper, final ContactSectionEntity item) {
        final Contact contact = item.t;


        final List<PhoneNumber> phoneNumbers = contact.getNumbers();
        String numberCacheColor = phoneNumbers.size() > 0 ? phoneNumbers.get(0).getNumber() : "";
        helper.avatar.loadAvatar(contact.getPhoto(), contact.getName(), numberCacheColor);
        helper.name.setText(contact.getName());

        String address = contact.getAddress();
        if (address != null && address.length() > 0) {
            helper.address.setText(address);
            helper.address.setVisibility(View.VISIBLE);
        } else helper.address.setVisibility(View.GONE);

        helper.expand.setVisibility(isExpand(contact.getId()) ? View.VISIBLE : View.GONE);

        helper.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof Activity) {
                    goDetailContact((Activity) mContext, helper.avatar, contact, BaseConstant.REQUEST_CODE_SHOW_POPUP);
                } else
                    goDetailContact(mContext, helper.avatar, contact);
//                    if (contact.hasPhoneNumber()) {
//                        idContactExpand = contact.getId();
//                        notifyDataSetChanged();
//                    } else {
//                        idContactExpand = null;
//                        goDetailContact(mContext, helper.avatar, contact);
//                    }
                }



        });

        helper.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumbers.size() > 1) {
                    ChooseNumberPopupWindow chooseNumberPopupWindow = new ChooseNumberPopupWindow(mContext, contact, ChooseNumberPopupWindow.Action.CALL);
                    chooseNumberPopupWindow.showPopup(helper.call, onClickViewConversation);
                } else {
                    if (onClickContactListener != null) {
                        onClickContactListener.onClick(new CallOrSms(phoneNumbers.get(0).getNumber(), true));
                        Log.d("dsđs", phoneNumbers.get(0).getNumber());
                    }
                    else {
                        Log.d("dsđs", phoneNumbers.get(0).getNumber());
                        CallUtils.makeCall(mContext, phoneNumbers.get(0).getNumber());
                    };
                }
            }
        });

        helper.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumbers.size() > 1) {
                    ChooseNumberPopupWindow chooseNumberPopupWindow = new ChooseNumberPopupWindow(mContext, contact, ChooseNumberPopupWindow.Action.SEND_SMS);
                    chooseNumberPopupWindow.showPopup(helper.sendMessage, onClickViewConversation);
                } else {
                    if (onClickContactListener != null)
                        onClickContactListener.onClick(new CallOrSms(phoneNumbers.get(0).getNumber(), false));
                    else {
                        Conversation conversation = new Conversation(mContext, new String[]{phoneNumbers.get(0).getNumber()});
                        if ((mContext instanceof Activity) || onClickViewConversation == null) {
                           //hasua ConversationActivity.launch(mContext, conversation);
                        } else onClickViewConversation.onClickConversation(conversation);
                    }
                }
            }
        });

        helper.goDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof Activity) {
                    goDetailContact((Activity) mContext, helper.avatar, contact, BaseConstant.REQUEST_CODE_SHOW_POPUP);
                } else
                    goDetailContact(mContext, helper.avatar, contact);
            }
        });

        helper.deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(mContext);
                }
                builder.setTitle(R.string.delete_contact)
                        .setMessage(R.string.alert_delete_contact)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                contact.delete(mContext, false, false);
                                data.remove(item);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        });

                Dialog dialog = builder.create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
            }
        });
    }

    private boolean isExpand(String contactId) {
        return idContactExpand != null && idContactExpand.equals(contactId);
    }

    private void goDetailContact(Context context, View avatar, Contact contact) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            ContactDetailActivity.launch(activity, avatar, contact);
        } else {
            ContactDetailActivity.launch(context, contact);
        }
    }

    private void goDetailContact(Activity activity, View avatar, Contact contact, int requestCode) {
        if (activity instanceof Activity) {
           ContactDetailActivity.launch(activity, avatar, contact, requestCode);

        } else {
            ContactDetailActivity.launch(activity, contact, requestCode);
        }
    }
    public int getPositionForSection(int section,List<Contact> contact) {
        for (int i = 0; i < contact.size() ; i++) {

            String sortStr = contact.get(i).getName();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }


}

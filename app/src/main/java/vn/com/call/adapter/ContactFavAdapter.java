package vn.com.call.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.huyanh.base.custominterface.OnClickContactListener;
import com.huyanh.base.dao.CallOrSms;
import com.huyanh.base.utils.BaseConstant;

import java.io.IOException;
import java.util.List;

import vn.com.call.R;
import vn.com.call.adapter.listener.OnClickViewConversationListener;
import vn.com.call.adapter.listener.OnClickViewFavoritesListener;
import vn.com.call.adapter.viewholder.ContactViewHolder;
import vn.com.call.db.ContactHelper;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.model.sms.Conversation;
import vn.com.call.ui.ChooseNumberPopupWindow;
import vn.com.call.ui.main.ContactSectionEntity;
import vn.com.call.ui.main.MainActivity;
import vn.com.call.utils.CallUtils;


/**
 * Created by ngson on 07/07/2017.
 */

public class ContactFavAdapter extends BaseSectionQuickAdapter<ContactSectionEntity, ContactViewHolder> {
    private List<ContactSectionEntity> data;

    private String idContactExpand = null;
    private boolean checkb = true;
    OnClickViewFavoritesListener onClickViewFavoritesListener;
    private OnClickViewConversationListener onClickViewConversation;
    private OnClickContactListener onClickContactListener;
    Fragment fragment;
    public ContactFavAdapter(List<ContactSectionEntity> data, Fragment fragment) {
        super(R.layout.item_contact, R.layout.item_header_contact, data);
         this.fragment = fragment;
        this.data = data;

    }

    public ContactFavAdapter(List<ContactSectionEntity> data, Fragment fragment,OnClickContactListener onClickContactListener) {
        super(R.layout.item_contact, R.layout.item_header_contact, data);
        this.data = data;
        this.onClickContactListener = onClickContactListener;
        this.fragment = fragment;
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
         // contact.changeFavorite(mContext);
             //   Log.d("dcdccdccd",contact.getPhoto()+ contact.getName());
              showSetting(mContext,contact,phoneNumbers.get(0).getNumber(),contact.getPhoto());
            }



        });

        helper.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumbers.size() > 1) {
                    ChooseNumberPopupWindow chooseNumberPopupWindow = new ChooseNumberPopupWindow(mContext, contact, ChooseNumberPopupWindow.Action.CALL);
                    chooseNumberPopupWindow.showPopup(helper.call, onClickViewConversation);
                } else {
                    if (onClickContactListener != null)
                        onClickContactListener.onClick(new CallOrSms(phoneNumbers.get(0).getNumber(), true));
                    else CallUtils.makeCall(mContext, phoneNumbers.get(0).getNumber());
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
            //hasua   ContactDetailActivity.launch(activity, avatar, contact);
        } else {
            //hasua  ContactDetailActivity.launch(context, contact);
        }
    }

    private void goDetailContact(Activity activity, View avatar, Contact contact, int requestCode) {
        if (activity instanceof Activity) {
            //hasua ContactDetailActivity.launch(activity, avatar, contact, requestCode);
        } else {
            //hasua  ContactDetailActivity.launch(activity, contact, requestCode);
        }
    }

    public void showSetting(final Context context, final Contact contact, final String phoneNumber, final String photo){
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.custom_diglog_favourite, null);
        final android.support.v7.app.AlertDialog deleteDialog = new android.support.v7.app.AlertDialog.Builder(context).create();


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
//                      if(controller.checkcallorsms(contact,"Messager") ==null){
//                          boolean check =  controller.insertDevice(contact,"Messager",phoneNumber,photo);
//                      }else;
               if( contact.isFavorite()){

                }else {
                   contact.setFavorite(true);
                   ContactHelper.setFavorite(context, contact.getId());


               }
                deleteDialog.dismiss();

            }
        });
        deleteDialogView.findViewById(R.id.txt_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(controller.checkcallorsms(contact,"Call") ==null){
//                    boolean check =  controller.insertDevice(contact,"Call",phoneNumber,photo);
//                }else;
                if( contact.isFavorite()){
                   // String toast = context.getString(R.string.detail_activity_title_toast_remove_favorite).replace("{name}", contact.getName());
                    //Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();

                }else {
                    contact.setFavorite(true);
                    ContactHelper.setFavorite(context, contact.getId());

                }
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
}

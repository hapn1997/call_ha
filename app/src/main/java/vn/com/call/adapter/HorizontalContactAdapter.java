package vn.com.call.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huyanh.base.custominterface.OnClickContactListener;
import com.huyanh.base.dao.CallOrSms;
import com.huyanh.base.utils.BaseConstant;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.call.R;
import vn.com.call.adapter.listener.OnClickViewConversationListener;
import vn.com.call.favController;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.ui.ChooseNumberDialogFragment;
import vn.com.call.ui.ChooseNumberPopupWindow;
import vn.com.call.ui.ContactDetailActivity;
import vn.com.call.ui.main.MainActivity;
import vn.com.call.utils.CallUtils;
import vn.com.call.widget.AvatarView;

/**
 * Created by ngson on 10/07/2017.
 */

public class HorizontalContactAdapter extends RecyclerView.Adapter<HorizontalContactAdapter.HorizontalViewHolder> {
    private Context context;
    private List<Contact> mContacts;
    favController controller;
    private OnClickViewConversationListener onClickViewConversation;
    private OnClickContactListener onClickContactListener;
    Fragment fragment;
   private  boolean checkRemove;
    public HorizontalContactAdapter(Fragment fragment,List<Contact> mContacts) {
        this.mContacts = mContacts;
        this.context = fragment.getContext();
        this.fragment= fragment;

    }

    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HorizontalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_horizontal, parent, false));
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final HorizontalViewHolder holder, int position) {
        final Contact contact = mContacts.get(position);

        //final Context context = holder.root.getContext();
        controller = new favController(context);
        try {
            controller.isCreatedDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final List<PhoneNumber> phoneNumbers = contact.getNumbers();
        String numberCacheColor = phoneNumbers != null && phoneNumbers.size() > 0 ? phoneNumbers.get(0).getNumber() : "";
        holder.avatar.loadAvatar(contact.getPhoto(), contact.getName(), numberCacheColor);
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.info.getContext();
                if (context instanceof Activity)
                    ContactDetailActivity.launch((Activity) context, holder.avatar, contact, BaseConstant.REQUEST_CODE_SHOW_POPUP);
                else ContactDetailActivity.launch(context, contact);
            }
        });
        holder.name.setText(contact.getName());

             //  holder.imagecallormes.setImageDrawable(context.getDrawable(R.drawable.iconfavcall1l));
               //holder.callorMess.setText(contact.getCallorrsms());

              // holder.imagecallormes.setImageDrawable(holder.callorMess.getText().length() ==4?context.getDrawable(R.drawable.iconfavcall1l) : context.getDrawable(R.drawable.iconfavcall));


        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkRemove) {
                      showSetting(contact,holder);
                }else {
//                    if (holder.callorMess.getText().length() == 4) {
//                        if (phoneNumbers.size() > 1) {
//                            ChooseNumberPopupWindow chooseNumberPopupWindow = new ChooseNumberPopupWindow(context, contact, ChooseNumberPopupWindow.Action.CALL);
//                            chooseNumberPopupWindow.showPopup(holder.root, onClickViewConversation);
//                        } else {
//                            if (onClickContactListener != null)
//                                onClickContactListener.onClick(new CallOrSms(phoneNumbers.get(0).getNumber(), true));
//                                //Log.d("dsđs",phoneNumbers.get(0).getNumber());
//                            else CallUtils.makeCall(context, phoneNumbers.get(0).getNumber());
//                        }
//                    } else {
//                        StringBuilder stringBuilder = new StringBuilder();
//                        stringBuilder.append("sms:");
//                        stringBuilder.append(phoneNumbers.get(0).getNumber());
//                        context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
//
//                    }
                    if (phoneNumbers.size() > 1) {
                            ChooseNumberPopupWindow chooseNumberPopupWindow = new ChooseNumberPopupWindow(context, contact, ChooseNumberPopupWindow.Action.CALL);
                            chooseNumberPopupWindow.showPopup(holder.root, onClickViewConversation);
                        } else {
                            if (onClickContactListener != null)
                                onClickContactListener.onClick(new CallOrSms(phoneNumbers.get(0).getNumber(), true));
                                //Log.d("dsđs",phoneNumbers.get(0).getNumber());
                            else CallUtils.makeCall(context, phoneNumbers.get(0).getNumber());
                        }
                }

            }
        });
        if(checkRemove){
            holder.imageView.setVisibility(View.VISIBLE);
        }else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.imageView.setVisibility(checkRemove ==true ? View.VISIBLE : View.GONE);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    public void checkRemove(boolean check){
        checkRemove = check;
        notifyDataSetChanged();
       Log.d("fdfdff", String.valueOf(checkRemove));


    }
    public boolean check(){
        notifyDataSetChanged();
        Log.d("fdfdffsd", String.valueOf(checkRemove));
        return this.checkRemove;


    }
    public void showSetting(final Contact contact, final HorizontalViewHolder holder){
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.dialog_remove_fav, null);
        final android.support.v7.app.AlertDialog deleteDialog = new android.support.v7.app.AlertDialog.Builder(context).create();


        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        deleteDialog.getWindow().setLayout(-1, -1);
        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.relative_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();

                notifyDataSetChanged();

            }
        });
        deleteDialogView.findViewById(R.id.txt_mess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // controller.deleteDevice(contact,holder.callorMess.getText().toString());
                contact.changeFavorite(context);
                deleteDialog.dismiss();
//
                fragment.getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
            }
        });

        deleteDialogView.findViewById(R.id.txt_cancel_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                notifyDataSetChanged();
            }
        });


        deleteDialog.show();
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    static class HorizontalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.info)
        ImageButton info;
        @BindView(R.id.root)
        View root;
        @BindView(R.id.avatar)
        AvatarView avatar;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.imagecallOrMess)
        ImageView imagecallormes;
        @BindView(R.id.date)
        TextView callorMess;
        @BindView(R.id.imageremove)
        ImageView imageView;
        public HorizontalViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

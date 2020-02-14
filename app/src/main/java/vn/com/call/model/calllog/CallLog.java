package vn.com.call.model.calllog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import vn.com.call.R;
import vn.com.call.db.cache.CallLogHelper;
import vn.com.call.ui.callback.SwipeToDeleteCallback;
import vn.com.call.ui.main.CallLogFragment;
import vn.com.call.ui.main.MainActivity;

/**
 * Created by ngson on 03/07/2017.
 */

public class CallLog implements Parcelable {
    private String idContact;
    private String photoContact;
    private String nameContact;

    private String number;

    private List<CallLogDetail> details = new ArrayList<>();

    public String getIdContact() {
        return idContact;
    }

    public void setIdContact(String idContact) {
        this.idContact = idContact;
    }

    public String getPhotoContact() {
        return photoContact;
    }

    public void setPhotoContact(String photoContact) {
        this.photoContact = photoContact;
    }

    public String getNameContact() {
        return nameContact;
    }

    public void setNameContact(String nameContact) {
        this.nameContact = nameContact;
    }

    public CallLog(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void addCallLog(CallLogDetail callLog) {
        details.add(callLog);
    }

    public List<CallLogDetail> getDetails() {
        return details;
    }

    public void enableSwipeToDeleteAndUndo(final Context context, final boolean finishAfterDelete, final CallLogFragment fragment) {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

                final int position = viewHolder.getAdapterPosition();
                Log.d("vvfvfvfv", String.valueOf(position));
                //mFavoriteContacts.get(position).changeFavorite(getContext());
                deleteAllCallLogs(context, finishAfterDelete);
                fragment.getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();


            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        //itemTouchhelper.attachToRecyclerView(mList);
    }
    public void delete(final Context context, boolean showConfirm, final boolean finishAfterDelete, final CallLogFragment fragment) {
        if (showConfirm) {

            LayoutInflater factory = LayoutInflater.from(context);
            final View deleteDialogView = factory.inflate(R.layout.dialog_remove_fav, null);
            final android.support.v7.app.AlertDialog deleteDialog = new android.support.v7.app.AlertDialog.Builder(context).create();

//        TextView txt_message = deleteDialogView.findViewById(R.id.txt_message);
//        txt_message.setText("");
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
                    // controller.deleteDevice(contact,holder.callorMess.getText().toString());
                    deleteAllCallLogs(context, finishAfterDelete);
                    deleteDialog.dismiss();

                    fragment.getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();



                }
            });

            deleteDialogView.findViewById(R.id.txt_cancel_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();

                }
            });

            deleteDialog.show();


        } else deleteAllCallLogs(context, finishAfterDelete);

    }

    private void deleteAllCallLogs(final Context context, final boolean finishAfterDelete) {
        CallLogHelper.deleteCallLog(context, details)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (finishAfterDelete && context instanceof Activity) ((Activity) context).finish();

                    }
                });
    }
    public void deleteAllCallLogs12(final Context context, final boolean finishAfterDelete) {
        CallLogHelper.deleteAllCallLog12(context)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (finishAfterDelete && context instanceof Activity) ((Activity) context).finish();

                    }
                });
    }


    public CallLog() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idContact);
        dest.writeString(this.photoContact);
        dest.writeString(this.nameContact);
        dest.writeString(this.number);
        dest.writeTypedList(this.details);
    }

    protected CallLog(Parcel in) {
        this.idContact = in.readString();
        this.photoContact = in.readString();
        this.nameContact = in.readString();
        this.number = in.readString();
        this.details = in.createTypedArrayList(CallLogDetail.CREATOR);
    }

    public static final Creator<CallLog> CREATOR = new Creator<CallLog>() {
        @Override
        public CallLog createFromParcel(Parcel source) {
            return new CallLog(source);
        }

        @Override
        public CallLog[] newArray(int size) {
            return new CallLog[size];
        }
    };
}

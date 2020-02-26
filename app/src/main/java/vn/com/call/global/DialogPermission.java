package vn.com.call.global;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.dialer.ios.iphone.contacts.R;

public class DialogPermission {

    private static AlertDialog dialog;
    private static Activity activity;
    private static long timeShowDialog;

    public static synchronized void showDialog(Activity activity, final DialogAppCallback callback) {
        try {
            long timeNow = System.currentTimeMillis();
            if ((timeNow - timeShowDialog) < 3000)
                return;
            timeShowDialog = timeNow;

            DialogPermission.activity = activity;
            cancel();

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
            LayoutInflater inflater = (activity).getLayoutInflater();
            builder.setCancelable(false);

            View view = inflater.inflate(R.layout.dialog_permission_layout, null);
            ImageView imageViewStep1 = (ImageView) view.findViewById(R.id.dialog_permission_img_step_1);
            ImageView imageViewStep2 = (ImageView) view.findViewById(R.id.dialog_permission_img_step_2);

            Glide.with(activity).load(R.drawable.dialog_ic_permission_step_1)
                    .into(imageViewStep1);
            Glide.with(activity).load(R.drawable.dialog_ic_permission_step_2)
                    .into(imageViewStep2);

            builder.setView(view);

            builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (callback != null)
                        callback.okDialog();
                    cancel();
                }
            });

            builder.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (callback != null)
                        callback.cancelDialog();
                    cancel();
                }
            });

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        dialog = builder.create();
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    public static void cancel() {
        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog = null;
                }
            });
        } catch (Exception e) {
        }
    }

}

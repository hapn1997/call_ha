package vn.com.call.call.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.com.call.App;
import vn.com.call.R;
import vn.com.call.call.model.CallFlashItem;
import vn.com.call.call.model.ContactItem;
import vn.com.call.call.util.CacheGifUtil;
import vn.com.call.call.util.CallFlashManager;
import vn.com.call.call.util.CallUtil;

public class ServiceCallRing extends Service {

    private static View layout;
    private static Timer mTimer;
    private static TimerTask mTimerTask;

    private WindowManager mWindowManager;
    private ScalableVideoView scalableVideoView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();
        try {
            addWindowViewCall(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    public void onDestroy() {
        try {
            if (layout != null)
                mWindowManager.removeView(layout);
            layout = null;

            try {
                if (scalableVideoView != null) {
                    scalableVideoView.setLooping(false);
                    scalableVideoView.pause();
                    scalableVideoView.stop();
                    scalableVideoView.release();
                    scalableVideoView = null;
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addWindowViewCall(Intent intent) {
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        try {
            if (layout != null)
                mWindowManager.removeView(layout);
            layout = null;
        } catch (Exception e) {
        }
        ContactItem contactItem = getContactDetail(getApplicationContext(), incomingNumber);

        layout = View.inflate(getApplicationContext(), R.layout.calling_fullscreen_layout, null);
        CallFlashItem callFlashItem = ((App) getApplicationContext()).getCallFlashItem();
        if (callFlashItem == null) {
            callFlashItem = CallFlashManager.getItemCallFlash(this);
            ((App) getApplicationContext()).setCallFlashItem(callFlashItem);
        }
        if (callFlashItem != null) {
            try {
                setupVideoPlayer(layout, callFlashItem);
            } catch (Exception e) {
                stopSelf();
                return;
            }
        }

        ImageView imgBg = layout.findViewById(R.id.calling_image_bg);
        CircleImageView imgAvatar = layout.findViewById(R.id.calling_image_avatar);
        TextView txtName = layout.findViewById(R.id.calling_txt_name);
        TextView txtPhone = layout.findViewById(R.id.calling_txt_phone);

        try {
            if (TextUtils.isEmpty(callFlashItem.getThumb())) {
                Picasso picasso = ((App) getApplicationContext()).getPicasso();
                picasso.load(callFlashItem.getThumb()).resize(1000, 2000).centerCrop().into(imgBg);
            }
        } catch (Exception e) {
        }

        imgAvatar.setImageBitmap(contactItem.getPhoto());
        txtName.setText(contactItem.getName());
        txtPhone.setText(contactItem.getPhoneNumber());

        View btnNgheMay = layout.findViewById(R.id.calling_btn_nghe_may);
        View btnCupMay = layout.findViewById(R.id.calling_btn_cup_may);
        btnNgheMay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallUtil.acceptCall(ServiceCallRing.this.getApplicationContext());
                ServiceCallRing.this.stopSelf();
            }
        });
        btnCupMay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallUtil.rejectCall(ServiceCallRing.this.getApplicationContext());
                stopWindowsAnim(getApplicationContext());
            }
        });

        try {
            if (Build.VERSION.SDK_INT >= 16) {
                layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                );
            }
        } catch (Exception e) {
        }
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        } catch (Exception e) {
        }
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                PixelFormat.TRANSPARENT);
        mWindowManager.addView(layout, wmParams);

    }

    private void setupVideoPlayer(View layout, final CallFlashItem item) throws Exception {
        scalableVideoView = layout.findViewById(R.id.calling_video_player);
        scalableVideoView.setDataSource(this,
                Uri.fromFile(CacheGifUtil.getCacheFileGif(this, item)));
        scalableVideoView.setLooping(true);
        scalableVideoView.prepare();
        scalableVideoView.start();
    }

    private ContactItem getContactDetail(Context context, String number) {
        ContactItem contactItem = new ContactItem();
        contactItem.setPhoneNumber(number);

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
//            }
//            cursor.close();
//        }

        String contactName = null, contactId = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (TextUtils.isEmpty(contactName))
            contactName = "Unknown";
        contactItem.setName(contactName);

        Bitmap photo = null;
        try {
            InputStream inputStream = null;
            try {
                inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId)));
            } catch (Exception e) {
            }
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (Exception e) {
        }
        if (photo == null)
            photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_avatar_default);
        contactItem.setPhoto(photo);

        return contactItem;
    }

    public static void stopWindowsAnim(final Context context) {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                public void run() {
                    Intent intent = new Intent(context, ServiceCallRing.class);
                    context.stopService(intent);

                    mTimerTask = null;
                    mTimer = null;
                }
            };
            mTimer.schedule(mTimerTask, 1000);
        }
    }

}

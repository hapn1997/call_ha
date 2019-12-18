package vn.com.call;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huyanh.base.BaseApplication;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;
import vn.com.call.call.model.CallFlashItem;
import vn.com.call.db.Settings;

import vn.vmb.security.AppLock;

public class App extends BaseApplication implements Application.ActivityLifecycleCallbacks {

    private final static String TAG = App.class.getSimpleName();

    private static App thisApp;

    public static App getAppThis() {
        return thisApp;
    }

    private Settings mSettings;
    private boolean running = false;
    private Gson gson;
    private Picasso picasso;
    // private CircleTransform circleTransform;
     private CallFlashItem callFlashItem;
    private Handler mHandlerLockApp;
    private Runnable mLockApp = new Runnable() {
        @Override
        public void run() {
            // lockApp();
        }
    };
    // private ShowChatheadIconSchedule mShowChatheadIconSchedule;

    private HashMap<String, String> mapId;
    // private ArrayList<FriendFBItem> listFriendFB;

    @Override
    public void onCreate() {
        super.onCreate();
        thisApp = this;

        setupPicasso();
        Fabric.with(getApplicationContext(), new Crashlytics());
        Paper.init(getApplicationContext());
       // AppLock.initAppLock(this, LockSession.LOCK_APP, "vietmobi");
//        AppLock.setKeepUnlockInMillisecond(LockSession.LOCK_APP, 0);

        //  mShowChatheadIconSchedule = new ShowChatheadIconSchedule(this);

        mHandlerLockApp = new Handler(getMainLooper());
        registerActivityLifecycleCallbacks(this);
        scheduleJob();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        mHandlerLockApp.removeCallbacks(mLockApp);

        running = true;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mHandlerLockApp.removeCallbacks(mLockApp);

        running = true;

        //Log.wtf(TAG, running + " onActivityStarted");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mHandlerLockApp.postDelayed(mLockApp, 1000);

        running = false;

        //    mShowChatheadIconSchedule.startSchedule();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        running = false;

        //Log.wtf(TAG, running + " onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        try {
            detachActivityFromActivityManager(activity);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        running = false;
        // mShowChatheadIconSchedule.startSchedule();
        //Log.wtf(TAG, running + " onActivityDestroyed");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mHandlerLockApp.removeCallbacks(mLockApp);

        running = true;

        //  mShowChatheadIconSchedule.removeSchedule();
    }

    public Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }

    private void setupPicasso() {
        Downloader downloader = new OkHttpDownloader(getApplicationContext(), 1024 * 1024 * 50);
        Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
        builder.downloader(downloader).memoryCache(new LruCache(1024 * 1024 * 30));
        picasso = builder.build();
    }

    public Picasso getPicasso() {
        if (picasso == null)
            setupPicasso();
        return picasso;
    }

    public void loadImageUser(ImageView imageView, String url) {
        if (picasso != null && url != null && url.length() > 5) {
//            if (circleTransform == null)
//                circleTransform = new CircleTransform();
//            picasso.load(url).transform(circleTransform).resize(256, 256)
//                    .centerCrop().into(imageView);
        }
    }

    public HashMap<String, String> getMapId() {
        getGson();
        if (mapId == null) {
            try {
                //   String str = SharedPreferencesFBMessUtil.getValue(this, Constant.KEY_MAP_ID_FB);
//                if (!TextUtils.isEmpty(str)) {
//                    Type type = new TypeToken<HashMap<String, String>>() {
//                    }.getType();
//                    mapId = gson.fromJson(str, type);
//                }
            } catch (Exception e) {
            }
        }
        if (mapId == null)
            mapId = new HashMap<>();
        return mapId;
    }

    public void saveMapId() {
        getMapId();
        getGson();
        String str = gson.toJson(mapId);
        //  SharedPreferencesFBMessUtil.setValue(this, Constant.KEY_MAP_ID_FB, str);
    }
//
//    public ArrayList<FriendFBItem> getListFriendFB() {
//        getGson();
//        if (listFriendFB == null) {
//            try {
//                String str = SharedPreferencesFBMessUtil.getValue(this, Constant.KEY_LIST_FR_FB);
//                if (!TextUtils.isEmpty(str)) {
//                    Type type = new TypeToken<ArrayList<FriendFBItem>>() {
//                    }.getType();
//                    listFriendFB = gson.fromJson(str, type);
//                }
//            } catch (Exception e) {
//            }
//        }
//        if (listFriendFB == null)
//            listFriendFB = new ArrayList<>();
//        return listFriendFB;
//    }

    public void saveListFriendFB() {
        //  getListFriendFB();
        getGson();
        //  String str = gson.toJson(listFriendFB);
        //  SharedPreferencesFBMessUtil.setValue(this, Constant.KEY_LIST_FR_FB, str);
    }

    public CallFlashItem getCallFlashItem() {
        return callFlashItem;
    }

    public void setCallFlashItem(CallFlashItem callFlashItem) {
        this.callFlashItem = callFlashItem;
    }

    private void lockApp() {
     //   AppLock.setLock(true, LockSession.LOCK_APP);
    }

    private void detachActivityFromActivityManager(Activity activity) throws
            NoSuchFieldException, IllegalAccessException {
        ActivityManager activityManager = (ActivityManager) activity.
                getSystemService(Context.ACTIVITY_SERVICE);

        Field contextField = activityManager.getClass().getDeclaredField("mContext");

        int modifiers = contextField.getModifiers();
        if ((modifiers | Modifier.STATIC) == modifiers) {
            // field is static on Samsung devices only
            contextField.setAccessible(true);

            if (contextField.get(null) == activity) {
                contextField.set(null, null);
            }
        }
    }

    private void scheduleJob() {
        //  JobManager.create(this).addJobCreator(new MessageJobCreator());

        rescheduleMessage();
    }

    public void rescheduleMessage() {
        //JobManager.instance().cancelAllForTag(ScheduleMessageJob.TAG);

        //  ScheduleMessageJob.scheduleJob(this);
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
package vn.com.call.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AppCheckService extends Service {
    public AppCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

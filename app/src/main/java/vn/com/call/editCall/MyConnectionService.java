package vn.com.call.editCall;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyConnectionService extends Service {
    public MyConnectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
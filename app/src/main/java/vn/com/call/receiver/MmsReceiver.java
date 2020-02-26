package vn.com.call.receiver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dialer.ios.iphone.contacts.R;

public class MmsReceiver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mms_receiver);
    }
}

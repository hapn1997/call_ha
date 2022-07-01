package vn.com.call.receiver;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.phone.thephone.call.dialer.R;

public class MmsReceiver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mms_receiver);
    }
}

package vn.vmb.security;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import vn.vmb.security.listener.OnCreatePasswordListener;
import vn.vmb.security.listener.OnUnlockListener;

/**
 * Created by ngson on 20/09/2017.
 */

public class SecurityScreenCompatActivity extends AppCompatActivity implements OnUnlockListener, OnCreatePasswordListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onUnlockSuccess() {

    }

    @Override
    public void onUnlockFail() {

    }

    @Override
    public void onCreatePasswordSuccess() {

    }
}

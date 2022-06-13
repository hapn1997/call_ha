package vn.com.call.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ngson on 12/06/2017.
 */

public abstract class BaseFragment extends com.huyanh.base.BaseFragment {
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(getLayoutId(), container, false);

        mUnbinder = ButterKnife.bind(this, root);

        onCreateView(savedInstanceState);

        return root;
    }

    protected abstract void onCreateView(Bundle savedInstanceState);

    protected abstract int getLayoutId();

    /*public BaseApplication getBaseApplication() {
        return (BaseApplication) (((BaseActivity) getActivity()).getApplication());
    }*/

    @Override
    public void onDestroyView() {
        if (mUnbinder != null) mUnbinder.unbind();
        super.onDestroyView();
    }
}

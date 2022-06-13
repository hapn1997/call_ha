package vn.com.call.ui.intro;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.dialer.ios.iphone.contacts.R;

public class BaseIntroSlideFragment extends Fragment {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private static final String ARG_IMAGE_RES_ID = "imageResId";
    private int layoutResId;
    private int imageResId;

    public static BaseIntroSlideFragment newInstance(int resId) {
        BaseIntroSlideFragment sampleSlide = new BaseIntroSlideFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, R.layout.intro_custom_layout);
        args.putInt(ARG_IMAGE_RES_ID, resId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
            imageResId = getArguments().getInt(ARG_IMAGE_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutResId, container, false);
        ImageView imageView = view.findViewById(R.id.intro_custom_image);
        imageView.setImageResource(imageResId);
        return view;
    }
}
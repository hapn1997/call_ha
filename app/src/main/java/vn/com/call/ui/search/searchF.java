package vn.com.call.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;


import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import vn.com.call.R;
import vn.com.call.bus.ChangeKeyword;
import vn.com.call.ui.BaseFragment;
import vn.com.call.ui.main.ContactFragment;
import vn.com.call.ui.main.FavoritesAddFragment;


/**
 * Created by ngson on 28/06/2017.
 */

public class searchF extends BaseFragment {
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppbarLayout;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private int mHeightAppbar;

    public static searchF newInstance() {
        Bundle args = new Bundle();

        searchF fragment = new searchF();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        ((FavoritesAddFragment) getChildFragmentManager().findFragmentById(R.id.contact_fragment)).hideButtonAddContact();
//        mAppbarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mHeightAppbar = mAppbarLayout.getHeight();
//                mAppbarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                mAppbarLayout.setTranslationY(-mHeightAppbar);
//            }
//        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fav_search;
    }

    public void changeKeyword(String keyword) {


        mViewPager.setAdapter(new SearchPagerAdapter(getContext(), getChildFragmentManager(), keyword));
        // mViewPager.setOffscreenPageLimit(1);
        mViewPager.setVisibility(View.VISIBLE);
        // mAppbarLayout.animate().translationY(-mHeightAppbar);
        EventBus.getDefault().post(new ChangeKeyword(keyword));

    }

    static class SearchPagerAdapter extends FragmentPagerAdapter {
        private Context context;
        private String keyword;

        public SearchPagerAdapter(Context context, FragmentManager fm, String keyword) {
            super(fm);
            this.context = context;
            this.keyword = keyword;
        }

        @Override
        public Fragment getItem(int position) {
            return SearchFavFragment.newInstance(keyword);

        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return context.getString(R.string.title_contact);
        }
    }
}

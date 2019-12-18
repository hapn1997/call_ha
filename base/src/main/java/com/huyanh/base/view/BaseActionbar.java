package com.huyanh.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huyanh.base.BaseApplication;
import com.huyanh.base.R;
import com.huyanh.base.utils.BaseUtils;

public class BaseActionbar extends RelativeLayout {

    public BaseActionbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.BaseActionbar, defStyleAttr, 0);
            setAttributes(a);
        }
        initView();
    }

    public BaseActionbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.BaseActionbar);
            setAttributes(a);
        }
        initView();
    }

    public BaseActionbar(Context context) {
        super(context);
        initView();
    }

    private Drawable bgBaseActionbar;
    private int bgBaseActionbarColor = Color.TRANSPARENT;
    private Drawable srcLeft, srcRight;
    private int titleColor = Color.WHITE;
    private String title = "";
    private boolean isSearch = false;

    private void setAttributes(TypedArray a) {
        if (a.hasValue(R.styleable.BaseActionbar_bgBaseActionbar)) {
            bgBaseActionbar = a.getDrawable(R.styleable.BaseActionbar_bgBaseActionbar);
        }
        if (a.hasValue(R.styleable.BaseActionbar_bgBaseActionbarColor)) {
            bgBaseActionbarColor = a.getColor(R.styleable.BaseActionbar_bgBaseActionbarColor, Color.TRANSPARENT);
        }
        if (a.hasValue(R.styleable.BaseActionbar_srcLeft)) {
            srcLeft = a.getDrawable(R.styleable.BaseActionbar_srcLeft);
        }

        if (a.hasValue(R.styleable.BaseActionbar_titleColor)) {
            titleColor = a.getColor(R.styleable.BaseActionbar_titleColor, Color.WHITE);
        }
        if (a.hasValue(R.styleable.BaseActionbar_title)) {
            title = a.getString(R.styleable.BaseActionbar_title);
        }
        if (a.hasValue(R.styleable.BaseActionbar_isSearch)) {
            isSearch = a.getBoolean(R.styleable.BaseActionbar_isSearch, false);
            if (isSearch) {
                srcRight = ContextCompat.getDrawable(getContext(), R.drawable.ic_search_white_48dp);
            }
        }
        if (a.hasValue(R.styleable.BaseActionbar_srcRight)) {
            srcRight = a.getDrawable(R.styleable.BaseActionbar_srcRight);
        }
    }


    private RelativeLayout rlBaseActionbar;
    private ImageView ivLeft, ivRight;
    private TextView tvTitle;

    private BaseApplication baseApplication;

    private EditText et;

    public EditText getEt() {
        return et;
    }

    private void initView() {
        baseApplication = (BaseApplication) getContext().getApplicationContext();
        View view = inflate(getContext(), R.layout.base_actionbar, null);
        addView(view);
        rlBaseActionbar = (RelativeLayout) view.findViewById(R.id.rlBaseActionbar);
        if (bgBaseActionbar != null)
            rlBaseActionbar.setBackground(bgBaseActionbar);
        else
            rlBaseActionbar.setBackgroundColor(bgBaseActionbarColor);

        ivLeft = (ImageView) view.findViewById(R.id.ivLeft);
        if (srcLeft != null)
            ivLeft.setImageDrawable(srcLeft);
        ivLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenerBaseActionbar != null) listenerBaseActionbar.onClickLeft();
            }
        });

        ivRight = (ImageView) view.findViewById(R.id.ivRight);
        if (srcRight != null)
            ivRight.setImageDrawable(srcRight);
        ivRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenerBaseActionbar != null) listenerBaseActionbar.onClickRight();
                if (isSearch) {
                    if (et.isShown()) {
                        goneSearch();
                    } else {
                        et.setVisibility(View.VISIBLE);
                        tvTitle.setVisibility(View.GONE);
                        et.requestFocus();
                        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(2, 1);
                        ivRight.setImageResource(R.drawable.ic_close_white_48dp);
                    }
                }
            }
        });

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setTypeface(baseApplication.getBaseTypeface().getRegular());
        tvTitle.setTextColor(titleColor);
        tvTitle.setText(title);

        et = (EditText) view.findViewById(R.id.autoEt);
        et.setTextColor(titleColor);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listenerBaseActionbar != null)
                    listenerBaseActionbar.onSearch(BaseUtils.removeSpecial(BaseUtils.removeAccent(et.getText().toString().toLowerCase())));
            }
        });
    }

    public void goneSearch() {
        et.setText("");
        et.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(et.getWindowToken(), 0);
        if (listenerBaseActionbar != null)
            listenerBaseActionbar.onSearch("");
        ivRight.setImageDrawable(srcRight);
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    private ListenerBaseActionbar listenerBaseActionbar;

    public interface ListenerBaseActionbar {
        void onClickLeft();

        void onClickRight();

        void onSearch(String value);
    }

    public void setListenerBaseActionbar(ListenerBaseActionbar listenerBaseActionbar) {
        this.listenerBaseActionbar = listenerBaseActionbar;
    }
}

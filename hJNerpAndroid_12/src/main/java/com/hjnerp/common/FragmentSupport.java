package com.hjnerp.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.hjnerp.widget.MyToast;


/**
 * Created by Admin on 2017/10/16.
 */

public class FragmentSupport extends Fragment {

    protected Context mContext;
    protected Gson mGson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mContext = getActivity();
        mGson = new Gson();
    }


    /**
     * 提交后的提示框
     *
     * @param content
     */
    public void showFailToast(String content) {
        new MyToast(mContext, content);
    }
}

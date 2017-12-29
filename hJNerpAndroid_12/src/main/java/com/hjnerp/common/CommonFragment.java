package com.hjnerp.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hjnerp.dao.QiXinBaseDao;
import com.hjnerp.model.UserInfo;
import com.hjnerp.widget.WaitDialogRectangle;
import com.hjnerpandroid.R;

/**
 * Created by Admin on 2017/11/14.
 */

public class CommonFragment extends Fragment {
    protected UserInfo myinfo;
    protected String sessionID;
    protected Gson mGson;

    //html标题
    public final String HTTITLE = "TITLE";
    //html地址
    public final String HTURL = "HTURL";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private void  init(){
        myinfo = QiXinBaseDao.queryCurrentUserInfo();
        if (myinfo == null) {
            ActionBarWidgetActivity.showFailToast("请下载基础数据");
            return;
        }

        mGson = new Gson();
        sessionID = myinfo.sessionID;
    }
    /**
     * 将View转换成Bitmap
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache(true);
    }

    /**
     * 得到名字布局
     *
     * @param name
     * @return
     */
    public static View getPotoView(Context context, String name) {
        View phView = LayoutInflater.from(context).inflate(R.layout.view_name_photo, null);
        TextView nameImg = (TextView) phView.findViewById(R.id.view_nameph_img);
        nameImg.setText(name);
        android.util.Log.e("show", nameImg.hashCode() + "");
        return phView;
    }

    /**
     * bundle
     *
     * @param to
     */
    public void intentActivity(Context context,Class to, Bundle bundle) {
        Intent intent = new Intent(context, to);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * 数据打印
     *
     * @param content
     */
    public static void LogShow(String content) {
        if (Constant.isLogShow)
            Log.e("EJ", content);
    }
}

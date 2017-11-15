package com.hjnerp.common;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hjnerpandroid.R;

/**
 * Created by Admin on 2017/11/14.
 */

public class CommonFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}

package com.hjnerp.activity.myinfo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.net.ChatConstants;
import com.hjnerp.net.ChatPacketHelper;
import com.hjnerp.util.Log;
import com.hjnerp.util.StringUtil;
import com.hjnerp.util.bitmap.BitmapUtils;
import com.hjnerpandroid.R;
import com.itheima.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowPortraitActivity extends ActionBarWidgetActivity implements View.OnClickListener {
    @BindView(R.id.portraitImg)
    RoundedImageView portraitImg;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    private String photoUrl;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_portrait);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        actionCenterTv.setText(getString(R.string.MyInfor_Title_PhotoPortrait));
        actionRightTv.setVisibility(View.GONE);
        portraitImg.setOnClickListener(this);
        actionLeftTv.setOnClickListener(this);
        photoUrl = getIntent().getStringExtra("photoUrl");
        userName = getIntent().getStringExtra("userName");

        // 设置头像
        if (StringUtil.isStrTrue(photoUrl)) {
            String url = ChatPacketHelper.buildImageRequestURL(photoUrl,
                    ChatConstants.iq.DATA_VALUE_RES_TYPE_ATTACH);
//            ImageLoaderHelper.displayImage(url, viewHolder.pic);
            ImageLoader.getInstance().displayImage(
                    url,
                    portraitImg,
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            portraitImg.setImageBitmap(
                                    BitmapUtils.convertViewToBitmap(
                                            ActionBarWidgetActivity.getPotoView(
                                                    context, StringUtil.doubleName(userName))));
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
        } else {
            portraitImg.setImageBitmap(
                    BitmapUtils.convertViewToBitmap(
                            ActionBarWidgetActivity.getPotoView(
                                    context, StringUtil.doubleName(userName))));
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_left_tv:
                finish();
                break;
        }
    }
}

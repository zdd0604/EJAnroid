package com.hjnerp.html;

import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.hjnerp.activity.html.HjAboutInformation;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.HttpUrlAddress;
import com.hjnerp.util.StringUtil;
import com.hjnerp.widget.MyToast;

public class HtmlUtils extends ActionBarWidgetActivity {

    public static HtmlCallBack mHtmlCallBack ;

    public static void setHtmlCallBack(HtmlCallBack htmlCallBack) {
        HtmlUtils.mHtmlCallBack = htmlCallBack;
    }

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    // 接受JS回调回来的数据
    @JavascriptInterface
    public void hello(String msg) {
        if (!StringUtil.isStrTrue(msg)) {
            new MyToast(mContext, "JS回调数据为空");
            return;
        }

        if (mHtmlCallBack!=null)
            mHtmlCallBack.JsLocality(msg);
    }


    public interface HtmlCallBack {
        void JsLocality(String msg);
    }
}

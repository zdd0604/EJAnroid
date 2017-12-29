package com.hjnerp.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.hjnerp.activity.html.HjAboutInformation;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.CommonFragment;
import com.hjnerp.common.HttpUrlAddress;
import com.hjnerp.dao.QiXinBaseDao;
import com.hjnerp.html.HtmlUtils;
import com.hjnerp.model.HttpNoticeInfo;
import com.hjnerp.model.UserInfo;
import com.hjnerp.util.StringUtil;
import com.hjnerp.widget.MyToast;
import com.hjnerp.widget.WaitDialogRectangle;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 公告界面
 * Created by zdd on 2017/11/28.
 */

public class NoticeFragment extends CommonFragment implements HtmlUtils.HtmlCallBack {
    @BindView(R.id.noticeWeb)
    WebView noticeWeb;
    @BindView(R.id.relayout_html_error)
    RelativeLayout relayout_html_error;


    private View view;
    private UserInfo myinfo;
    private String imageUrl;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice, container, false);
        //绑定fragment
        ButterKnife.bind(this, view);
        initView();
        return view;
    }


    @SuppressLint("JavascriptInterface")
    private void initView() {

        HtmlUtils.setHtmlCallBack(this);

        relayout_html_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageLoaderUrl();
            }
        });

        myinfo = QiXinBaseDao.queryCurrentUserInfo();
        if (myinfo == null) {
            new MyToast(getActivity(), "请下载基础数据");
            return;
        }

        //声明WebSettings子类
        WebSettings webSettings = noticeWeb.getSettings();

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //缩放操作 支持缩放，默认为true。是下面那个的前提。
        webSettings.setSupportZoom(true);
        //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setBuiltInZoomControls(false);
        //隐藏原生的缩放控件
        webSettings.setDisplayZoomControls(false);

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //设置编码格式
        webSettings.setDefaultTextEncodingName("utf-8");
        //webView拓展的api是否打开：
        webSettings.setDomStorageEnabled(true);
        //在高版本的时候我们是需要使用允许访问文件的urls：
        webSettings.setAllowFileAccessFromFileURLs(true);

        noticeWeb.addJavascriptInterface(new HtmlUtils(), "obj");//AndroidtoJS类对象映射到js的test对象

        getImageLoaderUrl();
    }

    /**
     * 获取H5加载图片地址
     */
    private void getImageLoaderUrl() {
        OkGo.post(HttpUrlAddress.NOTICEH5ANNO)
                .params("bill_type", "h5anno,h5annoFore")
                .params("id_com", myinfo.companyID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        setJson(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LogShow("网页加载错误");
                    }
                });
        LogShow(HttpUrlAddress.NOTICEH5ANNO + myinfo.companyID);
    }

    /**
     * 解析JSON
     *
     * @param data
     */
    private void setJson(String data) {
        LogShow("公告返回数据："+data);
        if (!StringUtil.isStrTrue(data)) {
            new MyToast(getActivity(), "图片地址获取失败");
            return;
        }

        HttpNoticeInfo httpNoticeInfo = mGson.fromJson(data, HttpNoticeInfo.class);
        imageUrl = httpNoticeInfo.getExtAddr().getVar_extaddr();
        HttpUrlAddress.PUBLICHEADER = httpNoticeInfo.getExtAddrFore().getVar_extaddr();
        //测试服务器的端口测试后可以删除
//        HttpUrlAddress.PUBLICHEADER = "http://172.16.12.236:8082";

        noticeWeb.loadUrl(HttpUrlAddress.PUBLICHEADER + HttpUrlAddress.NOTICEURL);
        LogShow(HttpUrlAddress.PUBLICHEADER + HttpUrlAddress.NOTICEURL);
        noticeWeb.setWebViewClient(new MyWebViewClient());
        noticeWeb.setWebChromeClient(new WebChromeClient());


    }

    /**
     * 传值给js
     */
    private class MyWebViewClient extends WebViewClient {
        //开始加载
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // 在这里显示自定义错误页
            noticeWeb.setVisibility(View.VISIBLE);
            relayout_html_error.setVisibility(View.GONE);
        }


        //加载结束
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            noticeWeb.loadUrl("javascript:setImgUrl ('" + sessionID + "','" + imageUrl + "')");
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        // 旧版本，会在新版本中也可能被调用，所以加上一个判断，防止重复显示
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            LogShow("onReceivedError夹杂网页出错1");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                return;
            }
            // 在这里显示自定义错误页
            noticeWeb.setVisibility(View.GONE);
            relayout_html_error.setVisibility(View.VISIBLE);

        }

        // 新版本，只会在Android6及以上调用
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            LogShow("onReceivedError夹杂网页出错2");
            // 在这里显示自定义错误页
            if (request.isForMainFrame()) {
                noticeWeb.setVisibility(View.GONE);
                relayout_html_error.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void JsLocality(String msg) {
        Bundle mBundle = new Bundle();
        mBundle.putCharSequence(HTTITLE, msg);
        mBundle.putCharSequence(HTURL, HttpUrlAddress.PUBLICHEADER + HttpUrlAddress.NOTICEDETAILSURL);
        intentActivity(getContext(), HjAboutInformation.class, mBundle);
    }
}

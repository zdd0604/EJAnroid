package com.hjnerp.activity.html;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerpandroid.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * zdd
 * 加载网址的类
 */
public class HjAboutInformation extends ActionBarWidgetActivity implements View.OnClickListener {
    private String title;
    private String webUrl;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_right_tv1)
    TextView actionRightTv1;
    @BindView(R.id.wv_abouthj)
    WebView webView;
    @BindView(R.id.web_error)
    ImageView web_error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hj_about_informatio);
        ButterKnife.bind(this);

        initView();
    }


    private void initView() {
        Bundle bundle = this.getIntent().getExtras();
        title = bundle.getString(HTTITLE);
        webUrl = bundle.getString(HTURL) + title;
        LogShow("公告详情："+webUrl);

        actionRightTv.setVisibility(View.GONE);
        actionLeftTv.setOnClickListener(this);
        actionCenterTv.setText(title);

        waitDialogRectangle.show();
        waitDialogRectangle.setText("正在加载...");

        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();

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

        webView.setWebViewClient(new WebViewClient() { // 点击超链之后在本页面打开
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                waitDialogRectangle.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                waitDialogRectangle.dismiss();
                webView.setVisibility(View.GONE);
                web_error.setVisibility(View.VISIBLE);
                web_error.setImageResource(R.drawable.meiyouwangluo);
            }

        });
        webView.loadUrl(webUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_left_tv:
                if (!webView.canGoBack()) {
                    finish();
                }
                webView.goBack();
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果点击的是后退键  首先判断webView是否能够后退
        //如果点击的是后退键  首先判断webView是否能够后退   返回值是boolean类型的
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.hjnerp.activity.html;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    @BindView(R.id.relayout_html_error)
    RelativeLayout relayout_html_error;
    @BindView(R.id.progressBar_html)
    ProgressBar progressBar_html;

    private String title;
    private String webUrl;
    private boolean isLoad = false;


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
        relayout_html_error.setOnClickListener(this);


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


        if (!hasInternetConnected())
        {
            // 在这里显示自定义错误页
            webView.setVisibility(View.GONE);
            relayout_html_error.setVisibility(View.VISIBLE);
        }
        else
        {
            setHtml();
        }

    }

    private void setHtml()
    {
        webView.loadUrl(webUrl);


        webView.setWebViewClient(new WebViewClient() { // 点击超链之后在本页面打开


            //开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 在这里显示自定义错误页
                webView.setVisibility(View.VISIBLE);
                relayout_html_error.setVisibility(View.GONE);
                LogShow("onPageStarted:"+url);
            }


            //加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogShow("onPageFinished:"+url);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //加载成功了
                LogShow("shouldOverrideUrlLoading:"+url);
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
                webView.setVisibility(View.GONE);
                relayout_html_error.setVisibility(View.VISIBLE);
                LogShow("onReceivedError:"+failingUrl);

            }

            // 新版本，只会在Android6及以上调用
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LogShow("onReceivedError夹杂网页出错2");
                LogShow("错误代码："+error.getErrorCode());
                // 在这里显示自定义错误页
                if (request.isForMainFrame()){
                    webView.setVisibility(View.GONE);
                    relayout_html_error.setVisibility(View.VISIBLE);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                LogShow("onProgressChanged");
                if(newProgress==100)
                {
                    progressBar_html.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else
                {
                    progressBar_html.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar_html.setProgress(newProgress);//设置进度值
                }
            }
        });
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
            case R.id.relayout_html_error:
                isLoad = false;
                 initView();
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

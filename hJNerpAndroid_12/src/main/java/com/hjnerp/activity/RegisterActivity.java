package com.hjnerp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hjnerp.business.BusinessJsonCallBack.BRegisterCallBack;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.dao.OtherBaseDao;
import com.hjnerp.model.BaseData;
import com.hjnerp.util.DateUtil;
import com.hjnerp.util.ToastUtil;
import com.hjnerp.widget.ClearEditText;
import com.hjnerp.widget.WaitDialogRectangle;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends ActionBarWidgetActivity implements View.OnClickListener {
    Handler mHandler;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_right_tv1)
    TextView actionRightTv1;
    @BindView(R.id.ar_reg_phone)
    ClearEditText mRegPhone;
    @BindView(R.id.ar_reg_code)
    ClearEditText mRegCode;
    @BindView(R.id.ar_reg_btn)
    Button mRegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        actionCenterTv.setText(getString(R.string.register_Title_TvActivity));
        actionLeftTv.setOnClickListener(this);
        actionRightTv.setVisibility(View.GONE);
        mRegCode.addTextChangedListener(textWatcher);
        mRegPhone.addTextChangedListener(textWatcher);
        mHandler = new Handler();
        mRegBtn.setOnClickListener(new RegBtnClickListener());
        this.closeInput();
    }


    /**
     * 监听事件的输入
     */
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (getEdVaule(mRegCode).length() > 0 && getEdVaule(mRegPhone).length() > 0) {
                mRegBtn.setEnabled(true);
            } else {
                mRegBtn.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    private void showIMF() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRegPhone.requestFocus();
                closeInput();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_left_tv:
                finish();
                break;
        }
    }

    public class RegBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (validateInternet()) {
                String eRegPhone = mRegPhone.getText().toString();
                String eRegCode = mRegCode.getText().toString();
                if (TextUtils.isEmpty(eRegPhone)) {
                    mRegPhone.setShakeAnimation();
                    showFailToast("手机号不能为空");
                    showIMF();
                    return;
                }
                if (TextUtils.isEmpty(eRegCode)) {
                    mRegCode.setShakeAnimation();
                    showFailToast("注册码不能为空");
                    showIMF();
                    return;
                }
                waitDialogRectangle.show();
                waitDialogRectangle.setText("正在验证...");

                getRegisterInfo(eRegPhone, eRegCode, "mobileInit");
            } else {
                showFailToast(getResources()
                        .getString(R.string.net_connect_error));
            }

// 原先注册,删除注解即可用
//			ArrayList<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
//			parameters.add(new BasicNameValuePair("phoneId", eRegPhone));
//			parameters.add(new BasicNameValuePair("valiadId", eRegCode));
//			parameters.add(new BasicNameValuePair("actionType", "mobileInit"));
//			// HttpPost request = new
//			// HttpPost("http://183.81.182.6:8090/nerp/hjMobile");
//
////			HttpPost request = new HttpPost(
////					"http://register.hejia.cn:8090/nerp/hjMobile");
//			HttpPost request = new HttpPost("http://register.hejia.cn:8090/nerp/hjMobile");
//			// HttpPost request = new
//			// HttpPost("http://172.16.12.26:8095/nerp/hjMobile");
//			try {
//				request.setEntity(new UrlEncodedFormEntity(parameters));
//			} catch (UnsupportedEncodingException e) {
//				Log.e(null, "", e);
//			}
//			HttpClientManager.open();
//			HttpClientManager.addTask(new RegisterResponseHandler(), request);
        }
    }
//原来的数据获取
//    class RegisterResponseHandler extends HttpResponseHandler {
//        @Override
//        public void onResponse(HttpResponse resp) {
//            try {
//                // eRegPhone));
//                // parameters.add(new BasicNameValuePair("valiadId", eRegCode)
//
//                String json = HttpClientManager.toStringContent(resp);
//                android.util.Log.i("info", "注册返回:" + json);
//                Gson gson = new Gson();
//                BaseData data = gson.fromJson(json, BaseData.class);
//                waitDialogRectangle.cancel();
//                if (data.isSuccess()) {
//                    /**
//                     * 保存验证码
//                     */
//                    sputil.setRegistId(mRegPhone.getText().toString());
//                    sputil.setRegistNub(mRegCode.getText().toString());
//                    /**
//                     * 判断当前是否超过注册时间
//                     */
//
//                    List<Map<String, Object>> dataList = data.getDataList();
//                    // 获取注册截止时间
//                    String dateStopReg = dataList.get(0).get("dateStopReg")
//                            .toString();
//                    long stopReg = DateUtil.StrToDate(dateStopReg).getTime();
//                    long current = new Date().getTime();
//                    if (current < stopReg || current == stopReg) {
//                        OtherBaseDao.replaceRegInfo(dataList);
//                        showFailToast("注册成功");
//                        Intent intent = new Intent(getBaseContext(),
//                                LoginActivity.class);
//                        setResult(RESULT_OK, intent);
//                        finish();
//                    } else {
//                        showFailToast("企业注册已过截止日期，请联系软件供应公司");
//                    }
//                } else {
//                    showFailToast(data.message);
//                    showIMF();
//                }
//            } catch (IOException e) {
//                onException(e);
//            }
//        }
//
//        @Override
//        public void onException(Exception e) {
//            waitDialogRectangle.cancel();
//            showFailToast("连接远程注册服务器失败，请稍后再试...");
//            showIMF();
//        }
//    }

    /**
     * 获取数据
     */
    private void getRegisterInfo(String eRegPhone, String eRegCode, String mobileInit) {
        Log.v("show", eRegPhone + "    " + eRegCode + "  " + mobileInit);
        OkGo.post("http://register.hejia.cn:8090/nerp/hjMobile")
                .params("phoneId", eRegPhone)
                .params("valiadId", eRegCode)
                .params("actionType", mobileInit)
                .execute(new BRegisterCallBack<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData, Call call, Response response) {
                        /**
                         * 保存验证码
                         */
                        sputil.setRegistId(mRegPhone.getText().toString());
                        sputil.setRegistNub(mRegCode.getText().toString());
                        /**
                         * 判断当前是否超过注册时间
                         */

                        List<Map<String, Object>> dataList = baseData.getDataList();
                        // 获取注册截止时间
                        String dateStopReg = dataList.get(0).get("dateStopReg")
                                .toString();
                        long stopReg = DateUtil.StrToDate(dateStopReg).getTime();
                        long current = new Date().getTime();
                        if (current < stopReg || current == stopReg) {
                            OtherBaseDao.replaceRegInfo(dataList);
                            showFailToast("注册成功");
                            Intent intent = new Intent(getBaseContext(),
                                    LoginActivity.class);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            showFailToast("企业注册已过截止日期，请联系软件供应公司");
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        waitDialogRectangle.cancel();
                        showFailToast(e.getMessage());
                        showIMF();
                    }
                });
    }


    @Override
    public WaitDialogRectangle getWaitDialogRectangle() {
        // TODO Auto-generated method stub
        return waitDialogRectangle;
    }
}
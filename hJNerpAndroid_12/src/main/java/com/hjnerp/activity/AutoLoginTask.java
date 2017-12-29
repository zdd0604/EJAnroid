package com.hjnerp.activity;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.hjnerp.business.Ctlm1345Update;
import com.hjnerp.business.Ctlm1347Update;
import com.hjnerp.common.Constant;
import com.hjnerp.common.IActivitySupport;
import com.hjnerp.manager.HJWebSocketManager;
import com.hjnerp.net.ChatConstants;
import com.hjnerp.net.IQ;
import com.hjnerp.util.SharePreferenceUtil;
import com.hjnerp.util.ToastUtil;
import com.hjnerp.widget.MyToast;
import com.hjnerpandroid.R;

public class AutoLoginTask extends AsyncTask<String, Integer, Integer> {
	private IActivitySupport activitySupport;
	private Context context;
	private String errorText;
	protected SharePreferenceUtil sputil;

	public AutoLoginTask(IActivitySupport activitySupport,
			SharePreferenceUtil spUtil) {

		this.activitySupport = activitySupport;
		this.context = activitySupport.getContext();
		this.sputil = spUtil;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		return login();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(Integer result) {
		switch (result) {
		case Constant.LOGIN_SECCESS: // 登录成功
			new Ctlm1347Update(null).action();
			new Ctlm1345Update(new String[]{"ctlm1345"}, new String[]{"1=1"}, null).action();
			context.startActivity(new Intent(context, MainActivity.class));
			((Activity) activitySupport).finish();
			break;
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// 账户或者密码错误
			new MyToast(context, errorText);
			((LoginActivity) activitySupport).init();
			break;
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
			new MyToast(context, errorText);
			((LoginActivity) activitySupport).init();
			break;
		case Constant.LOGIN_ERROR:// 未知异常
			new MyToast(context, errorText);
			((LoginActivity) activitySupport).init();
			break;
		case Constant.LOGIN_ANONYMOUS: 
			context.startActivity(new Intent(context, MainActivity.class));
			((Activity) activitySupport).finish();
		}
		super.onPostExecute(result);

	}

	private Integer login() {
//		activitySupport.stopService();
		// /如果没有网络直接登录
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!activitySupport.hasInternetConnected()) {

			return Constant.LOGIN_ANONYMOUS;
		}

		errorText = "";
		errorText = HJWebSocketManager.getInstance().autoLogin();

		if (!"".equalsIgnoreCase(errorText))
			return Constant.LOGIN_ERROR_ACCOUNT_PASS;

		IQ iqworkFlow = HJWebSocketManager.getInstance().readyWorkFlow();
		if (iqworkFlow != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = (Map<String, Object>) iqworkFlow.data;
			Object obj = dataMap.get(ChatConstants.iq.DATA_KEY_CONTENT);
			if (obj == null)
			{
				obj = "N";
			}
			if ("Y".equalsIgnoreCase(obj.toString())) {
				sputil.setWorkFlow(true);
			} else {
				sputil.setWorkFlow(false);
			}
		}

		return Constant.LOGIN_SECCESS;
	}
}

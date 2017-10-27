package com.hjnerp.business.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hjnerp.business.BusinessAdapter.popupAdapter;
import com.hjnerp.business.BusinessJsonCallBack.BFlagCallBack;
import com.hjnerp.business.BusinessQueryDao.BusinessQueryDao;
import com.hjnerp.business.BusinessStringBuffer.BusinessEJBuffer;
import com.hjnerp.business.businessutils.BusinessTimeUtils;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.model.EjWType1345;
import com.hjnerp.model.EjWadd1345;
import com.hjnerp.model.businessFlag;
import com.hjnerp.util.StringUtil;
import com.hjnerp.widget.ClearEditText;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 工作日志
 */
public class BusinessDgtdrechtml extends ActionBarWidgetActivity implements View.OnClickListener {
    //界面ID
    @BindView(R.id.log_date_task)
    TextView log_date_task;
    @BindView(R.id.log_id_wproj)
    TextView log_id_wproj;
    @BindView(R.id.log_dec_wtime)
    ClearEditText log_dec_wtime;
    @BindView(R.id.log_var_wtitle)
    ClearEditText log_var_wtitle;
    @BindView(R.id.log_id_corr)
    TextView log_id_corr;
    @BindView(R.id.log_var_remark)
    ClearEditText log_var_remark;
    @BindView(R.id.log_flag_wadd)
    TextView log_flag_wadd;
    @BindView(R.id.log_id_wtype)
    TextView log_id_wtype;
    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;

    //获取数据
    private List<EjWadd1345> ejWaddList;
    private List<EjWType1345> ejtpe1345List;

    private String Log_id_wproj;
    private String date_task;
    private String flag_wadd;
    private String id_wtype;
    private String dec_wtime;
    private String var_wtitle;
    private String var_remark;
    private PopupWindow mPopupWindow;
    private List<String> idSiteList = new ArrayList<>();
    private List<String> nameSiteList = new ArrayList<>();
    private List<String> idWTypeList = new ArrayList<>();
    private List<String> nameWTypeList = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    log_id_wproj.setText(Constant.item_peoject);
                    log_id_corr.setText(Constant.item_client);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dgtdrechtml);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        actionCenterTv.setText(getString(R.string.work_Title_TvActivity));
        actionRightTv.setText(getString(R.string.action_right_content_commit));
        actionRightTv.setOnClickListener(this);
        actionLeftTv.setOnClickListener(this);
        log_flag_wadd.setOnClickListener(this);
        log_id_wtype.setOnClickListener(this);
        log_date_task.setOnClickListener(this);
        log_id_wproj.setOnClickListener(this);

        querySite();
        queryWType();
    }

    /**
     * 查询公司地点
     */
    private void querySite() {
        ejWaddList = BusinessQueryDao.getMyWadd(mContext);
        for (int i = 0; i < ejWaddList.size(); i++) {
            nameSiteList.add(ejWaddList.get(i).getName_wadd());
            idSiteList.add(ejWaddList.get(i).getFlag_wadd());
        }
    }


    /**
     * 查询工作类型
     */
    private void queryWType() {
        ejtpe1345List = BusinessQueryDao.getMyWType(mContext);
        for (int i = 0; i < ejtpe1345List.size(); i++) {
            idWTypeList.add(ejtpe1345List.get(i).getId_wtype());
            nameWTypeList.add(ejtpe1345List.get(i).getName_projcate());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_date_task:
                showCalendar(log_date_task);
                break;
            case R.id.log_id_wproj:
                queryWorkType();
                break;
            case R.id.action_left_tv:
                finish();
                break;
            case R.id.action_right_tv:
                submitData();
                break;
            case R.id.log_flag_wadd:
                showPopupWindow(log_flag_wadd, nameSiteList, "site");
                break;
            case R.id.log_id_wtype:
                showPopupWindow(log_id_wtype, nameWTypeList, "wtype");
                break;
        }
    }

    private void submitData() {
        waitDialog.show();
        Log_id_wproj = log_id_wproj.getText().toString().trim();
        date_task = log_date_task.getText().toString().trim();
        dec_wtime = log_dec_wtime.getText().toString().trim();
        var_wtitle = log_var_wtitle.getText().toString().trim();
        var_remark = log_var_remark.getText().toString().trim();

        if (!StringUtil.isStrTrue(date_task)) {
            showFailToast(getString(R.string.toast_Hint_DateTask));
            waitDialog.dismiss();
            return;
        }

        if (!StringUtil.isStrTrue(flag_wadd)) {
            showFailToast(getString(R.string.toast_Hint_flagWadd));
            waitDialog.dismiss();
            return;
        }

        if (!StringUtil.isStrTrue(id_wtype)) {
            showFailToast(getString(R.string.toast_Hint_idWtype));
            waitDialog.dismiss();
            return;
        }
        if (!StringUtil.isStrTrue(Log_id_wproj)) {
            showFailToast(getString(R.string.toast_Hint_LogWproj));
            waitDialog.dismiss();
            return;
        }
        if (!StringUtil.isStrTrue(dec_wtime)) {
            showFailToast(getString(R.string.toast_Hint_decWtime));
            waitDialog.dismiss();
            return;
        }
        if (!StringUtil.isStrTrue(var_wtitle)) {
            showFailToast(getString(R.string.toast_Hint_varWtitle));
            waitDialog.dismiss();
            return;
        }



        String data = BusinessEJBuffer.getDgtdrecBuffer(Constant.ID_MENU,
                Constant.ej1345.getId_user(), Constant.MYUSERINFO.userID,
                Constant.MYUSERINFO.companyID, date_task, Constant.MYUSERINFO.departmentID,
                flag_wadd, id_wtype, Constant.id_wproj, dec_wtime, var_wtitle,
                var_remark, Constant.item_peoject, BusinessTimeUtils.getCurrentTime(Constant.NOWTIME),
                Integer.valueOf(BusinessTimeUtils.getCurrentTime(Constant.kpiperiod_MM)),
                Integer.valueOf(BusinessTimeUtils.getCurrentTime(Constant.Int_year_YYYY)),
                Constant.id_corr);
        getBusinessList(data);
    }

    /**
     * 提交数据
     *
     * @param datas
     */
    private void getBusinessList(String datas) {
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataUpdateServlet")
                .isMultipart(true)
                .params("datas", datas)
                .cacheKey(Constant.ID_MENU)            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .execute(new BFlagCallBack<businessFlag>() {
                    @Override
                    public void onSuccess(businessFlag businessFlag, Call call, Response response) {
                        String content = businessFlag.getMessage();
                        showFailToast(getString(R.string.toast_Message_CommitSucceed));
                        removeData();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        showFailToast(getString(R.string.toast_Message_CommitFail));
                        removeData();
                    }
                });
    }

    private void queryWorkType() {
        if (StringUtil.isStrTrue(id_wtype)) {
            Constant.id_wtype = id_wtype;
            intentActivity(BusinessSearch.class);
        } else {
            showFailToast("请先选择工作类型");
            removeData();
        }
    }

    public void intentActivity(Class c) {
        Intent intent = new Intent(this, c);
        Constant.travel = false;
        startActivityForResult(intent, 11);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // //界面返回值
        /**
         * @author haijian
         * 收到返回的值判断是否成功，如果同意就将数据移除刷新列表
         */
        if (requestCode == 11 && resultCode == 22) {
            handler.sendEmptyMessage(0);
        }
    }

    private void showPopupWindow(View anchorView, List<String> content, String btnType) {
        View contentView = getPopupWindowContentView(content, btnType);
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
//         popupWindow.showAsDropDown(mButton2);  // 默认在mButton2的左下角显示
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xOffset = anchorView.getWidth() / 2 - contentView.getMeasuredWidth() / 2;
        mPopupWindow.showAsDropDown(anchorView, xOffset, 0);    // 在mButton2的中间显示
//        View contentView = getPopupWindowContentView(content, btnType);
//        mPopupWindow = new PopupWindow(contentView,
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
//        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
//        // 设置好参数之后再show
//        int windowPos[] = PopupWindowUtil.calculatePopWindowPos(anchorView, contentView);
////        int xOff = 20; // 可以自己调整偏移
//        int xOff = anchorView.getWidth() / 2 - contentView.getMeasuredWidth() / 2;
//        windowPos[0] -= xOff;
//        mPopupWindow.showAtLocation(anchorView, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

    private View getPopupWindowContentView(final List<String> content, final String btnType) {
        // 一个自定义的布局，作为显示的内容
        int layoutId = R.layout.popup_content_layout;   // 布局ID
        final View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        ListView dgtdrech_list = (ListView) contentView.findViewById(R.id.dgtdrech_list);
        popupAdapter popupAdapter = new popupAdapter(BusinessDgtdrechtml.this, content);

        dgtdrech_list.setAdapter(popupAdapter);
        dgtdrech_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (btnType.equals("site")) {
                    log_flag_wadd.setText(content.get(position));
                    flag_wadd = idSiteList.get(position);
                } else {
                    id_wtype = idWTypeList.get(position);
                    log_id_wtype.setText(content.get(position));
                }
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
            }
        });
        return contentView;
    }

    private void removeData() {
        waitDialog.dismiss();
        log_date_task.setText("");
        log_id_wproj.setText("");
        log_dec_wtime.setText("");
        log_var_wtitle.setText("");
        log_id_corr.setText("");
        log_var_remark.setText("");
    }
}

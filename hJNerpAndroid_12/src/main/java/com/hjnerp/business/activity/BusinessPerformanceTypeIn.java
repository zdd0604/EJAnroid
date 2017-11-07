package com.hjnerp.business.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hjnerp.business.BusinessAdapter.popupAdapter;
import com.hjnerp.business.BusinessJsonCallBack.BFlagCallBack;
import com.hjnerp.business.businessutils.BusinessTimeUtils;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.dao.QiXinBaseDao;
import com.hjnerp.model.BusinessFlag;
import com.hjnerp.model.BusinessPerformanceColumn;
import com.hjnerp.model.BusinessPerformanceData;
import com.hjnerp.model.BusinessPerformanceInfo;
import com.hjnerp.model.UserInfo;
import com.hjnerp.util.Log;
import com.hjnerp.util.StringUtil;
import com.hjnerp.widget.ClearEditText;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.exception.OkGoException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 绩效计划录入
 * 新建绩效
 * zdd
 */
public class BusinessPerformanceTypeIn extends ActionBarWidgetActivity implements
        View.OnClickListener {
    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_right_tv1)
    TextView actionRightTv1;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    //考核年
    @BindView(R.id.input_int_year)
    TextView input_int_year;
    //考评周期
    @BindView(R.id.name_kpiperiod_tx)
    TextView name_kpiperiod_tx;
    //单据号
    @BindView(R.id.dkpipost_no)
    TextView dkpipost_no;
    //被评人
    @BindView(R.id.input_id_clerk)
    TextView input_id_clerk;
    //添加绩效详情
    @BindView(R.id.btn_add_kpiview)
    LinearLayout btn_addKpiView;
    //添加目标详情
    @BindView(R.id.btn_add_gsview)
    LinearLayout btn_addGsView;
    //计划日期
    @BindView(R.id.input_conclude_time)
    TextView input_conclude_time;
    //驳回意见 显示
    @BindView(R.id.input_var_reject)
    TextView input_var_reject;
    //驳回意见 输入
    @BindView(R.id.input_approval_context)
    EditText input_approval_context;
    //驳回按钮
    @BindView(R.id.input_btn_disagree)
    Button input_btn_disagree;
    //保存按钮
    @BindView(R.id.input_performanc_save)
    Button input_performanc_save;
    //布局 计划日期
    @BindView(R.id.layout_conclude_time)
    LinearLayout layout_conclude_time;
    //布局 考评周期
    @BindView(R.id.layout_name_kpiperiod)
    LinearLayout layout_name_kpiperiod;
    //布局 总分
    @BindView(R.id.layout_dec_smark)
    LinearLayout layout_dec_smark;
    //布局 驳回意见显示框
    @BindView(R.id.layout_var_reject)
    LinearLayout layout_var_reject;
    //布局 绩效详情
    @BindView(R.id.kpiview)
    LinearLayout lay_kpiView;
    //布局 工作目标
    @BindView(R.id.gsview)
    LinearLayout lay_gsView;
    //布局 驳回输入框
    @BindView(R.id.layout_reject)
    LinearLayout layout_reject;
    //布局 被评人
    @BindView(R.id.layout_id_clerk)
    LinearLayout layout_id_clerk;
    //布局 考核年
    @BindView(R.id.layout_int_year)
    LinearLayout layout_int_year;

    private LayoutInflater inflater;
    /**
     * 行号
     */
    private int line_numb = 0;
    /**
     * GS分数的集合
     */
    private List<Integer> list_WdigetGS = new ArrayList<>();
    /**
     * KPI分数的集合
     */
    private List<String> list_Kpicate = new ArrayList<>();
    /**
     * 指标分类
     */
    private List<Integer> list_WdigetKPI = new ArrayList<>();
    //权重的综合
    private Double widget_sum = 0.0;
    private Double widgetkpi_sum = 0.0;
    private UserInfo myInfo;
    private String companyID;
    private String userID;

    private List<BusinessPerformanceInfo> infoList = new ArrayList<>();
    private HashSet infoHashSet = new HashSet();
    private List<BusinessPerformanceInfo> oldList = new ArrayList<>();
    private HashSet<Integer> oldLine_no = new HashSet<>();
    private HashSet<Integer> newLine_no = new HashSet<>();
    private List<Integer> nLine_no = new ArrayList<>();
    private List<Integer> uLine_no = new ArrayList<>();
    private List<Integer> dLine_no = new ArrayList<>();

    private static String cgrgound;
    private static String idclerk;
    private static String idkpiperiod;
    private static String idsupclerk;
    private static String dataplan;
    private static String idkpicate;
    private static int intyear;
    //考评周期
    private String Id_kpiperiod;

    List<View> kpiListView = new ArrayList<>();
    List<View> gsListView = new ArrayList<>();
    private int kpiindexofView = 0;
    private int gsindexofView = 0;
    private List<Double> kpi_Widget = new ArrayList<>();
    private List<Double> gs_Widget = new ArrayList<>();
    private String conclude_time_et;

    private static int SUCCEED_SAVE = 0;//保存成功
    private static int SUCCEED_SEND = 1;//上传成功
    private static int SUCCEED_ERROR = 2;//上传失败
    private static int SUCCEED_THREAD = 3;//延迟销毁
    private Thread mThread;
    //确认按钮的状态
    private Boolean submit_type = false;
    //判断类型
    private final int KPIType = 0;
    private final int GSTYPE = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            switch (msg.what) {
                case 0:
                    showFailToast(message);
                    submit_type = true;
                    dkpipost_no.setText(Constant.billsNo);
                    //要在提交完成后删除
                    removeData();
                    break;
                case 1:
                    showFailToast(message);
                    submit_type = false;
                    //要在提交完成后删除
                    waitThread();
                    break;
                case 2:
                    //上传错误
                    showFailToast(message);
                    waitDialog.dismiss();
                    break;
                case 3:
                    //要在提交完成后删除
                    removeData();
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_performance_input);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        actionCenterTv.setText(getString(R.string.perf_Title_TvActivity));
        actionRightTv.setText(getString(R.string.action_right_content_commit));
        actionRightTv1.setText(getString(R.string.action_right_content_save));
        actionLeftTv.setOnClickListener(this);
        actionRightTv1.setVisibility(View.VISIBLE);
        actionRightTv1.setOnClickListener(this);
        actionRightTv.setOnClickListener(this);
        input_conclude_time.setOnClickListener(this);
        btn_addKpiView.setOnClickListener(this);
        btn_addGsView.setOnClickListener(this);
        name_kpiperiod_tx.setOnClickListener(this);
        inflater = LayoutInflater.from(this);

        layout_id_clerk.setVisibility(View.VISIBLE);
        layout_name_kpiperiod.setVisibility(View.VISIBLE);
        layout_dec_smark.setVisibility(View.GONE);
        layout_reject.setVisibility(View.GONE);
        myInfo = QiXinBaseDao.queryCurrentUserInfo();
        if (myInfo != null) {
            userID = myInfo.userID;
            companyID = myInfo.companyID;
        }
        input_id_clerk.setText(myInfo.username);
        input_int_year.setText(BusinessTimeUtils.getCurrentTime(Constant.TIME_YYYY));
        input_conclude_time.setText(BusinessTimeUtils.getCurrentTime(Constant.TIME_yyyy_MM_dd));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //考评周期
            case R.id.name_kpiperiod_tx:
                showPopupWindow(this, name_kpiperiod_tx, montStList);
                break;
            case R.id.input_conclude_time:
                showCalendar(input_conclude_time);
                break;
            case R.id.btn_add_kpiview:
                addLayoutView(KPIType);
                break;
            case R.id.btn_add_gsview:
                addLayoutView(GSTYPE);
                break;
            case R.id.action_right_tv:
                if (submit_type) {
                    submitDatas("S", "send", "1001");
                } else {
                    showFailToast("数据必须先保存才能送审!");
                    removeData();
                    return;
                }
                break;
            case R.id.action_right_tv1:
                submitDatas("L", "save", "1002");
                break;
            case R.id.action_left_tv:
                finish();
                break;

        }
    }

    private void showPopupWindow(Context context, View anchorView, List<String> content) {
        View contentView = getPopupWindowContentView(context, content);
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xOffset = anchorView.getWidth() / 2 - contentView.getMeasuredWidth() / 2;
        popupWindow.showAsDropDown(anchorView, xOffset, 0);    // 在mButton2的中间显示
    }

    private View getPopupWindowContentView(Context context, final List<String> content) {
        // 一个自定义的布局，作为显示的内容
        int layoutId = R.layout.popup_content_layout;   // 布局ID
        final View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        ListView dgtdrech_list = (ListView) contentView.findViewById(R.id.dgtdrech_list);
        popupAdapter popupAdapter = new popupAdapter(context, content);

        dgtdrech_list.setAdapter(popupAdapter);
        dgtdrech_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                name_kpiperiod_tx.setText(content.get(position));
                Id_kpiperiod = monthIntList.get(position);
                LogShow(Id_kpiperiod);
                if (popupWindow != null)
                    popupWindow.dismiss();
            }
        });
        return contentView;
    }

    /**
     * 添加View
     */
    private void addLayoutView(final int viewType) {
        line_numb++;
        final View view = inflater.inflate(R.layout.performance_item, null);
        TextView prfItemLineTitle = (TextView) view.findViewById(R.id.prfItemLineTitle);
        TextView line_no = (TextView) view.findViewById(R.id.input_line_id);
        line_no.setText(String.valueOf(line_numb));
        TextView line_delete = (TextView) view.findViewById(R.id.input_line_delete);
        line_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewType) {
                    case KPIType:
                        deletelLayoutView(view, KPIType);
                        break;
                    case GSTYPE:
                        deletelLayoutView(view, GSTYPE);
                        break;
                }
            }
        });

        switch (viewType) {
            case KPIType:
                prfItemLineTitle.setText(getString(R.string.pfItem_Title_perfKPI));
                lay_kpiView.addView(view);
                kpiListView.add(view);
                break;
            case GSTYPE:
                prfItemLineTitle.setText(getString(R.string.pfItem_Title_perfGS));
                lay_gsView.addView(view);
                gsListView.add(view);
                break;
        }
    }

    /**
     * 删除View
     *
     * @param view
     */
    private void deletelLayoutView(final View view, final int viewType) {
        final Dialog noticeDialog = new Dialog(this, R.style.noticeDialogStyle);
        noticeDialog.setContentView(R.layout.dialog_notice_withcancel);
        TextView dialog_cancel_rl, dialog_confirm_rl;
        TextView notice = (TextView) noticeDialog
                .findViewById(R.id.dialog_notice_tv);
        notice.setText("是否要删除该绩效明细");
        dialog_cancel_rl = (TextView) noticeDialog.findViewById(R.id.dialog_cancel_tv);
        TextView dialog_cancel_tv = (TextView) noticeDialog.findViewById(R.id.dialog_cancel_tv);
        dialog_cancel_tv.setText("取消");
        dialog_confirm_rl = (TextView) noticeDialog.findViewById(R.id.dialog_confirm_tv);
        TextView dialog_confirm_tv = (TextView) noticeDialog.findViewById(R.id.dialog_confirm_tv);
        dialog_confirm_tv.setText("删除");
        dialog_cancel_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                noticeDialog.dismiss();
            }
        });
        dialog_confirm_rl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                noticeDialog.dismiss();
                switch (viewType) {
                    case KPIType:
                        //计算
                        kpiindexofView = kpiListView.indexOf(view);
                        lay_kpiView.removeView(view);
                        kpiListView.remove(view);
                        LogShow("计算后的下标：" + kpiindexofView + ",,,,," + kpiListView.size());
                        if (kpi_Widget.size() >= kpiindexofView && kpi_Widget.size() > 0) {
                            kpi_Widget.remove(kpiindexofView);
                        }
                        break;
                    case GSTYPE:
                        //计算
                        gsindexofView = gsListView.indexOf(view);
                        lay_gsView.removeView(view);
                        gsListView.remove(view);
                        LogShow("计算后的下标：" + gsindexofView + ",,,,," + kpiListView.size());
                        if (gs_Widget.size() >= gsindexofView && gs_Widget.size() > 0) {
                            gs_Widget.remove(gsindexofView);
                        }
                        break;
                }
            }
        });
        noticeDialog.show();
    }


    /**
     * 提交数据
     *
     * @param flag_sts
     * @param dealtype
     * @param buttonid
     */
    private void submitDatas(String flag_sts, String dealtype, String buttonid) {
        removeData();
        waitDialog.show();
        if (kpiListView.size() == 0 && gsListView.size() == 0) {
            showFailToast("至少添加一条详情记录");
            waitDialog.dismiss();
            return;
        }
        conclude_time_et = input_conclude_time.getText().toString();
        if (!StringUtil.isStrTrue(conclude_time_et)) {
            showFailToast("计划签订日期不能为空");
            waitDialog.dismiss();
            return;
        }
        BusinessPerformanceInfo businessInfo = new BusinessPerformanceInfo();
        businessInfo.setTableid("dkpipost");
        businessInfo.setUserid(userID);
        businessInfo.setComid(companyID);
        businessInfo.setNo("");

        //存放绩效详情的数据
        List<BusinessPerformanceColumn> columns = new ArrayList<>();
        for (int i = 0; i < kpiListView.size(); i++) {
            View view = kpiListView.get(i);
            TextView line_no = (TextView) view.findViewById(R.id.input_line_id);
            TextView line_delete = (TextView) view.findViewById(R.id.input_line_delete);
            ClearEditText input_id_kpipost = (ClearEditText) view.findViewById(R.id.input_id_kpipost);
            ClearEditText input_var_evastd = (ClearEditText) view.findViewById(R.id.input_var_evastd);
            EditText input_dec_scale = (EditText) view.findViewById(R.id.input_dec_scale);

            if (!StringUtil.isStrTrue(input_id_kpipost.getText().toString())) {
                showFailToast("关键绩效 " + line_no.getText().toString().trim() + " 指标不能为空");
                waitDialog.dismiss();
                removeData();
                return;
            }
            if (!StringUtil.isStrTrue(input_var_evastd.getText().toString())) {
                showFailToast("关键绩效 " + line_no.getText().toString().trim() + " 评价标准不能为空");
                waitDialog.dismiss();
                removeData();
                return;
            }
            if (!StringUtil.isStrTrue(input_dec_scale.getText().toString())) {
                showFailToast("关键绩效 " + line_no.getText().toString().trim() + " 权重不能为空");
                waitDialog.dismiss();
                removeData();
                return;
            }

            List<BusinessPerformanceData> datas = new ArrayList<>();
            BusinessPerformanceData businessPerformanceData = new BusinessPerformanceData();
            businessPerformanceData.setTable("dkpipost_03");
            BusinessPerformanceColumn businessPerformanceColumn = new BusinessPerformanceColumn();
            businessPerformanceColumn.setFlag_sts(flag_sts);
            businessPerformanceColumn.setFlag_psts("");
            businessPerformanceColumn.setDate_audit("1900-01-01 00:00:00");
            businessPerformanceColumn.setDate_eva(BusinessTimeUtils.getCurrentTime(Constant.NOWTIME));
//            String time = conclude_time_et + BusinessTimeUtils.getCurrentTime(Constant.NOWTIME2);
            businessPerformanceColumn.setDate_plan(conclude_time_et);
            Log.v("show", "时间：" + conclude_time_et);
//            businessPerformanceColumn.setDate_plan(mainBean.getDate_plan());
            businessPerformanceColumn.setDec_cmark(0.00);
            businessPerformanceColumn.setDec_dmark(0.00);
            businessPerformanceColumn.setDec_emark(0.00);
            businessPerformanceColumn.setDec_smark(0.00);
            businessPerformanceColumn.setDec_import(0.0000000);
            businessPerformanceColumn.setDec_stdmark(0.0000000);
            businessPerformanceColumn.setDec_mark(0.0);
            Double scale = Double.valueOf(input_dec_scale.getText().toString()) / 100;
            businessPerformanceColumn.setDec_scale(scale);
            kpi_Widget.add(scale);
            businessPerformanceColumn.setId_audit("");
            businessPerformanceColumn.setId_flow("0601");
            businessPerformanceColumn.setId_bflow("");
            businessPerformanceColumn.setId_cgroup(Constant.ej1345.getId_cgroup());
            businessPerformanceColumn.setId_clerk(Constant.ej1345.getId_clerk());
            businessPerformanceColumn.setId_dept(Constant.ej1345.getId_dept());
            businessPerformanceColumn.setId_evapsn("");
            businessPerformanceColumn.setId_kpicate("001");
            businessPerformanceColumn.setId_kpiperiod(Id_kpiperiod);
            businessPerformanceColumn.setId_kpiphase("");
            businessPerformanceColumn.setId_kpigrade("");
            businessPerformanceColumn.setId_kpipost(input_id_kpipost.getText().toString());
            businessPerformanceColumn.setId_kpitype("001");
            businessPerformanceColumn.setId_post(Constant.ej1345.getId_post());
            businessPerformanceColumn.setId_supclerk(Constant.ej1345.getId_supclerk());
            businessPerformanceColumn.setId_table("");
            businessPerformanceColumn.setInt_year(Integer.valueOf(getTvVaule(input_int_year)));
            businessPerformanceColumn.setLine_no(Integer.valueOf(line_no.getText().toString()));
            businessPerformanceColumn.setOri_line_no(0);
            businessPerformanceColumn.setVar_comp("");
            businessPerformanceColumn.setVar_dremark("");
            businessPerformanceColumn.setVar_evastd(input_var_evastd.getText().toString());
            businessPerformanceColumn.setOri_no("");
            businessPerformanceColumn.setVar_evares("");
            businessPerformanceColumn.setVar_rejust("");
            businessPerformanceColumn.setVar_remark("");
            newLine_no.add(Integer.valueOf(line_no.getText().toString()));
            columns.add(businessPerformanceColumn);
            businessPerformanceData.setData(columns);
            datas.add(businessPerformanceData);
            businessInfo.setData(datas);
        }

        for (int i = 0; i < gsListView.size(); i++) {
            View view = gsListView.get(i);
            TextView line_no = (TextView) view.findViewById(R.id.input_line_id);
            TextView line_delete = (TextView) view.findViewById(R.id.input_line_delete);
            ClearEditText input_id_kpipost = (ClearEditText) view.findViewById(R.id.input_id_kpipost);
            ClearEditText input_var_evastd = (ClearEditText) view.findViewById(R.id.input_var_evastd);
            EditText input_dec_scale = (EditText) view.findViewById(R.id.input_dec_scale);

            if (!StringUtil.isStrTrue(input_id_kpipost.getText().toString())) {
                showFailToast("工作目标 " + line_no.getText().toString().trim() + " 指标不能为空");
                waitDialog.dismiss();
                removeData();
                return;
            }
            if (!StringUtil.isStrTrue(input_var_evastd.getText().toString())) {
                showFailToast("工作目标 " + line_no.getText().toString().trim() + " 评价标准不能为空");
                waitDialog.dismiss();
                removeData();
                return;
            }
            if (!StringUtil.isStrTrue(input_dec_scale.getText().toString())) {
                showFailToast("工作目标 " + line_no.getText().toString().trim() + " 权重不能为空");
                waitDialog.dismiss();
                removeData();
                return;
            }

            List<BusinessPerformanceData> datas = new ArrayList<>();
            BusinessPerformanceData businessPerformanceData = new BusinessPerformanceData();
            businessPerformanceData.setTable("dkpipost_03");
            BusinessPerformanceColumn businessPerformanceColumn = new BusinessPerformanceColumn();
            businessPerformanceColumn.setFlag_sts(flag_sts);
            businessPerformanceColumn.setFlag_psts("");
            businessPerformanceColumn.setDate_audit("1900-01-01 00:00:00");
            businessPerformanceColumn.setDate_eva(BusinessTimeUtils.getCurrentTime(Constant.NOWTIME));
//            String time = conclude_time_et + BusinessTimeUtils.getCurrentTime(Constant.NOWTIME2);
            businessPerformanceColumn.setDate_plan(conclude_time_et);
            businessPerformanceColumn.setDec_cmark(0.00);
            businessPerformanceColumn.setDec_dmark(0.00);
            businessPerformanceColumn.setDec_emark(0.00);
            businessPerformanceColumn.setDec_smark(0.00);
            businessPerformanceColumn.setDec_import(0.0000000);
            businessPerformanceColumn.setDec_stdmark(0.0000000);
            businessPerformanceColumn.setDec_mark(0.0);
            Double scale = Double.valueOf(input_dec_scale.getText().toString()) / 100;
            businessPerformanceColumn.setDec_scale(scale);
            gs_Widget.add(scale);
            businessPerformanceColumn.setId_audit("");
            businessPerformanceColumn.setId_flow("0601");
            businessPerformanceColumn.setId_bflow("");
            businessPerformanceColumn.setId_cgroup(Constant.ej1345.getId_cgroup());
            businessPerformanceColumn.setId_clerk(Constant.ej1345.getId_clerk());
            businessPerformanceColumn.setId_dept(Constant.ej1345.getId_dept());
            businessPerformanceColumn.setId_evapsn("");
            businessPerformanceColumn.setId_kpicate("002");
            businessPerformanceColumn.setId_kpiperiod(Id_kpiperiod);
            businessPerformanceColumn.setId_kpiphase("");
            businessPerformanceColumn.setId_kpigrade("");
            businessPerformanceColumn.setId_kpipost(input_id_kpipost.getText().toString());
            businessPerformanceColumn.setId_kpitype("001");
            businessPerformanceColumn.setId_post(Constant.ej1345.getId_post());
            businessPerformanceColumn.setId_supclerk(Constant.ej1345.getId_supclerk());
            businessPerformanceColumn.setId_table("");
            businessPerformanceColumn.setInt_year(Integer.valueOf(getTvVaule(input_int_year)));
            businessPerformanceColumn.setLine_no(Integer.valueOf(line_no.getText().toString()));
            businessPerformanceColumn.setOri_line_no(0);
            businessPerformanceColumn.setVar_comp("");
            businessPerformanceColumn.setVar_dremark("");
            businessPerformanceColumn.setVar_evastd(input_var_evastd.getText().toString());
            businessPerformanceColumn.setOri_no("");
            businessPerformanceColumn.setVar_evares("");
            businessPerformanceColumn.setVar_rejust("");
            businessPerformanceColumn.setVar_remark("");
            newLine_no.add(Integer.valueOf(line_no.getText().toString()));
            columns.add(businessPerformanceColumn);
            businessPerformanceData.setData(columns);
            datas.add(businessPerformanceData);
            businessInfo.setData(datas);
        }
        infoList.add(businessInfo);
        computeSum(businessInfo, dealtype, buttonid);
    }

    /**
     * 计算工作目标及关键绩效指标的各项总和
     */
    private void computeSum(BusinessPerformanceInfo bInfo, String dealtype, String buttonid) {
        if (kpi_Widget.size() > 0) {
            for (Double number : kpi_Widget) {
                widgetkpi_sum += number;
            }
            Log.v("show", "list_WdigetKPI:" + kpi_Widget.toString() + "和是：" + widgetkpi_sum);
            if (widgetkpi_sum != 1) {
                waitDialog.dismiss();
                showFailToast("关键绩效指标(KPI)的权重不等于100%");
                removeData();
                if (infoList.size() > 0) {
                    infoList.remove(infoList.size() - 1);
                }
                if (kpi_Widget.size() > 0) {
                    kpi_Widget.remove(kpi_Widget.size() - 1);
                }
                return;
            }
        } else {
            waitDialog.dismiss();
            Log.v("show", "list_WdigetKPI什么数据都没有");
        }

        if (gs_Widget.size() > 0) {
            for (Double number : gs_Widget) {
                widget_sum += number;
            }

            Log.v("show", "list_WdigetGS:" + gs_Widget.toString() + "和是：" + widget_sum);
            if (widget_sum != 1) {
                waitDialog.dismiss();
                showFailToast("工作目标(GS)的权重不等于100%");
                removeData();
                if (infoList.size() > 0) {
                    infoList.remove(infoList.size() - 1);
                }
                if (gs_Widget.size() > 0) {
                    gs_Widget.remove(gs_Widget.size() - 1);
                }
                return;
            }
        } else {
            waitDialog.dismiss();
            Log.v("show", "list_WdigetGS什么数据都没有");
        }

        judgeDatas(bInfo, dealtype, buttonid);
    }

    /**
     * 判断之前保存的一条与现在新的区别
     */
    private void judgeDatas(BusinessPerformanceInfo bInfo, String dealtype, String buttonid) {
        if (infoList.size() > 1) {
            oldList.add(infoList.get(infoList.size() - 2));
            Log.v("show", "内容1:" + oldList.toString());
            for (int i = 0; i < oldList.size(); i++) {
                BusinessPerformanceInfo oldinfo = oldList.get(i);
                List<BusinessPerformanceData> olddata = oldinfo.getData();
                for (int a = 0; a < olddata.size(); a++) {
                    List<BusinessPerformanceColumn> oldcolumn = olddata.get(a).getData();
                    for (int b = 0; b < oldcolumn.size(); b++) {
                        infoHashSet.add(oldcolumn.get(b).getLine_no());
                        oldLine_no.add(oldcolumn.get(b).getLine_no());
                    }
                }
            }

            Iterator<Integer> iterator = infoHashSet.iterator();
            while (iterator.hasNext()) {
                int content = iterator.next();
                if (oldLine_no.contains(content)) {
                    if (newLine_no.contains(content)) {
                        uLine_no.add(content);
                        Log.v("show", "修改的内容:" + oldLine_no.toString() + ",,," + content + ",,," + newLine_no.toString());
                    } else {
                        dLine_no.add(content);
                        Log.v("show", "1删除的内容:" + oldLine_no.toString() + ",,," + content + ",," + newLine_no.toString());
                    }
                } else {
                    if (newLine_no.contains(content)) {
                        nLine_no.add(content);
                        Log.v("show", "新增的内容:" + oldLine_no.toString() + ",,," + content + ",," + newLine_no.toString());
                    }
                }
                Log.v("show", "内容3:" + oldLine_no.toString() + ",,," + content + ",,," + newLine_no.toString());
            }
        } else if (oldList.size() > 0) {

            oldList.add(infoList.get(infoList.size() - 1));
            Log.v("show", "内容1:" + oldList.toString());
            for (int i = 0; i < oldList.size(); i++) {
                BusinessPerformanceInfo oldinfo = oldList.get(i);
                List<BusinessPerformanceData> olddata = oldinfo.getData();
                for (int a = 0; a < olddata.size(); a++) {
                    List<BusinessPerformanceColumn> oldcolumn = olddata.get(a).getData();
                    for (int b = 0; b < oldcolumn.size(); b++) {
                        infoHashSet.add(oldcolumn.get(b).getLine_no());
                        oldLine_no.add(oldcolumn.get(b).getLine_no());
                    }
                }
            }

            Iterator<Integer> iterator = infoHashSet.iterator();
            while (iterator.hasNext()) {
                int content = iterator.next();
                if (oldLine_no.contains(content)) {
                    if (newLine_no.contains(content)) {
                        uLine_no.add(content);
                        Log.v("show", "修改的内容:" + oldLine_no.toString() + ",,," + content + ",,," + newLine_no.toString());
                    } else {
                        dLine_no.add(content);
                        Log.v("show", "删除的内容:" + oldLine_no.toString() + ",,," + content + ",," + newLine_no.toString());
                    }
                } else {
                    if (newLine_no.contains(content)) {
                        nLine_no.add(content);
                        Log.v("show", "新增的内容:" + oldLine_no.toString() + ",,," + content + ",," + newLine_no.toString());
                    }
                }
                Log.v("show", "内容3:" + oldLine_no.toString() + ",,," + content + ",,," + newLine_no.toString());
            }
        }
        getDataString(bInfo, dealtype, buttonid);
    }

    /**
     * 拼接数据
     */
    private void getDataString(BusinessPerformanceInfo binfo, String dealtype, String buttonid) {
        StringBuffer stringBuffer = new StringBuffer();
        if (StringUtil.isStrTrue(Constant.billsNo)) {
            stringBuffer.append("{\"tableid\":\"dkpipost\",\"opr\":\"SS\"," +
                    "\"no\":\"" + Constant.billsNo + "\"," +
                    "\"userid\":\"" + userID + "\"," +
                    "\"comid\":\"" + companyID + "\"," +
                    "\"menuid\":\"" + Constant.ID_MENU + "\"," +
                    "\"dealtype\":\"" + dealtype + "\", " +
                    "\"buttonid\":\"" + buttonid + "\"," +
                    "\"data\":[");
        } else {
            stringBuffer.append("{\"tableid\":\"dkpipost\",\"opr\":\"SS\"," +
                    "\"no\":\" \"," +
                    "\"userid\":\"" + userID + "\"," +
                    "\"comid\":\"" + companyID + "\"," +
                    "\"menuid\":\"" + Constant.ID_MENU + "\"," +
                    "\"dealtype\":\"" + dealtype + "\", " +
                    "\"buttonid\":\"" + buttonid + "\"," +
                    "\"data\":[");
        }
        List<BusinessPerformanceData> bdataList = binfo.getData();
        for (int i = 0; i < bdataList.size(); i++) {
            BusinessPerformanceData bdata = bdataList.get(i);
            List<BusinessPerformanceColumn> bcolumnList = bdata.getData();
            for (int a = 0; a < bcolumnList.size(); a++) {
                BusinessPerformanceColumn bcolumn = bcolumnList.get(a);
                if (uLine_no.contains(bcolumn.getLine_no())) {
                    stringBuffer.append("{\"table\":\"dkpipost_03\"," +
                            "\"where\":\" \"," +
                            "\"oprdetail\":\"" + "U" + "\"," +
                            "\"data\":[");
                } else if (nLine_no.contains(bcolumn.getLine_no())) {
                    stringBuffer.append("{\"table\": \"dkpipost_03\"," +
                            "\"where\":\" \"," +
                            "\"oprdetail\":\"" + "N" + "\"," +
                            "\"data\":[");
                } else {
                    stringBuffer.append("{\"table\": \"dkpipost_03\"," +
                            "\"where\":\" \"," +
                            "\"oprdetail\":\"" + "N" + "\"," +
                            "\"data\":[");
                }
                stringBuffer.append("{\"column\":\"flag_sts\",\"value\":\"" + bcolumn.getFlag_sts() + "\",\"datatype\":\"varchar\"},");
                stringBuffer.append("{\"column\":\"id_flow\",\"value\":\"" + bcolumn.getId_flow() + "\",\"datatype\":\"varchar\"},");
                //审核时间
                stringBuffer.append("{\"column\":\"date_audit\",\"value\":\"" + bcolumn.getDate_audit() + "\",\"datatype\":\"datetime\"},");
                //评价日期
                stringBuffer.append("{\"column\":\"date_eva\",\"value\":\"" + bcolumn.getDate_eva() + "\",\"datatype\":\"datetime\"},");
                //计划签订日期
                stringBuffer.append("{\"column\":\"date_plan\",\"value\":\"" + bcolumn.getDate_plan() + "\",\"datatype\":\"datetime\"},");
                dataplan = bcolumn.getDate_plan();
                //综合得分
                stringBuffer.append("{\"column\":\"dec_cmark\",\"value\":\"" + bcolumn.getDec_cmark() + "\",\"datatype\":\"decimal\"},");
                //扣分
                stringBuffer.append("{\"column\":\"dec_dmark\",\"value\":\"" + bcolumn.getDec_dmark() + "\",\"datatype\":\"decimal\"},");
                //个人得分
                stringBuffer.append("{\"column\":\"dec_emark\",\"value\":\"" + bcolumn.getDec_emark() + "\",\"datatype\":\"decimal\"},");
                //个人总分
                stringBuffer.append("{\"column\":\"dec_smark\",\"value\":\"" + bcolumn.getDec_smark() + "\",\"datatype\":\"decimal\"},");
                //已导入值
                stringBuffer.append("{\"column\":\"dec_import\",\"value\":\"" + bcolumn.getDec_import() + "\",\"datatype\":\"decimal\"},");
                //标准分值
                stringBuffer.append("{\"column\":\"dec_stdmark\",\"value\":\"" + bcolumn.getDec_stdmark() + "\",\"datatype\":\"decimal\"},");
                //得分
                stringBuffer.append("{\"column\":\"dec_mark\",\"value\":\"" + bcolumn.getDec_mark() + "\",\"datatype\":\"int\"},");
                //权重
                stringBuffer.append("{\"column\":\"dec_scale\",\"value\":\"" + bcolumn.getDec_scale() + "\",\"datatype\":\"decimal\"},");
                //中止标志
                stringBuffer.append("{\"column\":\"flag_psts\",\"value\":\"" + bcolumn.getFlag_psts() + "\",\"datatype\":\"char\"},");
                //审核人
                stringBuffer.append("{\"column\":\"id_audit\",\"value\":\"" + bcolumn.getId_audit() + "\",\"datatype\":\"varchar\"},");
                //前流程
                stringBuffer.append("{\"column\":\"id_bflow\",\"value\":\"" + bcolumn.getId_bflow() + "\",\"datatype\":\"varchar\"},");
                //绩效组  获取新的字段
                stringBuffer.append("{\"column\":\"id_cgroup\",\"value\":\"" + bcolumn.getId_cgroup() + "\",\"datatype\":\"varchar\"},");
                cgrgound = bcolumn.getId_cgroup();
                //被评人
                stringBuffer.append("{\"column\":\"id_clerk\",\"value\":\"" + bcolumn.getId_clerk() + "\",\"datatype\":\"varchar\"},");
                idclerk = bcolumn.getId_clerk();
                //部门
                stringBuffer.append("{\"column\":\"id_dept\",\"value\":\"" + bcolumn.getId_dept() + "\",\"datatype\":\"varchar\"},");
                //评价人
                stringBuffer.append("{\"column\":\"id_evapsn\",\"value\":\"" + bcolumn.getId_evapsn() + "\",\"datatype\":\"varchar\"},");
                //指标分类
                stringBuffer.append("{\"column\":\"id_kpicate\",\"value\":\"" + bcolumn.getId_kpicate() + "\",\"datatype\":\"varchar\"},");
                idkpicate = bcolumn.getId_kpicate();
                //评价等级
                stringBuffer.append("{\"column\":\"id_kpigrade\",\"value\":\"" + bcolumn.getId_kpigrade() + "\",\"datatype\":\"varchar\"},");
                //评价周期
                stringBuffer.append("{\"column\":\"id_kpiperiod\",\"value\":\"" + bcolumn.getId_kpiperiod() + "\",\"datatype\":\"varchar\"},");
                idkpiperiod = bcolumn.getId_kpiperiod();
                //考评阶段
                stringBuffer.append("{\"column\":\"id_kpiphase\",\"value\":\"" + bcolumn.getId_kpiphase() + "\",\"datatype\":\"varchar\"},");
                //指标
                stringBuffer.append("{\"column\":\"id_kpipost\",\"value\":\"" + bcolumn.getId_kpipost() + "\",\"datatype\":\"varchar\"},");
                //考核类型
                stringBuffer.append("{\"column\":\"id_kpitype\",\"value\":\"" + bcolumn.getId_kpitype() + "\",\"datatype\":\"varchar\"},");
                //岗位
                stringBuffer.append("{\"column\":\"id_post\",\"value\":\"" + bcolumn.getId_post() + "\",\"datatype\":\"varchar\"},");
                //直接主管 改名称
                stringBuffer.append("{\"column\":\"id_supclerk\",\"value\":\"" + bcolumn.getId_supclerk() + "\",\"datatype\":\"varchar\"},");
                idsupclerk = bcolumn.getId_supclerk();
                //业务名
                stringBuffer.append("{\"column\":\"id_table\",\"value\":\"" + bcolumn.getId_table() + "\",\"datatype\":\"varchar\"},");
                //考核年
                stringBuffer.append("{\"column\":\"int_year\",\"value\":\"" + bcolumn.getInt_year() + "\",\"datatype\":\"int\"},");
                intyear = bcolumn.getInt_year();
                //行号1
                stringBuffer.append("{\"column\":\"line_no\",\"value\":\"" + bcolumn.getLine_no() + "\",\"datatype\":\"int\"},");
                //原始单行号
                stringBuffer.append("{\"column\":\"ori_line_no\",\"value\":\"" + bcolumn.getOri_line_no() + "\",\"datatype\":\"int\"},");
                //原始单号
                stringBuffer.append("{\"column\":\"ori_no\",\"value\":\"" + bcolumn.getOri_no() + "\",\"datatype\":\"varchar\"},");
                //完成情况
                stringBuffer.append("{\"column\":\"var_comp\",\"value\":\"" + bcolumn.getVar_comp() + "\",\"datatype\":\"varchar\"},");
                //备注
                stringBuffer.append("{\"column\":\"var_dremark\",\"value\":\"" + bcolumn.getVar_dremark() + "\",\"datatype\":\"varchar\"},");
                //评价意见
                stringBuffer.append("{\"column\":\"var_evares\",\"value\":\"" + bcolumn.getVar_evares() + "\",\"datatype\":\"varchar\"},");
                //评价标准
                stringBuffer.append("{\"column\":\"var_evastd\",\"value\":\"" + bcolumn.getVar_evastd() + "\",\"datatype\":\"varchar\"},");
                //驳回意见
                stringBuffer.append("{\"column\":\"var_rejust\",\"value\":\"" + bcolumn.getVar_rejust() + "\",\"datatype\":\"varchar\"},");
                //备注
                stringBuffer.append("{\"column\":\"var_remark\",\"value\":\"" + bcolumn.getVar_remark() + "\",\"datatype\":\"varchar\"}]}");

                if (dLine_no.size() > 0) {
                    stringBuffer.append(",");
                } else if (a != bcolumnList.size() - 1) {
                    stringBuffer.append(",");
                }
            }
        }

        if (dLine_no.size() > 0) {
            for (int b = 0; b < dLine_no.size(); b++) {
                int line_no = dLine_no.get(b);
                stringBuffer.append("{\"table\": \"dkpipost_03\",\"where\":\" \",\"oprdetail\":\"" + "D" + "\",\"data\":[");
                stringBuffer.append("{\"column\":\"flag_sts\",\"value\":\"" + "L" + "\",\"datatype\":\"varchar\"},");
                stringBuffer.append("{\"column\":\"id_flow\",\"value\":\"" + "0601" + "\",\"datatype\":\"varchar\"},");
                //审核时间
                stringBuffer.append("{\"column\":\"date_audit\",\"value\":\"" + "1900-01-01 00:00:00" + "\",\"datatype\":\"datetime\"},");
                //评价日期
                stringBuffer.append("{\"column\":\"date_eva\",\"value\":\"" + "" + "\",\"datatype\":\"datetime\"},");
                //计划签订日期
                stringBuffer.append("{\"column\":\"date_plan\",\"value\":\"" + dataplan + "\",\"datatype\":\"datetime\"},");
                //综合得分
                stringBuffer.append("{\"column\":\"dec_cmark\",\"value\":\"" + "" + "\",\"datatype\":\"decimal\"},");
                //扣分
                stringBuffer.append("{\"column\":\"dec_dmark\",\"value\":\"" + "" + "\",\"datatype\":\"decimal\"},");
                //个人得分
                stringBuffer.append("{\"column\":\"dec_emark\",\"value\":\"" + "" + "\",\"datatype\":\"decimal\"},");
                //个人总分
                stringBuffer.append("{\"column\":\"dec_smark\",\"value\":\"" + "" + "\",\"datatype\":\"decimal\"},");
                //已导入值
                stringBuffer.append("{\"column\":\"dec_import\",\"value\":\"" + "" + "\",\"datatype\":\"decimal\"},");
                //标准分值
                stringBuffer.append("{\"column\":\"dec_stdmark\",\"value\":\"" + "" + "\",\"datatype\":\"decimal\"},");
                //得分
                stringBuffer.append("{\"column\":\"dec_mark\",\"value\":\"" + "" + "\",\"datatype\":\"int\"},");
                //权重
                stringBuffer.append("{\"column\":\"dec_scale\",\"value\":\"" + "" + "\",\"datatype\":\"decimal\"},");
                //中止标志
                stringBuffer.append("{\"column\":\"flag_psts\",\"value\":\"" + "" + "\",\"datatype\":\"char\"},");
                //审核人
                stringBuffer.append("{\"column\":\"id_audit\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //前流程
                stringBuffer.append("{\"column\":\"id_bflow\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //绩效组  获取新的字段
                stringBuffer.append("{\"column\":\"id_cgroup\",\"value\":\"" + cgrgound + "\",\"datatype\":\"varchar\"},");
                //被评人
                stringBuffer.append("{\"column\":\"id_clerk\",\"value\":\"" + idclerk + "\",\"datatype\":\"varchar\"},");
                //部门
                stringBuffer.append("{\"column\":\"id_dept\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //评价人
                stringBuffer.append("{\"column\":\"id_evapsn\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //指标分类
                stringBuffer.append("{\"column\":\"id_kpicate\",\"value\":\"" + idkpicate + "\",\"datatype\":\"varchar\"},");
                //评价等级
                stringBuffer.append("{\"column\":\"id_kpigrade\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //评价周期
                stringBuffer.append("{\"column\":\"id_kpiperiod\",\"value\":\"" + idkpiperiod + "\",\"datatype\":\"varchar\"},");
                //考评阶段
                stringBuffer.append("{\"column\":\"id_kpiphase\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //指标
                stringBuffer.append("{\"column\":\"id_kpipost\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //考核类型
                stringBuffer.append("{\"column\":\"id_kpitype\",\"value\":\"" + "001" + "\",\"datatype\":\"varchar\"},");
                //岗位
                stringBuffer.append("{\"column\":\"id_post\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //直接主管 改名称
                stringBuffer.append("{\"column\":\"id_supclerk\",\"value\":\"" + idsupclerk + "\",\"datatype\":\"varchar\"},");
                //业务名
                stringBuffer.append("{\"column\":\"id_table\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //考核年
                stringBuffer.append("{\"column\":\"int_year\",\"value\":\"" + intyear + "\",\"datatype\":\"int\"},");
                //行号
                stringBuffer.append("{\"column\":\"line_no\",\"value\":\"" + line_no + "\",\"datatype\":\"int\"},");
                //原始单行号
                stringBuffer.append("{\"column\":\"ori_line_no\",\"value\":\"" + "" + "\",\"datatype\":\"int\"},");
                //原始单号
                stringBuffer.append("{\"column\":\"ori_no\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //完成情况
                stringBuffer.append("{\"column\":\"var_comp\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //备注
                stringBuffer.append("{\"column\":\"var_dremark\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //评价意见
                stringBuffer.append("{\"column\":\"var_evares\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //评价标准
                stringBuffer.append("{\"column\":\"var_evastd\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //驳回意见
                stringBuffer.append("{\"column\":\"var_rejust\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"},");
                //备注
                stringBuffer.append("{\"column\":\"var_remark\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"}]}");
                if (b != dLine_no.size() - 1) {
                    stringBuffer.append(",");
                }
            }
        }

        stringBuffer.append("]}");
        String str = stringBuffer.toString();
        getBusinessList(str, dealtype);
    }

    /**
     * 提交数据
     *
     * @param datas
     */
    private void getBusinessList(String datas, final String dealtype) {
//        OkGo.post("http://172.16.12.243:8085/hjmerp/servlet/DataUpdateServlet")
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataUpdateServlet")
                .isMultipart(true)
                .params("datas", datas)
                .execute(new BFlagCallBack<BusinessFlag>() {
                    @Override
                    public void onSuccess(BusinessFlag businessFlag, Call call, Response response) {
                        LogShow(businessFlag.toString());
                        String content = businessFlag.getMessage();
                        Constant.billsNo = businessFlag.getNo();
                        if (businessFlag.getFlag().equals("Y")) {
                            if (dealtype.equals(Constant.SAVE_DEALTYPE)) {
                                setMessage(SUCCEED_SAVE, content);
                            } else {
                                setMessage(SUCCEED_SEND, content);
                            }
                        } else {
                            setMessage(SUCCEED_ERROR, content);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (e instanceof OkGoException) {
                            showFailToast("服务器异常");
                            setMessage(SUCCEED_ERROR, "服务器错误");
                        }
                    }
                });
    }

    private void setMessage(int type, Object msg) {
        Message message = new Message();
        message.obj = msg;
        message.what = type;
        handler.sendMessage(message);
    }

    private void waitThread() {
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    handler.sendEmptyMessage(SUCCEED_THREAD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

    /**
     * 清除遍历的数据
     */
    private void removeData() {
        waitDialog.dismiss();
        widget_sum = 0.0;
        widgetkpi_sum = 0.0;
        list_WdigetGS.clear();
        list_WdigetKPI.clear();
        list_Kpicate.clear();
        oldLine_no.clear();
        newLine_no.clear();
        oldList.clear();
//        newList.clear();
        infoHashSet.clear();
        uLine_no.clear();
        nLine_no.clear();
        dLine_no.clear();
        gs_Widget.clear();
        kpi_Widget.clear();
        conclude_time_et = "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeData();
        nLine_no.clear();
        uLine_no.clear();
        dLine_no.clear();
        infoList.clear();
        Constant.billsNo = "";
        waitDialog.dismiss();
    }
}

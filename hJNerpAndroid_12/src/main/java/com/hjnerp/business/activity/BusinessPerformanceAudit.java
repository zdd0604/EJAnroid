package com.hjnerp.business.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjnerp.business.BusinessJsonCallBack.BFlagCallBack;
import com.hjnerp.business.businessutils.BusinessScore;
import com.hjnerp.business.businessutils.BusinessTimeUtils;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.dao.QiXinBaseDao;
import com.hjnerp.model.BusinessFlag;
import com.hjnerp.model.BusinessPerformanceColumn;
import com.hjnerp.model.BusinessPerformanceData;
import com.hjnerp.model.BusinessPerformanceInfo;
import com.hjnerp.model.PerformanceBean;
import com.hjnerp.model.PerformanceDatas;
import com.hjnerp.model.UserInfo;
import com.hjnerp.util.Log;
import com.hjnerp.util.StringUtil;
import com.hjnerp.util.ToastUtil;
import com.hjnerp.widget.ClearEditText;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.exception.OkGoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 绩效计划审核
 */
public class BusinessPerformanceAudit extends ActionBarWidgetActivity implements View.OnClickListener {

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
    //kpi分类标题
    @BindView(R.id.kpi_title)
    TextView kpi_title;
    //gs分类标题
    @BindView(R.id.gs_title)
    TextView gs_title;
    //总分
    @BindView(R.id.dec_smark_tx)
    TextView dec_smark_tx;
    //驳回意见 输入
    @BindView(R.id.input_approval_context)
    EditText input_approval_context;
    //驳回按钮
    @BindView(R.id.input_btn_disagree)
    Button input_btn_disagree;
    //保存按钮
    @BindView(R.id.input_performanc_save)
    Button input_performanc_save;
    //布局 被评人
    @BindView(R.id.layout_id_clerk)
    LinearLayout layout_id_clerk;
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
    //布局 驳回意见
    @BindView(R.id.input_approval_approval)
    RelativeLayout input_approval_approval;
    //布局 考核年
    @BindView(R.id.layout_int_year)
    LinearLayout layout_int_year;


    private LayoutInflater inflater;
    private String conclude_time_et;
    private String var_comp_et;
    private UserInfo myInfo;
    private String companyID;
    private String userID;
    private PerformanceDatas pds;
    private PerformanceDatas.MainBean mainBean;
    //行号
    private int line_numb = 0;

    List<View> kpiListView = new ArrayList<>();
    List<View> gsListView = new ArrayList<>();
    private int kpiindexofView = 0;
    private int gsindexofView = 0;
    private List<PerformanceBean> details;
    private List<Integer> line_no = new ArrayList<>();

    private List<PerformanceBean> kpiBean = new ArrayList<>();
    private List<PerformanceBean> gsBean = new ArrayList<>();

    private List<BusinessPerformanceInfo> infoList = new ArrayList<>();
    private List<BusinessPerformanceInfo> oldList = new ArrayList<>();
    private List<Double> kpi_Widget = new ArrayList<>();
    private List<Double> gs_Widget = new ArrayList<>();
    //权重的综合
    private Double widget_sum = 0.0;
    private Double widgetkpi_sum = 0.0;

    private Double smarksKpi = 0.0;
    private Double smarksGs = 0.0;
    //得分集合
    private List<Double> smarkKpi = new ArrayList<>();
    private List<Double> smarkGs = new ArrayList<>();

    private HashSet<Integer> oldLine_no = new HashSet<>();
    private HashSet<Integer> newLine_no = new HashSet<>();
    private HashSet infoHashSet = new HashSet();
    private List<Integer> nLine_no = new ArrayList<>();
    private List<Integer> uLine_no = new ArrayList<>();
    private List<Integer> dLine_no = new ArrayList<>();

    private static String cgrgound;
    private static String idclerk;
    private static String idkpiperiod;
    private static String idsupclerk;
    private static String dataplan;
    private static String idkpicate;
    private static String regect = "";
    private Boolean reject_isture = false;
    private Boolean var_comp_istrue = false;
    private static int intyear;
    private static int SUCCEED_SAVE = 0;//保存成功
    private static int SUCCEED_SEND = 1;//上传成功
    private static int SUCCEED_ERROR = 2;//上传失败
    private static int SUCCEED_THREAD = 3;//延迟销毁
    private Boolean submit_isTrue = false;//保存按钮的状态
    private Thread mThread;
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
                    submit_isTrue = true;
                    showFailToast(message);
                    //要在提交完成后删除
                    removeData();
                    break;
                case 1:
                    setResult(22);
                    showFailToast(message);
                    waitThread();
                    break;
                case 2:
                    submit_isTrue = false;
                    //要在提交完成后删除
                    removeData();
                    break;
                case 3:
                    submit_isTrue = false;
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
        input_performanc_save.setOnClickListener(this);
        inflater = LayoutInflater.from(this);

        pds = Constant.performanceDatas;
        mainBean = pds.getMain();
        input_id_clerk.setText(mainBean.getName_user());
        name_kpiperiod_tx.setTextAppearance(this,R.style.Item_LinearLayout_TvRightTitleStyle);
        name_kpiperiod_tx.setText(mainBean.getName_kpiperiod());
        input_int_year.setText(mainBean.getInt_year() + "");
        String conclude_time = mainBean.getDate_plan();
        conclude_time_et = conclude_time.substring(0, 10);
        input_conclude_time.setText(conclude_time_et);
        dec_smark_tx.setText(mainBean.getDec_emark() + "");
        dkpipost_no.setText(mainBean.getDkpipost_no());

        setTitleName();
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
     * 设置标题名称以及按钮名称
     */
    private void setTitleName() {

        if (Constant.ID_MENU.equals("002055")) {
            setLayoutVisibility("绩效计划审核", "审核", false, View.GONE, View.GONE,
                    null, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
        }

        if (Constant.ID_MENU.equals("002060")) {
            setLayoutVisibility("绩效计划确认", "确认", false, View.GONE, View.GONE,
                    null, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
        }

        if (Constant.ID_MENU.equals("002070")) {
            setLayoutVisibility("绩效完成情况自述", "确认", true, View.GONE, View.GONE,
                    null, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
        }

        if (Constant.ID_MENU.equals("002075")) {
            setLayoutVisibility("绩效完成情况评价", "确认", true, View.GONE, View.GONE,
                    null, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
        }

        if (Constant.ID_MENU.equals("002080")) {
            setLayoutVisibility("绩效评价结果确认", "确认", true, View.GONE, View.GONE,
                    null, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
        }

        if (Constant.ID_MENU.equals("002055") ||
                Constant.ID_MENU.equals("002060") ||
                Constant.ID_MENU.equals("002070") ||
                Constant.ID_MENU.equals("002075") ||
                Constant.ID_MENU.equals("002080")) {
            reject_isture = true;
            input_btn_disagree.setOnClickListener(this);
            layout_reject.setVisibility(View.VISIBLE);
        } else {
            layout_reject.setVisibility(View.GONE);
        }

        if (StringUtil.isStrTrue(mainBean.getVar_rejust())) {
            layout_var_reject.setVisibility(View.VISIBLE);
            input_var_reject = (TextView) findViewById(R.id.input_var_reject);
            input_var_reject.setText(mainBean.getVar_rejust());
        }

        kpiType();
    }

    /**
     * @param centerTv             中间标题
     * @param rightTv              右边按钮标题
     * @param var_comp             是否得到自述信息
     * @param btnAddKPI            kpi添加按钮
     * @param btnADDGS             gs添加按钮
     * @param conclude_time        计划日期的点击事件
     * @param conclude_time_layout 计划日期布局是否显示
     * @param id_clerk_layout      被评人是否显示
     * @param kpiperiod            考评周期是否显示
     * @param approval             驳回意见是否显示
     * @param dec_smark            总分显示
     */
    private void setLayoutVisibility(String centerTv, String rightTv, boolean var_comp, int btnAddKPI,
                                     int btnADDGS, View.OnClickListener conclude_time,
                                     int conclude_time_layout, int id_clerk_layout, int kpiperiod,
                                     int approval, int dec_smark) {
        actionCenterTv.setText(centerTv);
        actionRightTv.setText(rightTv);
        var_comp_istrue = var_comp;
        btn_addKpiView.setVisibility(btnAddKPI);
        btn_addGsView.setVisibility(btnADDGS);
        input_conclude_time.setOnClickListener(conclude_time);
        layout_conclude_time.setVisibility(conclude_time_layout);
        layout_id_clerk.setVisibility(id_clerk_layout);
        layout_name_kpiperiod.setVisibility(kpiperiod);
        input_approval_approval.setVisibility(approval);
        layout_dec_smark.setVisibility(dec_smark);
    }


    /**
     * 区分类别
     */
    private void kpiType() {
        details = pds.getDetails();
        for (int i = 0; i < details.size(); i++) {
            PerformanceBean per = details.get(i);
            line_no.add(Integer.valueOf(per.getLine_no()));
            oldLine_no.add(Integer.valueOf(per.getLine_no()));
            infoHashSet.add(Integer.valueOf(per.getLine_no()));
            if (per.getId_kpicate().equals("001")) {
                kpiBean.add(per);
            } else {
                gsBean.add(per);
            }
        }
        if (kpiBean.size() > 0)
            setLayouViewDatas(kpiBean, KPIType);

        if (gsBean.size() > 0)
            setLayouViewDatas(gsBean, GSTYPE);

        Collections.sort(line_no);
        line_numb = line_no.get(line_no.size() - 1);

        myInfo = QiXinBaseDao.queryCurrentUserInfo();
        if (myInfo != null) {
            userID = myInfo.userID;
            companyID = myInfo.companyID;
        }

        //保存第一次加载的数据
        addArryListData();
    }


    /**
     * 设置界面数据
     *
     * @param datas    kpi或者GS的数据集合
     * @param viewType 判断是KPI还是GS
     */
    private void setLayouViewDatas(List<PerformanceBean> datas, final int viewType) {
        for (int i = 0; i < datas.size(); i++) {
            final View view = inflater.inflate(R.layout.performance_item, null);
            TextView prfItemLineTitle = (TextView) view.findViewById(R.id.prfItemLineTitle);
            TextView line_no = (TextView) view.findViewById(R.id.input_line_id);
            TextView line_delete = (TextView) view.findViewById(R.id.input_line_delete);
            ClearEditText line_target = (ClearEditText) view.findViewById(R.id.input_id_kpipost);
            ClearEditText line_standard = (ClearEditText) view.findViewById(R.id.input_var_evastd);
            ClearEditText line_weight = (ClearEditText) view.findViewById(R.id.input_dec_scale);
            //布局 完成情况
            LinearLayout layout_inputvar_comp = (LinearLayout) view.findViewById(R.id.layout_inputvar_comp);
            //完成情况输入框
            EditText input_var_comp = (EditText) view.findViewById(R.id.input_var_comp);
            //得分
            LinearLayout layout_dec_mark = (LinearLayout) view.findViewById(R.id.layout_dec_mark);
            //得分
            EditText input_dec_mark = (EditText) view.findViewById(R.id.input_dec_mark);
            //设置小数点后两位
            setEditextPoint(input_dec_mark);

            PerformanceBean pBean = datas.get(i);
            line_no.setText(pBean.getLine_no());
            if (Constant.ID_MENU.equals("002075") ||
                    Constant.ID_MENU.equals("002080") ||
                    Constant.ID_MENU.equals("002070")) {
                if (StringUtil.isStrTrue(pBean.getVar_comp())) {
                    input_var_comp.setText(pBean.getVar_comp());
                    input_var_comp.setSelection(pBean.getVar_comp().length());
                }
            }

            input_dec_mark.setText(pBean.getDec_mark());
            if (pBean.getDec_mark().length() > 0)
                input_dec_mark.setSelection(pBean.getDec_mark().length());
            line_target.setText(pBean.getId_kpipost());
            if (pBean.getId_kpipost().length() > 0)
                line_target.setSelection(pBean.getId_kpipost().length());
            line_standard.setText(pBean.getVar_evastd());
            if (pBean.getVar_evastd().length() > 0)
                line_standard.setSelection(pBean.getVar_evastd().length());
            Double scale = Math.round(Double.valueOf(pBean.getDec_scale()) * 10000) / 100.0;
            line_weight.setText(scale + "");
            line_weight.setSelection(String.valueOf(scale).length());

            if (Constant.ID_MENU.equals("002060")) {
                line_target.setFocusable(false);
                line_standard.setFocusable(false);
                line_weight.setFocusable(false);
                line_delete.setVisibility(View.GONE);
            }
            if (Constant.ID_MENU.equals("002055")) {
                line_delete.setVisibility(View.GONE);
            }
            if (Constant.ID_MENU.equals("002070")) {
                line_target.setFocusable(false);
                line_standard.setFocusable(false);
                line_weight.setFocusable(false);
                line_delete.setVisibility(View.GONE);
                layout_inputvar_comp.setVisibility(View.VISIBLE);
            }

            if (Constant.ID_MENU.equals("002075")) {
                line_target.setFocusable(false);
                line_standard.setFocusable(false);
                line_weight.setFocusable(false);
                line_delete.setVisibility(View.GONE);
                layout_inputvar_comp.setVisibility(View.VISIBLE);
                layout_dec_mark.setVisibility(View.VISIBLE);
                input_dec_mark.setVisibility(View.VISIBLE);
            }

            if (Constant.ID_MENU.equals("002080")) {
                line_target.setFocusable(false);
                line_standard.setFocusable(false);
                line_weight.setFocusable(false);
                input_var_comp.setFocusable(false);
                input_dec_mark.setFocusable(false);
                line_delete.setVisibility(View.GONE);
                layout_inputvar_comp.setVisibility(View.VISIBLE);
                layout_dec_mark.setVisibility(View.VISIBLE);
                input_dec_mark.setVisibility(View.VISIBLE);
            }
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


            line_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (viewType) {
                        case KPIType:
                            deletelLayoutView(view, viewType);
                            break;
                        case GSTYPE:
                            deletelLayoutView(view, viewType);
                            break;
                    }
                }
            });
        }
    }


    /**
     * 添加第一加载的数据方便后面进行比较
     */
    private void addArryListData() {
        conclude_time_et = input_conclude_time.getText().toString();
        BusinessPerformanceInfo businessInfo = new BusinessPerformanceInfo();
        businessInfo.setTableid("dkpipost");
        businessInfo.setNo(mainBean.getDkpipost_no());
        businessInfo.setUserid(mainBean.getId_recorder());
        businessInfo.setComid(mainBean.getId_com());
        //存放绩效详情的数据
        List<BusinessPerformanceColumn> columns = new ArrayList<>();
        for (int i = 0; i < details.size(); i++) {
            PerformanceBean per = details.get(i);
            List<BusinessPerformanceData> datas = new ArrayList<>();
            BusinessPerformanceData businessPerformanceData = new BusinessPerformanceData();
            businessPerformanceData.setTable("dkpipost_03");
            BusinessPerformanceColumn businessPerformanceColumn = new BusinessPerformanceColumn();
            businessPerformanceColumn.setFlag_sts(mainBean.getFlag_sts());
            businessPerformanceColumn.setFlag_psts(mainBean.getFlag_sts());
            businessPerformanceColumn.setDate_audit(mainBean.getDate_audit());
            businessPerformanceColumn.setDate_eva(mainBean.getDate_eva());
            businessPerformanceColumn.setDate_plan(mainBean.getDate_plan());
            businessPerformanceColumn.setDec_cmark(Double.valueOf(mainBean.getDec_cmark()));
            businessPerformanceColumn.setDec_dmark(Double.valueOf(mainBean.getDec_dmark()));
            businessPerformanceColumn.setDec_emark(Double.valueOf(mainBean.getDec_emark()));
            businessPerformanceColumn.setDec_smark(Double.valueOf(mainBean.getDec_smark()));
            businessPerformanceColumn.setDec_import(Double.valueOf(per.getDec_import()));
            businessPerformanceColumn.setDec_stdmark(Double.valueOf(per.getDec_stdmark()));
            businessPerformanceColumn.setDec_mark(Double.valueOf(per.getDec_mark()));
            Double scale = Double.valueOf(per.getDec_scale()) / 100;
            businessPerformanceColumn.setDec_scale(scale);
            businessPerformanceColumn.setId_audit(mainBean.getId_audit());
            businessPerformanceColumn.setId_flow(mainBean.getId_flow());
            businessPerformanceColumn.setId_bflow(mainBean.getId_bflow());
            businessPerformanceColumn.setId_cgroup(mainBean.getId_cgroup());
            businessPerformanceColumn.setId_clerk(mainBean.getId_clerk());
            businessPerformanceColumn.setId_dept(mainBean.getId_dept());
            businessPerformanceColumn.setId_evapsn(mainBean.getId_evapsn());
            businessPerformanceColumn.setId_kpicate(per.getId_kpicate());
            businessPerformanceColumn.setId_kpiperiod(mainBean.getId_kpiperiod());
            businessPerformanceColumn.setId_kpiphase(mainBean.getId_kpiphase());
            businessPerformanceColumn.setId_kpigrade(mainBean.getId_kpigrade());
            businessPerformanceColumn.setId_kpipost(per.getId_kpipost());
            businessPerformanceColumn.setId_kpitype(mainBean.getId_kpitype());
            businessPerformanceColumn.setId_post(mainBean.getId_post());
            businessPerformanceColumn.setId_supclerk(mainBean.getId_supclerk());
            businessPerformanceColumn.setId_table(per.getId_table());
            businessPerformanceColumn.setInt_year(Integer.valueOf(mainBean.getInt_year()));
            businessPerformanceColumn.setLine_no(Integer.valueOf(per.getLine_no()));
            businessPerformanceColumn.setOri_line_no(Integer.valueOf(per.getOri_line_no()));
            businessPerformanceColumn.setVar_comp(per.getVar_comp());
            businessPerformanceColumn.setVar_dremark(per.getVar_dremark());
            businessPerformanceColumn.setVar_evastd(per.getVar_evastd());
            businessPerformanceColumn.setOri_no(per.getOri_no());
            businessPerformanceColumn.setVar_evares(mainBean.getVar_evares());
            businessPerformanceColumn.setVar_rejust(mainBean.getVar_rejust());
            businessPerformanceColumn.setVar_remark(mainBean.getVar_remark());
            columns.add(businessPerformanceColumn);
            businessPerformanceData.setData(columns);
            datas.add(businessPerformanceData);
            businessInfo.setData(datas);
        }
        infoList.add(businessInfo);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_left_tv:
                finish();
                break;
            case R.id.btn_add_kpiview:
                addLayoutView(KPIType);
                break;
            case R.id.btn_add_gsview:
                addLayoutView(GSTYPE);
                break;
            case R.id.input_performanc_save:
                submitDatas(mainBean.getFlag_sts(), "save", "1002", mainBean.getId_recorder());
                break;
            case R.id.action_right_tv1:
                submitDatas(mainBean.getFlag_sts(), "save", "1002", mainBean.getId_recorder());
                break;
            case R.id.action_right_tv:
//                if (submit_isTrue) {
                if (Constant.ID_MENU.equals("002055")) {
                    submitDatas(mainBean.getFlag_sts(), "audit", "1004", mainBean.getId_recorder());
                }
                if (Constant.ID_MENU.equals("002060")) {
                    submitDatas(mainBean.getFlag_sts(), "custom", "1005", mainBean.getId_recorder());
                }
                if (Constant.ID_MENU.equals("002070")) {
                    submitDatas(mainBean.getFlag_sts(), "custom", "1007", mainBean.getId_recorder());
                }
                if (Constant.ID_MENU.equals("002075")) {
                    submitDatas(mainBean.getFlag_sts(), "custom", "1009", mainBean.getId_recorder());
                }
                if (Constant.ID_MENU.equals("002080")) {
                    submitDatas(mainBean.getFlag_sts(), "custom", "1011", mainBean.getId_recorder());
                }
//                } else {
//                    ToastUtil.ShowLong(BusinessPerformanceAudit.this, "必须先保存数据才能提交！");
//                }
                break;
            case R.id.input_conclude_time:
                showCalendar(input_conclude_time);
                break;
            case R.id.input_btn_disagree:
//                if (submit_isTrue) {
                if (Constant.ID_MENU.equals("002055")) {
                    submitDatas(mainBean.getFlag_sts(), "reject", "1003", mainBean.getId_recorder());
                }
                if (Constant.ID_MENU.equals("002060")) {
                    submitDatas(mainBean.getFlag_sts(), "custom", "1006", mainBean.getId_recorder());
                }
                if (Constant.ID_MENU.equals("002070")) {
                    submitDatas(mainBean.getFlag_sts(), "custom", "1008", mainBean.getId_recorder());
                }
                if (Constant.ID_MENU.equals("002075")) {
                    submitDatas(mainBean.getFlag_sts(), "custom", "1010", mainBean.getId_recorder());
                }
                if (Constant.ID_MENU.equals("002080")) {
                    submitDatas(mainBean.getFlag_sts(), "custom", "1012", mainBean.getId_recorder());
                }
//                } else {
//                    ToastUtil.ShowLong(BusinessPerformanceAudit.this, "必须先保存数据才能驳回！");
//                }
                break;
        }
    }


    private void submitDatas(String flag_sts, String dealtype, String buttonid, String id_recorder) {
        removeData();
        waitDialog.show();
        if (kpiListView.size() == 0 && gsListView.size() == 0) {
            ToastUtil.ShowLong(this, "至少添加一条详情记录");
            removeData();
            return;
        }
        conclude_time_et = input_conclude_time.getText().toString();
        if (reject_isture) {
            regect = input_approval_context.getText().toString();
        }

        BusinessPerformanceInfo businessInfo = new BusinessPerformanceInfo();
        businessInfo.setTableid("dkpipost");
        businessInfo.setUserid(userID);
        businessInfo.setComid(companyID);
        businessInfo.setNo(mainBean.getDkpipost_no());

        //存放绩效详情的数据
        List<BusinessPerformanceColumn> columns = new ArrayList<>();
        for (int i = 0; i < kpiListView.size(); i++) {
            View view = kpiListView.get(i);
            TextView line_no = (TextView) view.findViewById(R.id.input_line_id);
            TextView line_delete = (TextView) view.findViewById(R.id.input_line_delete);
            ClearEditText input_id_kpipost = (ClearEditText) view.findViewById(R.id.input_id_kpipost);
            ClearEditText input_var_evastd = (ClearEditText) view.findViewById(R.id.input_var_evastd);
            EditText input_dec_scale = (EditText) view.findViewById(R.id.input_dec_scale);
            EditText input_dec_mark = (EditText) view.findViewById(R.id.input_dec_mark);
            EditText input_var_comp = (EditText) view.findViewById(R.id.input_var_comp);
            if (!StringUtil.isStrTrue(input_id_kpipost.getText().toString())) {
                ToastUtil.ShowLong(this, "关键绩效 " + line_no.getText().toString().trim() + " 指标不能为空");
                removeData();
                return;
            }
            if (!StringUtil.isStrTrue(input_var_evastd.getText().toString())) {
                ToastUtil.ShowLong(this, "关键绩效 " + line_no.getText().toString().trim() + " 评价标准不能为空");
                removeData();
                return;
            }
            if (!StringUtil.isStrTrue(input_dec_scale.getText().toString())) {
                ToastUtil.ShowLong(this, "关键绩效 " + line_no.getText().toString().trim() + " 权重不能为空");
                removeData();
                return;
            }

            if (var_comp_istrue) {
                var_comp_et = input_var_comp.getText().toString();
            }
            List<BusinessPerformanceData> datas = new ArrayList<>();
            BusinessPerformanceData businessPerformanceData = new BusinessPerformanceData();
            businessPerformanceData.setTable("dkpipost_03");
            BusinessPerformanceColumn businessPerformanceColumn = new BusinessPerformanceColumn();
            businessPerformanceColumn.setFlag_sts(flag_sts);
            businessPerformanceColumn.setFlag_psts("");
            businessPerformanceColumn.setId_flow("0601");
            businessPerformanceColumn.setId_bflow("");
            businessPerformanceColumn.setDate_audit("1900-01-01 00:00:00");
            businessPerformanceColumn.setDate_eva(BusinessTimeUtils.getCurrentTime(Constant.NOWTIME));
//            String time = conclude_time_et + BusinessTimeUtils.getCurrentTime(Constant.NOWTIME2);
            businessPerformanceColumn.setDate_plan(conclude_time_et);
//            businessPerformanceColumn.setDate_plan(mainBean.getDate_plan());
            businessPerformanceColumn.setDec_cmark(0.00);
            businessPerformanceColumn.setDec_dmark(0.00);
            businessPerformanceColumn.setDec_emark(0.00);
            businessPerformanceColumn.setDec_smark(0.00);
            businessPerformanceColumn.setDec_import(0.0000000);
            businessPerformanceColumn.setDec_stdmark(0.0000000);
//            businessPerformanceColumn.setDec_mark(Double.valueOf(input_dec_mark.getText().toString()));
            Double decmark = Double.valueOf(input_dec_mark.getText().toString());
            if (StringUtil.isStrTrue(String.valueOf(decmark))) {
                businessPerformanceColumn.setDec_mark(decmark);
            } else {
                businessPerformanceColumn.setDec_mark(0.0);
            }
            Double scale = Double.valueOf(input_dec_scale.getText().toString()) / 100;
            businessPerformanceColumn.setDec_scale(scale);
            kpi_Widget.add(scale);
            if (kpiListView.size() > 0 && gsListView.size() > 0) {
                smarkKpi.add(BusinessScore.smark1(scale, decmark));
            } else {
                smarkKpi.add(BusinessScore.smark2(scale, decmark));
            }
            businessPerformanceColumn.setId_audit("");
            businessPerformanceColumn.setId_cgroup(mainBean.getId_cgroup());
            businessPerformanceColumn.setId_clerk(mainBean.getId_clerk());
            businessPerformanceColumn.setId_dept(mainBean.getId_dept());
            businessPerformanceColumn.setId_evapsn(mainBean.getId_evapsn());
            businessPerformanceColumn.setId_kpicate("001");
            businessPerformanceColumn.setId_kpiperiod(mainBean.getId_kpiperiod());
            businessPerformanceColumn.setId_kpiphase(mainBean.getId_kpiphase());
            businessPerformanceColumn.setId_kpigrade(mainBean.getId_kpigrade());
            businessPerformanceColumn.setId_kpipost(input_id_kpipost.getText().toString());
            businessPerformanceColumn.setId_kpitype("001");
            businessPerformanceColumn.setId_recorder(id_recorder);
            businessPerformanceColumn.setId_post(mainBean.getId_post());
            businessPerformanceColumn.setId_supclerk(mainBean.getId_supclerk());
            businessPerformanceColumn.setId_table("");
            businessPerformanceColumn.setInt_year(Integer.valueOf(mainBean.getInt_year()));
            businessPerformanceColumn.setLine_no(Integer.valueOf(line_no.getText().toString()));
            businessPerformanceColumn.setOri_line_no(0);
            businessPerformanceColumn.setVar_comp(var_comp_et);
            businessPerformanceColumn.setVar_dremark("");
            businessPerformanceColumn.setVar_evastd(input_var_evastd.getText().toString());
            businessPerformanceColumn.setOri_no("");
            businessPerformanceColumn.setVar_evares(mainBean.getVar_evares());
            businessPerformanceColumn.setVar_rejust(regect);
            businessPerformanceColumn.setVar_remark(mainBean.getVar_remark());
            newLine_no.add(Integer.valueOf(line_no.getText().toString()));
//            infoHashSet.add(Integer.valueOf(item_line_id.getText().toString()));
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
            EditText input_dec_mark = (EditText) view.findViewById(R.id.input_dec_mark);
            EditText input_var_comp = (EditText) view.findViewById(R.id.input_var_comp);

            if (!StringUtil.isStrTrue(input_id_kpipost.getText().toString())) {
                ToastUtil.ShowLong(this, "工作目标 " + line_no.getText().toString().trim() + " 指标不能为空");
                removeData();
                return;
            }
            if (!StringUtil.isStrTrue(input_var_evastd.getText().toString())) {
                ToastUtil.ShowLong(this, "工作目标 " + line_no.getText().toString().trim() + " 评价标准不能为空");
                removeData();
                return;
            }
            if (!StringUtil.isStrTrue(input_dec_scale.getText().toString())) {
                ToastUtil.ShowLong(this, "工作目标 " + line_no.getText().toString().trim() + " 权重不能为空");
                removeData();
                return;
            }

            if (var_comp_istrue) {
                var_comp_et = input_var_comp.getText().toString();
            }
            List<BusinessPerformanceData> datas = new ArrayList<>();
            BusinessPerformanceData businessPerformanceData = new BusinessPerformanceData();
            businessPerformanceData.setTable("dkpipost_03");
            BusinessPerformanceColumn businessPerformanceColumn = new BusinessPerformanceColumn();
            businessPerformanceColumn.setFlag_sts(flag_sts);
            businessPerformanceColumn.setFlag_psts("");
            businessPerformanceColumn.setId_flow("0601");
            businessPerformanceColumn.setId_bflow("");
            businessPerformanceColumn.setDate_audit("1900-01-01 00:00:00");
            businessPerformanceColumn.setDate_eva(BusinessTimeUtils.getCurrentTime(Constant.NOWTIME));
            String time = conclude_time_et + BusinessTimeUtils.getCurrentTime(Constant.NOWTIME2);
            businessPerformanceColumn.setDate_plan(time);
            businessPerformanceColumn.setDec_cmark(0.00);
            businessPerformanceColumn.setDec_dmark(0.00);
            businessPerformanceColumn.setDec_emark(0.00);
            businessPerformanceColumn.setDec_smark(0.00);
            businessPerformanceColumn.setDec_import(0.0000000);
            businessPerformanceColumn.setDec_stdmark(0.0000000);
            Double decmark = Double.valueOf(input_dec_mark.getText().toString());
            if (StringUtil.isStrTrue(String.valueOf(decmark))) {
                businessPerformanceColumn.setDec_mark(decmark);
            } else {
                businessPerformanceColumn.setDec_mark(0.0);
            }
            Double scale = Double.valueOf(input_dec_scale.getText().toString()) / 100;
            businessPerformanceColumn.setDec_scale(scale);
            gs_Widget.add(scale);
            if (kpiListView.size() > 0 && gsListView.size() > 0) {
                smarkGs.add(BusinessScore.smark1(scale, decmark));
            } else {
                smarkGs.add(BusinessScore.smark2(scale, decmark));
            }
            businessPerformanceColumn.setId_audit("");
            businessPerformanceColumn.setId_cgroup(mainBean.getId_cgroup());
            businessPerformanceColumn.setId_clerk(mainBean.getId_clerk());
            businessPerformanceColumn.setId_dept(mainBean.getId_dept());
            businessPerformanceColumn.setId_evapsn("");
            businessPerformanceColumn.setId_kpicate("002");
            businessPerformanceColumn.setId_kpiperiod(mainBean.getId_kpiperiod());
            businessPerformanceColumn.setId_kpiphase(mainBean.getId_kpiphase());
            businessPerformanceColumn.setId_kpigrade(mainBean.getId_kpigrade());
            businessPerformanceColumn.setId_kpipost(input_id_kpipost.getText().toString());
            businessPerformanceColumn.setId_kpitype("001");
            businessPerformanceColumn.setId_post(mainBean.getId_post());
            businessPerformanceColumn.setId_supclerk(mainBean.getId_supclerk());
            businessPerformanceColumn.setId_table("");
            businessPerformanceColumn.setInt_year(Integer.valueOf(mainBean.getInt_year()));
            businessPerformanceColumn.setLine_no(Integer.valueOf(line_no.getText().toString()));
            businessPerformanceColumn.setOri_line_no(0);
            businessPerformanceColumn.setVar_comp(var_comp_et);
            businessPerformanceColumn.setId_recorder(id_recorder);
            businessPerformanceColumn.setVar_dremark("");
            businessPerformanceColumn.setVar_evastd(input_var_evastd.getText().toString());
            businessPerformanceColumn.setOri_no("");
            businessPerformanceColumn.setVar_evares("");
            businessPerformanceColumn.setVar_rejust(regect);
            businessPerformanceColumn.setVar_remark(mainBean.getVar_remark());
            newLine_no.add(Integer.valueOf(line_no.getText().toString()));
//            infoHashSet.add(Integer.valueOf(item_line_id.getText().toString()));
            columns.add(businessPerformanceColumn);
            businessPerformanceData.setData(columns);
            datas.add(businessPerformanceData);
            businessInfo.setData(datas);
        }

        infoList.add(businessInfo);
        computeSum(businessInfo, dealtype, buttonid);
    }

    /**
     * 判断之前保存的一条与现在新的区别
     */
    private void judgeDatas(BusinessPerformanceInfo bInfo, String dealtype, String buttonid) {
        if (infoList.size() > 1) {
            oldList.add(infoList.get(infoList.size() - 2));
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
                    } else {
                        dLine_no.add(content);
                    }
                } else {
                    if (newLine_no.contains(content)) {
                        nLine_no.add(content);
                    }
                }
            }
        } else if (oldList.size() > 0) {

            oldList.add(infoList.get(infoList.size() - 1));
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
                    } else {
                        dLine_no.add(content);
                    }
                } else {
                    if (newLine_no.contains(content)) {
                        nLine_no.add(content);
                    }
                }
            }
        }
        setSubString(bInfo, dealtype, buttonid);
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
            if (widgetkpi_sum != 1.0) {
                ToastUtil.ShowLong(this, "关键绩效指标(KPI)的权重不等于100%");
                removeData();
                if (infoList.size() > 0) {
                    infoList.remove(infoList.size() - 1);
                }
                if (kpi_Widget.size() > 0) {
                    kpi_Widget.remove(kpi_Widget.size() - 1);
                }
                return;
            }
        }
        if (gs_Widget.size() > 0) {
            for (Double number : gs_Widget) {
                widget_sum += number;
            }

            Log.v("show", "list_WdigetGS:" + gs_Widget.toString() + "和是：" + widget_sum);
            if (widget_sum != 1.0) {
                ToastUtil.ShowLong(this, "工作目标(GS)的权重不等于100%");
                removeData();
                if (infoList.size() > 0) {
                    infoList.remove(infoList.size() - 1);
                }
                if (gs_Widget.size() > 0) {
                    gs_Widget.remove(gs_Widget.size() - 1);
                }
                return;
            }
        }


        if (smarkKpi.size() > 0) {
            for (Double number : smarkKpi) {
                smarksKpi += number;
            }
//            dec_smark_tx.setText(smarksKpi + "");
        }
        if (smarkGs.size() > 0) {
            for (Double number : smarkGs) {
                smarksGs += number;
            }
        }

        dec_smark_tx.setText((smarksGs + smarksKpi) + "");
        judgeDatas(bInfo, dealtype, buttonid);
    }

    /**
     * 删除View
     *
     * @param view
     */
    private void deletelView(final View view) {
        final Dialog noticeDialog = new Dialog(this, R.style.noticeDialogStyle);
        noticeDialog.setContentView(R.layout.dialog_notice_withcancel);
        TextView dialog_cancel_rl, dialog_confirm_rl;
        TextView notice = (TextView) noticeDialog
                .findViewById(R.id.dialog_notice_tv);
        notice.setText("是否要删除该绩效明细？");
        dialog_cancel_rl = (TextView) noticeDialog
                .findViewById(R.id.dialog_cancel_tv);
        TextView dialog_cancel_tv = (TextView) noticeDialog
                .findViewById(R.id.dialog_cancel_tv);
        dialog_cancel_tv.setText("取消");
        dialog_confirm_rl = (TextView) noticeDialog
                .findViewById(R.id.dialog_confirm_tv);
        TextView dialog_confirm_tv = (TextView) noticeDialog
                .findViewById(R.id.dialog_confirm_tv);
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

//                计算
                kpiindexofView = kpiListView.indexOf(view);
                lay_kpiView.removeView(view);
                kpiListView.remove(view);
                Log.v("show", "计算后的下标：" + kpiindexofView + ",,,,," + kpiListView.size());
                if (kpi_Widget.size() >= kpiindexofView && kpi_Widget.size() > 0) {
                    kpi_Widget.remove(kpiindexofView);
                }

            }
        });
        noticeDialog.show();
    }

    /**
     * 删除View
     *
     * @param view
     */
    private void deletelGsView(final View view) {
        final Dialog noticeDialog = new Dialog(this, R.style.noticeDialogStyle);
        noticeDialog.setContentView(R.layout.dialog_notice_withcancel);
        TextView dialog_cancel_rl, dialog_confirm_rl;
        TextView notice = (TextView) noticeDialog
                .findViewById(R.id.dialog_notice_tv);
        notice.setText("是否要删除该绩效明细？");
        dialog_cancel_rl = (TextView) noticeDialog
                .findViewById(R.id.dialog_cancel_tv);
        TextView dialog_cancel_tv = (TextView) noticeDialog
                .findViewById(R.id.dialog_cancel_tv);
        dialog_cancel_tv.setText("取消");
        dialog_confirm_rl = (TextView) noticeDialog
                .findViewById(R.id.dialog_confirm_tv);
        TextView dialog_confirm_tv = (TextView) noticeDialog
                .findViewById(R.id.dialog_confirm_tv);
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
//                计算
                gsindexofView = gsListView.indexOf(view);
                lay_gsView.removeView(view);
                gsListView.remove(view);
                Log.v("show", "计算后的下标：" + gsindexofView + ",,,,," + kpiListView.size());
                if (gs_Widget.size() >= gsindexofView && gs_Widget.size() > 0) {
                    gs_Widget.remove(gsindexofView);
                }

            }
        });
        noticeDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        regect = "";
        Constant.billsNo = "";
        removeData();
    }

    /**
     * 拼接字符串
     */
    private void setSubString(BusinessPerformanceInfo bInfo, String dealtype, String buttonid) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{\"tableid\":\"dkpipost\",\"opr\":\"SS\",\"no\":\"" + bInfo.getNo() + "\",\"userid\":\"" + userID +
                "\",\"comid\":\"" + companyID + "\"," + "\"menuid\":\"" + Constant.ID_MENU + "\",\"dealtype\":\""
                + dealtype + "\", \"buttonid\":\"" + buttonid + "\",\"data\":[");
        List<BusinessPerformanceData> bdataList = bInfo.getData();
        for (int i = 0; i < bdataList.size(); i++) {
            BusinessPerformanceData bdata = bdataList.get(i);
            List<BusinessPerformanceColumn> bcolumnList = bdata.getData();
            for (int a = 0; a < bcolumnList.size(); a++) {
                BusinessPerformanceColumn bcolumn = bcolumnList.get(a);
                if (uLine_no.contains(bcolumn.getLine_no())) {
                    stringBuffer.append("{\"table\":\"dkpipost_03\",\"where\":\" \",\"oprdetail\":\"" + "U" + "\",\"data\":[");
                } else if (nLine_no.contains(bcolumn.getLine_no())) {
                    stringBuffer.append("{\"table\": \"dkpipost_03\",\"where\":\" \",\"oprdetail\":\"" + "N" + "\",\"data\":[");
                } else {
                    stringBuffer.append("{\"table\": \"dkpipost_03\",\"where\":\" \",\"oprdetail\":\"" + "N" + "\",\"data\":[");
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
                //录入人
                stringBuffer.append("{\"column\":\"id_recorder\",\"value\":\"" + bcolumn.getId_recorder() + "\",\"datatype\":\"varchar\"},");
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
                stringBuffer.append("{\"column\":\"id_flow\",\"value\":\"" + 0601 + "\",\"datatype\":\"varchar\"},");
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
                //录入人
                stringBuffer.append("{\"column\":\"id_recorder\",\"value\":\"" + userID + "\",\"datatype\":\"varchar\"},");
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
        Log.v("show", stringBuffer.toString());
        getBusinessList(str, dealtype);
    }

    private void waitThread() {
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    handler.sendEmptyMessage(SUCCEED_THREAD);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

    private void setMessage(int type, Object msg) {
        Message message = new Message();
        message.obj = msg;
        message.what = type;
        handler.sendMessage(message);
    }

    /**
     * 提交数据
     *
     * @param datas
     */
    private void getBusinessList(String datas, final String dealtype) {
//        OkGo.post("http://172.16.12.243:8085/hjmerp/servlet/DataUpdateServlet")
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataUpdateServlet")
//        OkGo.post("http://192.168.199.215:8085/hjmerp/servlet/DataUpdateServlet")//
                .isMultipart(true)
                .params("datas", datas)
//                .cacheKey(Constant.ID_MENU)            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
//                .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new BFlagCallBack<BusinessFlag>() {
                    @Override
                    public void onSuccess(BusinessFlag businessFlag, Call call, Response response) {
                        String content = businessFlag.getMessage();
                        if (dealtype.equals(Constant.SAVE_DEALTYPE)) {
                            Constant.billsNo = businessFlag.getNo();
                            setMessage(SUCCEED_SAVE, content);
                        } else {
                            Constant.billsNo = businessFlag.getNo();
                            setMessage(SUCCEED_SEND, content);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (e instanceof OkGoException) {
                            ToastUtil.ShowLong(BusinessPerformanceAudit.this, "服务器异常");
                            handler.sendEmptyMessage(SUCCEED_ERROR);
                        }
                    }

//                    @Override
//                    public void onCacheSuccess(businessFlag businessFlag, Call call) {
//                        onSuccess(businessFlag, call, null);
//                    }

                });
    }

    /**
     * 清除数据
     */
    private void removeData() {
        waitDialog.dismiss();
        gs_Widget.clear();
        kpi_Widget.clear();
        widget_sum = 0.0;
        widgetkpi_sum = 0.0;
        conclude_time_et = "";

        oldLine_no.clear();
        newLine_no.clear();
        oldList.clear();

        infoHashSet.clear();
        uLine_no.clear();
        nLine_no.clear();
        dLine_no.clear();

        smarksKpi = 0.0;
        smarksGs = 0.0;
        smarkKpi.clear();
        smarkGs.clear();

    }
}

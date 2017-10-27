package com.hjnerp.business.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjnerp.business.BusinessJsonCallBack.BFlagCallBack;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.dao.BusinessBaseDao;
import com.hjnerp.model.AbnormalDetailModel;
import com.hjnerp.model.Ctlm1345;
import com.hjnerp.model.Ej1345;
import com.hjnerp.model.PerformanceBean;
import com.hjnerp.model.PerformanceDatas;
import com.hjnerp.model.businessFlag;
import com.hjnerp.util.StringUtil;
import com.hjnerp.widget.ClearEditText;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.exception.OkGoException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 考勤异常申诉
 */
public class AbnormalBusiness extends ActionBarWidgetActivity implements View.OnClickListener,
        AbnormalDetail.AbNormalDetailListener {
    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    @BindView(R.id.abnormal_name)
    TextView abnormal_name;
    @BindView(R.id.abnormal_part)
    TextView abnormal_part;
    @BindView(R.id.add_layout_abnormal)
    LinearLayout add_layout_abnormal;
    @BindView(R.id.add_abnormal)
    LinearLayout add_abnormal;
    @BindView(R.id.busadd)
    TextView busadd;
    @BindView(R.id.var_rejust_name_abnormal)
    TextView var_rejust_name_abnormal;
    @BindView(R.id.rejust_list_abnormal)
    LinearLayout rejust_list_abnormal;

    private String name_user;
    private String id_user;
    private String name_dept;
    private String id_com;
    private String id_dept;
    private List<Ctlm1345> users;
    private String id_clerk;
    private String id_linem;
    private List<AbnormalDetailModel> detailModelList;
    private List<View> listaddview;
    private PerformanceDatas pds;
    private PerformanceDatas.MainBean mainBean;
    private List<PerformanceBean> details;
    private int countDetail = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abnormal_business);
        ButterKnife.bind(this);
        initData();
        initView();
    }


    private void initData() {
        users = new ArrayList<>();
        users = BusinessBaseDao.getCTLM1345ByIdTable("user");
        if (users.size() == 0) {
            showFailToast("请先下载基础数据");
            finish();
            return;
        }
        String userinfos = users.get(0).getVar_value();
        Ej1345 ej1345 = mGson.fromJson(userinfos, Ej1345.class);
        id_clerk = ej1345.getId_clerk();
        id_user = ej1345.getId_user();
        name_user = ej1345.getName_user();
        id_dept = ej1345.getId_dept();
        name_dept = ej1345.getName_dept();
        id_com = ej1345.getId_com();
        id_linem = ej1345.getId_linem();

        detailModelList = new ArrayList<>();
        listaddview = new ArrayList<>();
    }

    private void initView() {
        actionCenterTv.setText(getString(R.string.detail_Title_NormalDetail));
        actionRightTv.setText(getString(R.string.action_right_content_commit));
        actionLeftTv.setOnClickListener(this);
        actionRightTv.setOnClickListener(this);
        add_abnormal.setOnClickListener(this);

        AbnormalDetail.setAbListener(this);

        if (users.size() == 0) {
            showFailToast("请先下载基础数据");
            finish();
            return;
        }

        abnormal_name.setText(name_user.trim());
        abnormal_part.setText(name_dept.trim());

        addlayout(detailModelList);

        if (!Constant.JUDGE_TYPE) {
            setRejustContext();
        }
    }


    /**
     * 保存的数据
     */
    private void setRejustContext() {
        pds = Constant.performanceDatas;
        mainBean = pds.getMain();
        details = pds.getDetails();
        if (StringUtil.isStrTrue(mainBean.getVar_rejust())) {
            rejust_list_abnormal.setVisibility(View.VISIBLE);
            var_rejust_name_abnormal.setText(mainBean.getVar_rejust());
        }

        busadd.setText("重新选择");
        countDetail = details.size();
        if (detailModelList == null || detailModelList.size() == 0) {
            for (int i = 0; i < details.size(); i++) {
                addlayoutforrejust(i);
            }
        }
    }


    /**
     * 新增添加数据
     *
     * @param detailModelList
     */
    private void addlayout(List<AbnormalDetailModel> detailModelList) {
        for (int i = 0; i < detailModelList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.abnormal_detail, add_layout_abnormal, false);
            add_layout_abnormal.addView(view);
            listaddview.add(view);
            TextView abnormal_date = (TextView) view.findViewById(R.id.abnormal_date);
            TextView abnormal_type = (TextView) view.findViewById(R.id.abnormal_type);
            TextView abnormal_time = (TextView) view.findViewById(R.id.abnormal_time);
            abnormal_date.setText(detailModelList.get(i).getVar_date());
            abnormal_type.setText(detailModelList.get(i).getVar_on());
            abnormal_time.setText(detailModelList.get(i).getVar_off());
        }
    }

    /**
     * 保存的数据
     *
     * @param i
     */
    private void addlayoutforrejust(int i) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.abnormal_detail, add_layout_abnormal, false);
        add_layout_abnormal.addView(view);
        listaddview.add(view);
        TextView abnormal_date = (TextView) view.findViewById(R.id.abnormal_date);
        TextView abnormal_type = (TextView) view.findViewById(R.id.abnormal_type);
        TextView abnormal_time = (TextView) view.findViewById(R.id.abnormal_time);
        ClearEditText abnormal_remark = (ClearEditText) view.findViewById(R.id.abnormal_remark);

        abnormal_date.setText(details.get(i).getVar_date());
        abnormal_type.setText(details.get(i).getVar_on());
        abnormal_time.setText(details.get(i).getVar_off());
        abnormal_remark.setText(details.get(i).getVar_remark());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_left_tv:
                finish();
                break;
            case R.id.action_right_tv:
                submit();
                break;
            case R.id.add_abnormal:
                intentActivity(AbnormalDetail.class);
                break;
        }
    }

    private void submit() {
        // validate
        String name = abnormal_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showFailToast("名字不能为空");
            return;
        }

        String part = abnormal_part.getText().toString().trim();
        if (TextUtils.isEmpty(part)) {
            showFailToast("部门不能为空");
            return;
        }

        if (listaddview.size() == 0) {
            showFailToast("请添加明细");
            return;
        }

        for (int i = 0; i < listaddview.size(); i++) {
            View view3 = listaddview.get(i);
            EditText abnormal_remark = (EditText) view3.findViewById(R.id.abnormal_remark);
            String remark = abnormal_remark.getText().toString().trim();
            if (TextUtils.isEmpty(remark)) {
                showFailToast("异常说明不能为空");
                return;
            }
        }

        HjUpload hjUpload = new HjUpload(this);
        StringBuffer stringBuffer = new StringBuffer();
        if (!Constant.JUDGE_TYPE) {
            stringBuffer.append("{\"tableid\":\"dgtdabn\",\"opr\":\"SS\",\"no\":\"" + mainBean.getDgtdabn_no() + "\",\"userid\":\"" + id_user + "\",\"comid\":\"" + id_com + "\",");
            stringBuffer.append("\"menuid\":\"002095\",\"dealtype\":\"save\",\"data\":[");
        } else {
            stringBuffer.append("[{key:{\"tableid\":\"dgtdabn\",\"opr\":\"SS\",\"no\":\"\",\"userid\":\"" + id_user + "\",\"comid\":\"" + id_com + "\",\"data\":[");
        }
        int gap = 0;
        int cycleTime = 0;
        if (Constant.JUDGE_TYPE) {
            gap = 0;
            cycleTime = listaddview.size();
        } else {
            gap = listaddview.size() - countDetail;
            if (gap >= 0) {
                cycleTime = listaddview.size();
            } else {
                cycleTime = countDetail;
            }
        }
        for (int i = 0; i < cycleTime; i++) {
            if (!Constant.JUDGE_TYPE) {
                if (i >= countDetail) {
                    stringBuffer.append("{\"table\": \"dgtdabn_03\",\"oprdetail\":\"N\",\"where\":\" \",\"data\":[");
                } else if (i >= countDetail + gap) {
                    stringBuffer.append("{\"table\": \"dgtdabn_03\",\"oprdetail\":\"D\",\"where\":\" \",\"data\":[");
                } else {
                    stringBuffer.append("{\"table\": \"dgtdabn_03\",\"oprdetail\":\"U\",\"where\":\" \",\"data\":[");
                }
            } else {
                stringBuffer.append("{\"table\": \"dgtdabn_03\",\"where\":\" \",\"data\":[");
            }
            stringBuffer.append("{\"column\":\"flag_sts\",\"value\":\"L\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"id_flow\",\"value\":\"FBgtd\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"id_dept\",\"value\":\"" + id_dept + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"line_no\",\"value\":\"" + (i + 1) + "\",\"datatype\":\"int\"}, ");
            if (!Constant.JUDGE_TYPE) {
                stringBuffer.append("{\"column\":\"id_recorder\",\"value\":\"" + id_user + "\",\"datatype\":\"varchar\"},");
                stringBuffer.append("{\"column\":\"date_opr\",\"value\":\"" + StringUtil.getTime(Constant.TIME_yyyy_MM_dd_HH_mm_ss) + "\",\"datatype\":\"datetime\"}, ");
                stringBuffer.append("{\"column\":\"flag_psts\",\"value\":\"\",\"datatype\":\"varchar\"},");
                stringBuffer.append("{\"column\":\"id_table\",\"value\":\"\",\"datatype\":\"varchar\"},");

            }
            String date = "";
            String type = "";
            String time = "";
            String remark = "";
            if (i < listaddview.size()) {
                View view3 = listaddview.get(i);
                TextView abnormal_date = (TextView) view3.findViewById(R.id.abnormal_date);
                TextView abnormal_type = (TextView) view3.findViewById(R.id.abnormal_type);
                TextView abnormal_time = (TextView) view3.findViewById(R.id.abnormal_time);
                ClearEditText abnormal_remark = (ClearEditText) view3.findViewById(R.id.abnormal_remark);
                date = abnormal_date.getText().toString().trim();
                type = abnormal_type.getText().toString().trim();
                time = abnormal_time.getText().toString().trim();
                remark = abnormal_remark.getText().toString().trim();
            }
            stringBuffer.append("{\"column\":\"var_date\",\"value\":\"" + date + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"var_off\",\"value\":\"" + time + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"var_on\",\"value\":\"" + type + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"var_remark\",\"value\":\"" + remark + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"id_clerk\",\"value\":\"" + id_clerk + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"date_today\",\"value\":\"" + StringUtil.getTime(Constant.TIME_yyyy_MM_dd) + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"id_linem\",\"value\":\"" + id_linem + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"var_title\",\"value\":\"" + name + "\",\"datatype\":\"varchar\"}]}");
            if (i != cycleTime - 1) {
                stringBuffer.append(",");
            }
        }
        if (!Constant.JUDGE_TYPE) {
            stringBuffer.append("]}");
            String str = stringBuffer.toString();
            LogShow("save" + str);
            getBusinessList(str, "save");
            String save = "\"menuid\":\"002095\",\"dealtype\":\"save\",\"data\":[";
            String send = "\"menuid\":\"002095\",\"dealtype\":\"send\",\"data\":[";
            String finalstring = str.replace(save, send);
            LogShow("send" + finalstring);
            getBusinessList(finalstring, "send");
        } else {
            stringBuffer.append("]},value:\"\"}]");
            String str = stringBuffer.toString();
            LogShow(str);
            try {
                JSONArray jsonArray = new JSONArray(str);
                hjUpload.onhjupload(jsonArray);
                resetAll();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 提交数据
     *
     * @param datas
     */
    private void getBusinessList(String datas, final String dealtype) {
        if (waitDialogRectangle != null && waitDialogRectangle.isShowing()) {
            waitDialogRectangle.dismiss();
        }
        waitDialogRectangle.setCanceledOnTouchOutside(false);
        waitDialogRectangle.show();
        waitDialogRectangle.setText("正在提交");
//        OkGo.post("http://172.16.12.243:8085/hjmerp/servlet/DataUpdateServlet")
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataUpdateServlet")
                .isMultipart(true)
                .params("datas", datas)
                .execute(new BFlagCallBack<businessFlag>() {
                    @Override
                    public void onSuccess(businessFlag businessFlag, Call call, Response response) {
                        String content = businessFlag.getMessage();
                        Constant.billsNo = businessFlag.getNo();
                        if (businessFlag.getFlag().equals("Y")) {
                            if (dealtype.equalsIgnoreCase("send")) {
                                showFailToast(getString(R.string.toast_Message_CommitSucceed));
//                                resetAll();
                                TravelActivityNew.travelActivityNew.refresh();
                                finish();
                            }
                        } else {
                            showFailToast(getString(R.string.toast_Message_CommitFail));
                        }
                        waitDialogRectangle.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (e instanceof OkGoException) {
                            showFailToast("网络错误");
                        } else {
                            showFailToast(e.getMessage());
                        }
                        waitDialogRectangle.dismiss();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void resetAll() {
        detailModelList.clear();
        add_layout_abnormal.removeAllViews();
        listaddview.clear();
        initData();
        initView();
    }

    @Override
    public void GetNDetail(List<AbnormalDetailModel> detailList) {
        //添加前先清除视图
        add_layout_abnormal.removeAllViews();
        //添加视图
        addlayout(detailList);
    }
}

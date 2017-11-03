package com.hjnerp.business.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjnerp.business.BusinessJsonCallBack.BFlagCallBack;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.dao.BusinessBaseDao;
import com.hjnerp.model.Ctlm1345;
import com.hjnerp.model.Ej1345;
import com.hjnerp.model.PerformanceDatas;
import com.hjnerp.model.BusinessFlag;
import com.hjnerp.widget.ClearEditText;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.exception.OkGoException;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 加班申请单
 */
public class OverBusiness extends ActionBarWidgetActivity implements View.OnClickListener {

    private String name_user;
    private String id_user;
    private String name_dept;
    private String id_com;
    private String id_dept;
    private List<Ctlm1345> users;
    private String id_clerk;
    private String id_linem;
    private PerformanceDatas pds;
    private PerformanceDatas.MainBean mainBean;

    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    @BindView(R.id.over_name)
    TextView over_name;
    @BindView(R.id.over_part)
    TextView over_part;
    @BindView(R.id.over_time_begin)
    TextView over_time_begin;
    @BindView(R.id.over_days)
    ClearEditText over_days;
    @BindView(R.id.over_reason)
    ClearEditText over_reason;
    @BindView(R.id.var_rejust_name_over)
    TextView var_rejust_name_over;
    @BindView(R.id.rejust_list_over)
    LinearLayout rejust_list_over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_business);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        actionCenterTv.setText(getString(R.string.over_Title_TvActivity));
        actionLeftTv.setOnClickListener(this);
        actionRightTv.setText(getString(R.string.action_right_content_commit));
        actionRightTv.setOnClickListener(this);
        over_time_begin.setOnClickListener(this);

        if (users.size() == 0) {
            showFailToast("请先下载基础数据");
            finish();
            return;
        }

        over_name.setText(name_user.trim());
        over_part.setText(name_dept.trim());
        if (!Constant.JUDGE_TYPE) {
            rejust_list_over.setVisibility(View.VISIBLE);
            setRejustContext();
        }
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
    }


    private void setRejustContext() {
        pds = Constant.performanceDatas;
        mainBean = pds.getMain();
        var_rejust_name_over.setText(mainBean.getVar_rejust());
        over_time_begin.setText(mainBean.getDate_time());
        over_days.setText(mainBean.getDec_ot());
        over_reason.setText(mainBean.getRemark());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_right_tv:
                submit();
                break;
            case R.id.over_time_begin:
                showCalendar(over_time_begin);
                break;
        }
    }

    private void submit() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String a = f.format(calendar.getTime());
        String name = over_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showFailToast("员工姓名不能为空");
            return;
        }

        String part = over_part.getText().toString().trim();
        if (TextUtils.isEmpty(part)) {
            showFailToast("部门不能为空");
            return;
        }

        String begin = over_time_begin.getText().toString().trim();
        if (TextUtils.isEmpty(begin)) {
            showFailToast("请选择开始加班的日期");
            return;
        }

        String days = over_days.getText().toString().trim();
        if (TextUtils.isEmpty(days)) {
            showFailToast("请输入加班天数");
            return;
        }

        String reason = over_reason.getText().toString().trim();
        if (TextUtils.isEmpty(reason)) {
            showFailToast("请填写加班原因及内容");
            return;
        }

        // TODO validate success, do something

        StringBuffer stringBuffer = new StringBuffer();
        if (!Constant.JUDGE_TYPE) {
            stringBuffer.append("{\"tableid\":\"dgtdot\",\"opr\":\"SS\",\"no\":\"" + mainBean.getDgtdot_no() + "\",\"userid\":\"" + id_user + "\",\"comid\":\"" + id_com + "\",");
            stringBuffer.append("\"menuid\":\"002090\",\"dealtype\":\"save\",\"data\":[");
            stringBuffer.append("{\"table\": \"dgtdot_03\",\"oprdetail\":\"U\",\"where\":\" \",\"data\":[");
            stringBuffer.append("{\"column\":\"id_recorder\",\"value\":\"" + id_user + "\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"date_opr\",\"value\":\"" + a + "\",\"datatype\":\"datetime\"}, ");
            stringBuffer.append("{\"column\":\"flag_psts\",\"value\":\"\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"id_table\",\"value\":\"\",\"datatype\":\"varchar\"},");

        } else {
            stringBuffer.append("[{key:{\"tableid\":\"dgtdot\",\"opr\":\"SS\",\"no\":\"\",\"userid\":\"" + id_user + "\",\"comid\":\"" + id_com + "\",\"data\":[");
            stringBuffer.append("{\"table\": \"dgtdot_03\",\"where\":\" \",\"data\":[");
        }
        stringBuffer.append("{\"column\":\"flag_sts\",\"value\":\"L\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"id_flow\",\"value\":\"FBgtd\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"line_no\",\"value\":\"1\",\"datatype\":\"int\"},");
        stringBuffer.append("{\"column\":\"id_dept\",\"value\":\"" + id_dept + "\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"id_clerk\",\"value\":\"" + id_clerk + "\",\"datatype\":\"varchar\"}, ");
        stringBuffer.append("{\"column\":\"dec_ot\",\"value\":\"" + days + "\",\"datatype\":\"int\"},");
        stringBuffer.append("{\"column\":\"date_time\",\"value\":\"" + begin + "\",\"datatype\":\"datetime\"},");
        stringBuffer.append("{\"column\":\"id_linem\",\"value\":\"" + id_linem + "\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"var_title\",\"value\":\"" + name_user + "\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"remark\",\"value\":\"" + reason + "\",\"datatype\":\"varchar\"}]}]}");
        if (!Constant.JUDGE_TYPE) {
            String str = stringBuffer.toString();
            Log.d("str1", str);
            getBusinessList(str, "save");
            String save = "\"menuid\":\"002090\",\"dealtype\":\"save\",\"data\":[";
            String send = "\"menuid\":\"002090\",\"dealtype\":\"send\",\"data\":[";
            String finalstring = str.replace(save, send);
            Log.d("str2", finalstring);
            getBusinessList(finalstring, "send");
        } else {
            stringBuffer.append(",value:\"\"}]");
            String str = stringBuffer.toString();
            try {
                HjUpload hjUpload = new HjUpload(this);
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
        waitDialogRectangle.show();
        waitDialogRectangle.setText("正在提交");
//        OkGo.post("http://172.16.12.243:8085/hjmerp/servlet/DataUpdateServlet")
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataUpdateServlet")
                .isMultipart(true)
                .params("datas", datas)
                .execute(new BFlagCallBack<BusinessFlag>() {
                    @Override
                    public void onSuccess(BusinessFlag businessFlag, Call call, Response response) {
                        String content = businessFlag.getMessage();
                        Constant.billsNo = businessFlag.getNo();
                        if (businessFlag.getFlag().equals("Y")) {
                            if (dealtype.equalsIgnoreCase("send")) {
                                showFailToast(getString(R.string.toast_Message_CommitSucceed));
//                                resetAll();
                                setResult(22);
                                finish();
                            }
                        } else {
                            showFailToast(getString(R.string.toast_Message_CommitFail) + content);
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

    private void resetAll() {
        over_time_begin.setText("");
        over_days.setText("");
        over_reason.setText("");
    }
}

package com.hjnerp.business.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hjnerp.business.BusinessJsonCallBack.BFlagCallBack;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.dao.BusinessBaseDao;
import com.hjnerp.model.BusinessFlag;
import com.hjnerp.model.Ctlm1345;
import com.hjnerp.model.Ej1345;
import com.hjnerp.model.LeaveType;
import com.hjnerp.model.PerformanceDatas;
import com.hjnerp.net.HttpClientManager;
import com.hjnerp.util.StringUtil;
import com.hjnerp.util.ZipUtils;
import com.hjnerp.util.myscom.FileUtils;
import com.hjnerp.widget.ClearEditText;
import com.hjnerpandroid.R;
import com.lidroid.xutils.util.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.exception.OkGoException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 休假申请
 */
public class LeaveBusiness extends ActionBarWidgetActivity implements View.OnClickListener {
    private String name_user;
    private String id_user;
    private String name_dept;
    private String id_com;
    private String id_dept;
    private List<Ctlm1345> users;
    private String id_clerk;
    private String id_linem;
    private String id_auditlvl;
    private ArrayAdapter<String> stringArrayAdapter;
    //    private List<Ctlm1345> typesAll;
    private List<Ctlm1345> types;
    private List<LeaveType> typevalues;
    private String[] typeNames;
    private String[] typeId;
    private String[] decNo;
    private static final int UPLOAD_INFO = 10000;// 上传结果提示
    private PerformanceDatas pds;
    private PerformanceDatas.MainBean mainBean;
    private int mark_type;

    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    @BindView(R.id.have_days)
    TextView have_days;
    @BindView(R.id.var_rejust_name_leave)
    TextView var_rejust_name_leave;
    @BindView(R.id.rejust_list_leave)
    LinearLayout rejust_list_leave;
    @BindView(R.id.nofocus_leave)
    TextView nofocus_leave;
    @BindView(R.id.leave_name)
    TextView leave_name;
    @BindView(R.id.leave_part)
    TextView leave_part;
    @BindView(R.id.leave_time_begin)
    TextView leave_time_begin;
    @BindView(R.id.leave_time_end)
    TextView leave_time_end;
    @BindView(R.id.leave_type)
    Spinner leave_type;
    @BindView(R.id.leave_days)
    TextView leave_days;
    @BindView(R.id.leave_reason)
    ClearEditText leave_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_business);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initView() {
        actionCenterTv.setText(getString(R.string.travel_Title_TvActivity));
        actionRightTv.setText(getString(R.string.action_right_content_commit));
        actionRightTv.setOnClickListener(this);
        actionLeftTv.setOnClickListener(this);
        leave_time_begin.setOnClickListener(this);
        leave_time_end.setOnClickListener(this);
        if (users.size() == 0) {
            showFailToast("请先下载基础数据");
            finish();
            return;
        }
        leave_name.setText(name_user.trim());
        leave_part.setText(name_dept.trim());
        leave_type.setAdapter(stringArrayAdapter);
        leave_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                have_days.setText(decNo[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (!Constant.JUDGE_TYPE) {
            rejust_list_leave.setVisibility(View.VISIBLE);
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

        id_auditlvl = ej1345.getId_auditlvl();
        types = new ArrayList<>();
        typevalues = new ArrayList<>();
        types = BusinessBaseDao.getCTLM1345ByIdTable("dgtdvat");

        typeNames = new String[types.size()];
        typeNames[0] = "";
        decNo = new String[types.size()];
        decNo[0] = "";
        typeId = new String[types.size()];
        typeId[0] = "";
        for (int i = 0; i < types.size(); i++) {
            String typeValues = types.get(i).getVar_value();
            LeaveType leaveType = mGson.fromJson(typeValues, LeaveType.class);
            typevalues.add(leaveType);
        }
        Collections.sort(typevalues);
        for (int i = 0; i < typevalues.size(); i++) {
            typeNames[i] = typevalues.get(i).getName_atttype();
            typeId[i] = typevalues.get(i).getId_atttype();
            decNo[i] = typevalues.get(i).getDec_no();
        }

        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, typeNames);
        stringArrayAdapter.setDropDownViewResource(R.layout.spinner_item_hint);
    }

    private void setRejustContext() {
        pds = Constant.performanceDatas;
        mainBean = pds.getMain();
        var_rejust_name_leave.setText(mainBean.getVar_rejust());
        leave_time_begin.setText(mainBean.getDate_begin());
        leave_time_end.setText(mainBean.getDate_end());
        leave_days.setText(mainBean.getDec_leave());
        leave_reason.setText(mainBean.getVar_remark());
        for (int i = 0; i < typeId.length; i++) {
            if (typeId[i].equalsIgnoreCase(mainBean.getId_atttype())) {
                leave_type.setSelection(i, true);
                mark_type = i;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_right_tv:
                submit();
                break;
            case R.id.leave_time_begin:
                showCalendar(leave_time_begin);
                break;
            case R.id.leave_time_end:
                showCalendar(leave_time_end);
                break;
            case R.id.action_left_tv:
                finish();
                break;
        }
    }


    private void submit() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String a = f.format(calendar.getTime());
        String name = leave_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showFailToast("休假人员不能为空");
            return;
        }

        String part = leave_part.getText().toString().trim();
        if (TextUtils.isEmpty(part)) {
            showFailToast("部门不能为空");
            return;
        }

        String begin = leave_time_begin.getText().toString().trim();
        if (TextUtils.isEmpty(begin)) {
            showFailToast("请选择开始休假的日期");
            return;
        }

        String end = leave_time_end.getText().toString().trim();
        if (TextUtils.isEmpty(end)) {
            showFailToast("请选择结束休假的日期");
            return;
        }
        String id_type_leave = typeId[leave_type.getSelectedItemPosition()];
        String type_leave = typeNames[leave_type.getSelectedItemPosition()];
        String dec_no = decNo[leave_type.getSelectedItemPosition()];
        if (TextUtils.isEmpty(id_type_leave)) {
            showFailToast("请选择休假类型");
            return;
        }

        String days = leave_days.getText().toString().trim();
        if (TextUtils.isEmpty(days)) {
            showFailToast("请输入休假天数");
            return;
        }

        String reason = leave_reason.getText().toString().trim();
        HjUpload hjUpload = new HjUpload(this);
        try {
            if (hjUpload.compare(end, begin)) {
                showFailToast("开始时间不能大于结束时间。");
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // TODO validate success, do something
        StringBuffer stringBuffer = new StringBuffer();
        if (!Constant.JUDGE_TYPE) {
            stringBuffer.append("{\"tableid\":\"dgtdvat\",\"opr\":\"SS\",\"no\":\"" + mainBean.getDgtdvat_no() + "\",\"userid\":\"" + id_user + "\",\"comid\":\"" + id_com + "\",");
            stringBuffer.append("\"menuid\":\"002040\",\"dealtype\":\"save\",\"data\":[");
            stringBuffer.append("{\"table\": \"dgtdvat_03\",\"oprdetail\":\"U\",\"where\":\" \",\"data\":[");
            stringBuffer.append("{\"column\":\"id_recorder\",\"value\":\"" + id_user + "\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"date_opr\",\"value\":\"" + a + "\",\"datatype\":\"datetime\"}, ");
            stringBuffer.append("{\"column\":\"flag_psts\",\"value\":\"\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"id_table\",\"value\":\"\",\"datatype\":\"varchar\"},");

        } else {
            stringBuffer.append("[{key:{\"tableid\":\"dgtdvat\",\"opr\":\"SS\",\"no\":\"\",\"userid\":\"" + id_user + "\",\"comid\":\"" + id_com + "\",\"data\":[");
            stringBuffer.append("{\"table\": \"dgtdvat_03\",\"where\":\" \",\"data\":[");
        }

        stringBuffer.append("{\"column\":\"flag_sts\",\"value\":\"L\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"id_flow\",\"value\":\"FBgtd\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"line_no\",\"value\":\"1\",\"datatype\":\"int\"},");
        stringBuffer.append("{\"column\":\"id_dept\",\"value\":\"" + id_dept + "\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"dec_no\",\"value\":\"" + dec_no + "\",\"datatype\":\"int\"},");
        stringBuffer.append("{\"column\":\"id_clerk\",\"value\":\"" + id_clerk + "\",\"datatype\":\"varchar\"}, ");
        stringBuffer.append("{\"column\":\"id_atttype\",\"value\":\"" + id_type_leave + "\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"dec_leave\",\"value\":\"" + days + "\",\"datatype\":\"int\"},");
        stringBuffer.append("{\"column\":\"date_begin\",\"value\":\"" + begin + "\",\"datatype\":\"datetime\"},");
        stringBuffer.append("{\"column\":\"date_end\",\"value\":\"" + end + "\",\"datatype\":\"datetime\"},");
        stringBuffer.append("{\"column\":\"id_linem\",\"value\":\"" + id_linem + "\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"id_auditlvl\",\"value\":\"" + id_auditlvl + "\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"var_title\",\"value\":\"" + name_user + " " + type_leave + "\",\"datatype\":\"varchar\"},");
        stringBuffer.append("{\"column\":\"var_remark\",\"value\":\"" + reason + "\",\"datatype\":\"varchar\"}]}]}");

        String str = "";
        if (!Constant.JUDGE_TYPE) {
            str = stringBuffer.toString();
            Log.d("str1", str);
            getBusinessList(str, "save");
            String save = "\"menuid\":\"002040\",\"dealtype\":\"save\",\"data\":[";
            String send = "\"menuid\":\"002040\",\"dealtype\":\"send\",\"data\":[";
            String finalstring = str.replace(save, send);
            getBusinessList(finalstring, "send");
        } else {
            stringBuffer.append(",value:\"\"}]");
            str = stringBuffer.toString();
            LogShow("休假申请： " + str);
            try {
                JSONArray jsonArray = new JSONArray(str);
                onhjuploada(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void onhjuploada(JSONArray args) {
//        int resultint = 0;
        // 上传的代码
        waitDialogRectangle.show();
        waitDialogRectangle.setText("正在提交");
        JSONObject obj = args.optJSONObject(0);
        if (obj != null) {
            try {
                List<File> files = new ArrayList<>();
                String json = obj.getString("key");
                String file_list = obj.getString("value");
                String uuid = StringUtil.getMyUUID();
                File file = new File(Constant.TEMP_DIR, uuid + ".txt");
                if (!"".equals(file_list)) {
                    for (String name : file_list.split(",")) {
                        files.add(new File("file://" + Constant.HJPHOTO_CACHE_DIR + name));
                    }
                }
                files.add(file);
                try {
                    FileUtils.writeStringToFile(file, "business" + json, "utf-8");
                    File f = new File(Constant.TEMP_DIR + uuid + ".zip");
                    ZipUtils.zipFiles(files, f);
                    sendZipFile(Constant.TEMP_DIR + uuid + ".zip");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // for (Ctlm1347 ctlm1347 : ctlm1345ListFromCtlm1347) {
        // if (CHECKED.equals(ctlm1347.getFlag_upload())) {
        // billNoList.add(ctlm1347.getVar_billno());
        // idnodeList.add(ctlm1347.getId_node());
        // }
        // }
        // BusinessLua.uploadData(billNoList);
    }

    /**
     * @param name
     */
    private void sendZipFile(String name) {
//        final int[] resultint = {0};
        MultipartEntity entity = new MultipartEntity();
        entity.addPart("download", new FileBody(new File(name)));
        HttpPost httpPost = new HttpPost(EapApplication.URL_SERVER_HOST_HTTP
                + Constant.BUSINESS_SERVICE_ADDRESS);

        LogShow("上传服务地址：" + EapApplication.URL_SERVER_HOST_HTTP
                + Constant.BUSINESS_SERVICE_ADDRESS);

        httpPost.setEntity(entity);
        HttpClientManager.addTask(new HttpClientManager.HttpResponseHandler() {

            @Override
            public void onResponse(HttpResponse resp) {
                try {
                    String msga = HttpClientManager.toStringContent(resp);

                    try {
                        JSONObject jsonObject = new JSONObject(msga);
                        String result = jsonObject.getString("flag");
                        LogShow("上传返回的数据:" + jsonObject.toString());
                        if ("ok".equalsIgnoreCase(result)) {
                            // 上传成功
//                            ToastUtil.ShowShort(context, "上传成功：" + result);
                            setHandlerInfoNoDialog(UPLOAD_INFO, "提交成功");
//                            resetAll();
                        } else {
                            String message = jsonObject.getString("message");
                            if (message.contains("$")) {
                                int index = message.indexOf("$");
                                String messageSbf = message.substring(0, index);
                                // 上传失败
                                Log.d(context.getPackageName(), "上传失败：" + result
                                        + " 消息：" + messageSbf);
                                setHandlerInfoNoDialog(UPLOAD_INFO, "提交失败:" + messageSbf);

                            } else {
                                // 上传失败
                                Log.d(context.getPackageName(), "上传失败：" + result
                                        + " 消息：" + message);
                                setHandlerInfoNoDialog(UPLOAD_INFO, "提交失败：" + message);

                            }

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Log.d(context.getPackageName(), "上传失败：" + e.toString());
                        setHandlerInfoNoDialog(UPLOAD_INFO, "提交失败：" + e.toString());

                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d(context.getPackageName(), "上传失败：" + e.toString());
                    setHandlerInfoNoDialog(UPLOAD_INFO, "提交失败：" + e.toString());

                }

            }

            @Override
            public void onException(Exception e) {
                // 上传失败
                Log.d(context.getPackageName(), "上传失败：" + e.toString());
                setHandlerInfoNoDialog(UPLOAD_INFO, "提交失败：" + e.toString());

            }
        }, httpPost);
    }

    private void setHandlerInfoNoDialog(int sginType, Object content) {
        Message msg = new Message();
        msg.what = sginType;
        msg.obj = content;
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_INFO:
                    String info = (String) msg.obj;
                    if (StringUtil.isStrTrue(info)) {
                        showFailToast(info);
                        waitDialogRectangle.dismiss();
                    }
                    if (info.equalsIgnoreCase("提交成功")) {
                        resetAll();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 提交数据
     *
     * @param datas
     */
    private void getBusinessList(String datas, final String dealtype) {

        LogShow(dealtype + "休假申请 "+datas);

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


    private void resetAll() {
        leave_time_begin.setText("");
        leave_time_end.setText("");
        leave_type.setSelection(0);
        leave_days.setText("");
        leave_reason.setText("");
        users.clear();
        types.clear();
        typevalues.clear();
        initData();
        initView();
    }
}

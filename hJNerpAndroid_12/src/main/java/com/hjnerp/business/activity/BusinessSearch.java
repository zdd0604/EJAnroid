package com.hjnerp.business.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.hjnerp.business.BusinessAdapter.BusinessSearchAdapter;
import com.hjnerp.business.BusinessQueryDao.BusinessQueryDao;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.model.Ctlm7502Json;
import com.hjnerp.model.EjMyWProj1345;
import com.hjnerp.net.HttpClientBuilder;
import com.hjnerp.net.HttpClientManager;
import com.hjnerp.util.myscom.StringUtils;
import com.hjnerp.widget.ClearEditText;
import com.hjnerpandroid.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusinessSearch extends ActionBarWidgetActivity implements View.OnClickListener,
        ActionBarWidgetActivity.NsyncDataConnector,
        BusinessSearchAdapter.OnItemClickLitener{
    @BindView(R.id.project_search)
    ClearEditText project_search;
    @BindView(R.id.project_recy)
    RecyclerView project_recy;
    @BindView(R.id.secah_error)
    LinearLayout secah_error;
    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_right_tv1)
    TextView actionRightTv1;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;

    private BusinessSearchAdapter adapter;
    public static final Pattern p = Pattern.compile("\\s*|\t|\r|\n");
    private List<Ctlm7502Json> travelDatas = new ArrayList<>();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String content = (String) msg.obj;
            switch (msg.what) {
                case 0:
                    getWProject(content, 0);
                    break;
                case 1:
                    getWProject(content, 1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_seach);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ActionBarWidgetActivity.setNsyncDataConnector(this);
        BusinessSearchAdapter.setOnItemClickLitener(this);
        project_search.addTextChangedListener(textWatcher);
        actionCenterTv.setText(getString(R.string.search_Title_TvActivity));
        actionRightTv.setVisibility(View.GONE);
        actionLeftTv.setOnClickListener(this);
        project_recy.setLayoutManager(new LinearLayoutManager(this));
        if (Constant.travel) {
            if (travelDatas.size() > 0) {
                setHandlerMsg(1, project_search.getText().toString());
            } else {
                readFrom7502();
            }
        } else {
            setHandlerMsg(0, project_search.getText().toString());
        }
    }


    /**
     * 查询7502表
     */
    private void readFrom7502() {
        waitDialog.show();
        try {
            HttpClientBuilder.HttpClientParam param = HttpClientBuilder
                    .createParam(Constant.NBUSINESS_SERVICE_ADDRESS);
            param.addKeyValue(Constant.BM_ACTION_TYPE, "MobileSyncDataDownload")
                    .addKeyValue("id_table", StringUtils.join("ctlm7502_corr"))
                    .addKeyValue("condition", "1=1");
            HttpClientManager.addTask(responseHandler, param.getHttpPost());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜索监听输入框
     */
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (Constant.travel) {
                if (travelDatas != null || travelDatas.size() > 0) {
                    setHandlerMsg(1, s.toString());
                } else {
                    readFrom7502();
                }
            } else {
                setHandlerMsg(0, s.toString());
            }

        }
    };


    public void getWProject(String content, int i) {
        List<EjMyWProj1345> list = null;
        if (i == 0) {
            list = BusinessQueryDao.getMyProj(content, Constant.id_wtype, Constant.MYUSERINFO.userID);
        } else if (i == 1) {
            list = getProject(content);
        }
        if (list.size() > 0) {
            project_recy.setVisibility(View.VISIBLE);
            secah_error.setVisibility(View.GONE);
            adapter = new BusinessSearchAdapter(this, list);
            adapter.notifyDataSetChanged();
            project_recy.setAdapter(adapter);
        } else {
            project_recy.setVisibility(View.GONE);
            secah_error.setVisibility(View.VISIBLE);
        }

    }

    private List<EjMyWProj1345> getProject(String content) {
        List<EjMyWProj1345> list = new ArrayList<>();
        for (int i = 0; i < travelDatas.size(); i++) {
            if (content.isEmpty() || travelDatas.get(i).getName_proj().contains(content) ||
                    travelDatas.get(i).getName_corr().contains(content)) {
                EjMyWProj1345 ejMyWProj1345 = new EjMyWProj1345();
                ejMyWProj1345.setName_wproj(travelDatas.get(i).getName_proj().trim());
                ejMyWProj1345.setId_wproj(travelDatas.get(i).getId_proj().trim());
                ejMyWProj1345.setName_corr(travelDatas.get(i).getName_corr().trim());
                ejMyWProj1345.setId_corr(travelDatas.get(i).getId_corr().trim());
                list.add(ejMyWProj1345);
            }
        }
        waitDialog.dismiss();
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_left_tv:
                finish();
                break;
        }
    }

    @Override
    public void processJsonValue(String value) {
        try {
            if (value.equalsIgnoreCase("[]") || value.equalsIgnoreCase(null)) {
                return;
            }
            JSONArray jsonArray = new JSONArray(value);
            for (int i = 0; i < jsonArray.length(); i++) {
                String temp = jsonArray.getString(i);
                Matcher m = p.matcher(temp);
                String subValue = temp.substring(temp.indexOf("{"),
                        temp.indexOf("}") + 1);
                Ctlm7502Json ctlm7502Json = mGson.fromJson(subValue, Ctlm7502Json.class);
                travelDatas.add(ctlm7502Json);
            }
            if (travelDatas != null || travelDatas.size() > 0) {
                setHandlerMsg(1, project_search.getText().toString());
            } else {
                readFrom7502();
            }
        } catch (JSONException e) {
            LogShow("搜索解析异常");
        }
    }

    private void setHandlerMsg(int numb, Object o) {
        Message message = new Message();
        message.what = numb;
        message.obj = o;
        mHandler.sendMessage(message);
    }

    @Override
    public void onItemClick(String item_peoject, String item_client, String id_wproj, String id_corr, int position) {
        Constant.item_peoject = item_peoject;
        Constant.item_client = item_client;
        Constant.id_wproj = id_wproj;
        Constant.id_corr = id_corr;
        setResult(22);
        finish();
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}

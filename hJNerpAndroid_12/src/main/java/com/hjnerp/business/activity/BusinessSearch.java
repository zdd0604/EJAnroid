package com.hjnerp.business.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.hjnerp.business.BusinessAdapter.BusinessSearchAdapter;
import com.hjnerp.business.BusinessQueryDao.BusinessQueryDao;
import com.hjnerp.common.ActivitySupport;
import com.hjnerp.common.Constant;
import com.hjnerp.model.Ctlm7502Json;
import com.hjnerp.model.EjMyWProj1345;
import com.hjnerp.net.HttpClientBuilder;
import com.hjnerp.net.HttpClientManager;
import com.hjnerp.util.myscom.StringUtils;
import com.hjnerp.widget.ClearEditText;
import com.hjnerp.widget.WaitDialogRectangle;
import com.hjnerpandroid.R;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BusinessSearch extends ActivitySupport implements View.OnClickListener {
    private ClearEditText project_search;
    private RecyclerView project_recy;
    private CharSequence temp;//监听前的文本
    private BusinessSearchAdapter adapter;
    private Context mContext;
    private LinearLayout secah_error;
    private HttpClientManager.HttpResponseHandler responseHandler = new NsyncDataHandler();
    public static final String JSON_VALUE = "values";
    public static final Pattern p = Pattern.compile("\\s*|\t|\r|\n");
    private List<Ctlm7502Json> travelDatas = new ArrayList<>();
    private WaitDialogRectangle waitDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getSupportActionBar();
        setContentView(R.layout.activity_business_seach);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("项目搜索");
        mContext = this;
        initView();
    }

    private void initView() {
        project_search = (ClearEditText) findViewById(R.id.project_search);
        project_search.addTextChangedListener(textWatcher);
        project_recy = (RecyclerView) findViewById(R.id.project_recy);
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

        secah_error = (LinearLayout) findViewById(R.id.secah_error);
    }

    private void readFrom7502() {
        waitDialog = new WaitDialogRectangle(this);
        waitDialog.show();
        try {
            HttpClientBuilder.HttpClientParam param = HttpClientBuilder
                    .createParam(Constant.NBUSINESS_SERVICE_ADDRESS);
            param.addKeyValue(Constant.BM_ACTION_TYPE, "MobileSyncDataDownload")
                    .addKeyValue("id_table", StringUtils.join("ctlm7502_corr"))
                    .addKeyValue("condition", "1=1");
            HttpClientManager.addTask(responseHandler, param.getHttpPost());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
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

    private void setHandlerMsg(int numb, Object o) {
        Message message = new Message();
        message.what = numb;
        message.obj = o;
        mHandler.sendMessage(message);
    }

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
            adapter.setOnItemClickLitener(new BusinessSearchAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(String item_peoject, String item_client, String id_wproj, String id_corr, int position) {
                    Constant.item_peoject = item_peoject;
                    Constant.item_client = item_client;
                    Constant.id_wproj = id_wproj;
                    Constant.id_corr = id_corr;
                    setResult(22);
//                ToastUtil.ShowLong(mContext, item_peoject + "..." + item_client);
                    finish();
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        } else {
            project_recy.setVisibility(View.GONE);
            secah_error.setVisibility(View.VISIBLE);
        }

    }

    private List<EjMyWProj1345> getProject(String content) {

        List<EjMyWProj1345> list = new ArrayList<>();
        for (int i = 0; i < travelDatas.size(); i++) {
            if (content.isEmpty() || travelDatas.get(i).getName_proj().contains(content) || travelDatas.get(i).getName_corr().contains(content)) {
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

    private class NsyncDataHandler extends HttpClientManager.HttpResponseHandler {

        @Override
        public void onException(Exception e) {

        }

        @Override
        public void onResponse(HttpResponse resp) {
            // TODO Auto-generated method stub
            try {
                String contentType = resp.getHeaders("Content-Type")[0]
                        .getValue();
                // if ("application/octet-stream".equals(contentType) ) {
                if (contentType.indexOf("application/octet-stream") != -1) {
                    String contentDiscreption = resp
                            .getHeaders("Content-Disposition")[0].getValue();
                    String fileName = contentDiscreption
                            .substring(contentDiscreption.indexOf("=") + 1);
                    FileOutputStream fos = new FileOutputStream(new File(
                            getExternalCacheDir(), fileName));
                    resp.getEntity().writeTo(fos);
                    fos.close();
                    String json = processBusinessCompress(fileName);
                    JSONObject jsonObject = new JSONObject(json);
                    String value = jsonObject.getString(JSON_VALUE);

                    Log.d("value", value);
                    processJsonValue(value);
                } else {

                }

            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void processJsonValue(String value) throws JSONException {
        // TODO Auto-generated method stub

        if (value.equalsIgnoreCase("[]") || value.equalsIgnoreCase(null)) {
            return;
        }
        JSONArray jsonArray = new JSONArray(value);

        for (int i = 0; i < jsonArray.length(); i++) {
            String temp = jsonArray.getString(i);
            Matcher m = p.matcher(temp);
            String subValue = temp.substring(temp.indexOf("{"),
                    temp.indexOf("}") + 1);
            Gson gson = new Gson();
            Ctlm7502Json ctlm7502Json = gson.fromJson(subValue, Ctlm7502Json.class);
            travelDatas.add(ctlm7502Json);

        }
        if (travelDatas != null || travelDatas.size() > 0) {
            setHandlerMsg(1, project_search.getText().toString());

        } else {
            readFrom7502();
        }
    }

    private String processBusinessCompress(String fileName) {
        // TODO Auto-generated method stub
        ZipInputStream zis = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            File f = new File(getExternalCacheDir(), fileName);
            FileInputStream fis = new FileInputStream(f);
            zis = new ZipInputStream(fis);
            ZipEntry zip = zis.getNextEntry();
            int len = 0;
            while ((len = zis.read(data)) != -1) {
                baos.write(data, 0, len);
            }
            String json = new String(baos.toByteArray(), HTTP.UTF_8);
            return json;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    public void onClick(View v) {

    }
}

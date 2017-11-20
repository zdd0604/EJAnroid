package com.hjnerp.business.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hjnerp.activity.MainActivity;
import com.hjnerp.adapter.BusinessBillsAdapter;
import com.hjnerp.business.BusinessJsonCallBack.BJsonCallBack;
import com.hjnerp.business.businessutils.DateUtil;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.model.BusinessBillsMessages;
import com.hjnerp.model.PerformanceDatas;
import com.hjnerp.util.StringUtil;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class BusinessPerformanceArrayList extends ActionBarWidgetActivity implements View.OnClickListener {
    //布局
    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_right_tv1)
    TextView actionRightTv1;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    @BindView(R.id.pull_refresh_billlist)
    PullToRefreshListView pull_refresh_billlist;
    private List<PerformanceDatas> datas = new ArrayList<>();
    private BusinessBillsAdapter billsAdapter;
    private int index;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.HANDLERTYPE_0:
                    setViewData();
                    break;
                case Constant.HANDLERTYPE_1:
                    String content = (String) msg.obj;
                    showFailToast(content);
                    waitDialog.dismiss();
                    datas.clear();
                    refreshList();
                    break;
                case Constant.HANDLERTYPE_2:
                    datas.remove(index);
                    refreshList();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_performance_array_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        actionCenterTv.setText(getString(R.string.perf_Title_TvActivity));
        actionRightTv.setText(getString(R.string.action_right_content_add));
        actionLeftTv.setOnClickListener(this);
        actionRightTv.setOnClickListener(this);
        billsAdapter = new BusinessBillsAdapter(this, datas);
        pull_refresh_billlist.setAdapter(billsAdapter);
        billsAdapter.notifyDataSetChanged();
        // pull_refresh_bills.setMode(Mode.BOTH);//上下拉刷新
        pull_refresh_billlist.setMode(PullToRefreshBase.Mode.PULL_FROM_START);// 仅下拉刷新
        // //上下拉刷新
        pull_refresh_billlist.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                addListViewData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
            }
        });

        //请求网络
        addListViewData();
    }

    private void setViewData() {
        Collections.sort(datas, new Comparator<PerformanceDatas>() {
            @Override
            public int compare(PerformanceDatas lhs, PerformanceDatas rhs) {
                Date date1 = DateUtil.stringToDate(lhs.getMain().getDate_opr());
                Date date2 = DateUtil.stringToDate(rhs.getMain().getDate_opr());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;
            }
        });
        for (PerformanceDatas pd : datas) {
            if (pd.getMain() != null) {
                pull_refresh_billlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Constant.performanceDatas = getPerformanceDatas(position - 1);
                        Constant.JUDGE_TYPE = false;
                        intentActivity(BusinessPerformanceInput.class, 11);
                    }
                });
                refreshList();
                waitDialog.dismiss();
            } else {
                sendMessage(Constant.HANDLERTYPE_1, getString(R.string.toast_Message_DataNull));
            }
        }
    }

    /**
     * 刷新listview中的数据
     */
    private void refreshList() {
        billsAdapter.refreshList(datas);
        pull_refresh_billlist.onRefreshComplete();
    }

    // 获得WorkInfo
    private PerformanceDatas getPerformanceDatas(int position) {
        PerformanceDatas info = null;
        index = position;
        info = datas.get(position);
        return info;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // //界面返回值
        /**
         * @author haijian
         * 收到返回的值判断是否成功，如果同意就将数据移除刷新列表
         */
        if (requestCode == 11 && resultCode == 22) {
            handler.sendEmptyMessage(2);
            MainActivity.WORK_COUNT = MainActivity.WORK_COUNT - 1;
        }
    }

    /**
     * 从网络加载数据
     */
    private void addListViewData() {
        // 如果有网络加载网络数据，并在加载成功后删除之前保留的本地数据，没有网络加载本地数据
        if (!hasInternetConnected()) {
            //结束刷新动画
            pull_refresh_billlist.onRefreshComplete();
            return;
        }

        if (StringUtil.isStrTrue(Constant.ID_MENU)) {
            getBusinessList(Constant.ID_MENU, Constant.ej1345.getId_user());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_right_tv:
                intentActivity(BusinessPerformanceTypeIn.class);
                Constant.JUDGE_TYPE = true;
                break;
            case R.id.action_left_tv:
                finish();
                break;
        }
    }

    /**
     * 获取数据
     *
     * @param idmenu
     * @param iduser
     */
    private void getBusinessList(String idmenu, String iduser) {
        waitDialog.show();
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataQueryServlet")
                .params("idmenu", idmenu)
                .params("iduser", iduser)
                .execute(new BJsonCallBack<BusinessBillsMessages>() {
                    @Override
                    public void onSuccess(BusinessBillsMessages businessBillsMessages, Call call, Response response) {
                        datas = businessBillsMessages.getDatas();
                        handler.sendEmptyMessage(Constant.HANDLERTYPE_0);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        sendMessage(Constant.HANDLERTYPE_1, getString(R.string.toast_Message_GetDataFail));
                    }
                });
    }

    private void sendMessage(int tag, Object content) {
        Message message = new Message();
        message.what = tag;
        message.obj = content;
        handler.sendMessage(message);
    }
}

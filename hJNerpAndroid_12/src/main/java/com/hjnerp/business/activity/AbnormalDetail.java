package com.hjnerp.business.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.dao.BusinessBaseDao;
import com.hjnerp.model.AbnormalDetailModel;
import com.hjnerp.model.Ctlm1345;
import com.hjnerpandroid.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AbnormalDetail extends ActionBarWidgetActivity implements View.OnClickListener {

    private List<AbnormalDetailModel> data;
    private List<AbnormalDetailModel> sureData;
    private List<Ctlm1345> abnormals;
    private MyListAdapter adapter;
    //是否全选
    private boolean isALL = false;

    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    @BindView(R.id.action_right_tv1)
    TextView actionRightTv1;
    @BindView(R.id.abnormal_list_view)
    ListView abnormal_list_view;

    public static AbNormalDetailListener abListener;

    public static void setAbListener(AbNormalDetailListener abListener) {
        AbnormalDetail.abListener = abListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abnormal_detail);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        data = new ArrayList<>();
        abnormals = new ArrayList<>();
        abnormals = BusinessBaseDao.getCTLM1345ByIdTable("dgtdabn");
        if (abnormals.size() == 0) {
            showFailToast("没有考勤异常记录");
            finish();
            return;
        }
        for (int i = 0; i < abnormals.size(); i++) {
            String abstrings = abnormals.get(i).getVar_value();
            AbnormalDetailModel abnormalDetailModel = mGson.fromJson(abstrings, AbnormalDetailModel.class);
            abnormalDetailModel.setSelect(false);
            data.add(abnormalDetailModel);
        }
    }

    private void initView() {
        actionCenterTv.setText(getString(R.string.detail_Title_NormalDetail));
        actionRightTv.setText(getString(R.string.action_right_content_sure));
        actionRightTv1.setVisibility(View.VISIBLE);
        actionRightTv1.setText(getString(R.string.action_right_content_SelectAll));
        actionLeftTv.setOnClickListener(this);
        actionRightTv.setOnClickListener(this);
        actionRightTv1.setOnClickListener(this);

        adapter = new MyListAdapter(data);
        abnormal_list_view.setAdapter(adapter);

        sureData = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_right_tv:
                submit();
                break;
            case R.id.action_right_tv1:
                selecAll();
                break;
            case R.id.action_left_tv:
                finish();
                break;
        }
    }

    /**
     * 全选按钮
     */
    private void selecAll() {
        if (isALL) {
            isALL = false;
            selectAll(isALL);
        } else {
            isALL = true;
            selectAll(isALL);
        }
    }

    /**
     * 全选以及取消
     *
     * @param isck
     */
    private void selectAll(boolean isck) {
        waitDialogRectangle.show();
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setSelect(isck);
        }
        adapter.notifyDataSetChanged();
        waitDialogRectangle.dismiss();
    }


    /**
     * 提交
     */
    private void submit() {
        sureData.clear();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getSelect())
                sureData.add(data.get(i));
        }

        if (sureData.size() == 0) {
            showFailToast("请选择数据");
            return;
        }

        if (abListener == null) {
            showFailToast("未设置回调");
            return;
        }

        abListener.GetNDetail(sureData);

        finish();
    }


    private class MyListAdapter extends BaseAdapter {

        List<AbnormalDetailModel> listAbnormal;

        public MyListAdapter(List<AbnormalDetailModel> listAbnormal) {
            this.listAbnormal = listAbnormal;
        }

        @Override
        public int getCount() {
            return listAbnormal.size();
        }

        @Override
        public AbnormalDetailModel getItem(int position) {
            return listAbnormal.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.abnormal_detail_item, null);
                holder.date = (TextView) convertView.findViewById(R.id.date_abnormal_item);
                holder.type = (TextView) convertView.findViewById(R.id.type_abnormal_item);
                holder.onoff = (TextView) convertView.findViewById(R.id.startoff_abnormal_item);
                holder.time = (TextView) convertView.findViewById(R.id.time_abnormal_item);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.abnormal_detail_checkbox);
                holder.layout = (LinearLayout) convertView.findViewById(R.id.abnormal_detail_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.date.setText("日期：" + listAbnormal.get(position).getVar_date());

            switch (listAbnormal.get(position).getId_schtype()) {
                case "A":
                    holder.type.setText("类型：管理班");
                    break;
                case "B":
                    holder.type.setText("类型：正常班");
                    break;
                case "C":
                    holder.type.setText("类型：休息");
                    break;
                case "D":
                    holder.type.setText("类型：加班");
                    break;
                case "E":
                    holder.type.setText("类型：特殊班（保洁）");
                    break;
                case "F":
                    holder.type.setText("类型：特殊班（哺乳期)");
                    break;
                case "G":
                    holder.type.setText("类型：特殊班");
                    break;
                case "H":
                    holder.type.setText("类型：正常班(弹性)");
                    break;
            }
            holder.onoff.setText("上/下班：" + listAbnormal.get(position).getVar_on());
            holder.time.setText("时间：" + listAbnormal.get(position).getVar_off());
            holder.checkBox.setChecked(listAbnormal.get(position).getSelect());
            final ViewHolder finalHolder = holder;
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalHolder.checkBox.isChecked()) {
                        finalHolder.checkBox.setChecked(false);
                    } else {
                        finalHolder.checkBox.setChecked(true);
                    }

                    data.get(position).setSelect(finalHolder.checkBox.isChecked());
                }
            });
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalHolder.checkBox.isChecked()) {
                        finalHolder.checkBox.setChecked(false);
                    } else {
                        finalHolder.checkBox.setChecked(true);
                    }
                    data.get(position).setSelect(finalHolder.checkBox.isChecked());
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView date;
            TextView type;
            TextView onoff;
            TextView time;
            CheckBox checkBox;
            LinearLayout layout;
        }
    }

    public interface AbNormalDetailListener {
        void GetNDetail(List<AbnormalDetailModel> detailList);
    }
}

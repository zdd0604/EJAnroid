package com.hjnerp.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjnerp.activity.SetActivity;
import com.hjnerp.adapter.BusinessGridViewAdapter;
import com.hjnerp.business.BusinessQueryDao.BusinessQueryDao;
import com.hjnerp.business.Ctlm1346Update;
import com.hjnerp.business.activity.BusinessActivity;
import com.hjnerp.business.activity.BusinessBillsActivity;
import com.hjnerp.business.activity.BusinessDdisplocathActivity;
import com.hjnerp.business.activity.BusinessDgtdrechtml;
import com.hjnerp.business.activity.BusinessEJLocation;
import com.hjnerp.business.activity.BusinessPerformanceArrayList;
import com.hjnerp.business.activity.BussinessHtmlActivity;
import com.hjnerp.business.activity.TravelActivityNew;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.dao.BusinessBaseDao;
import com.hjnerp.db.SQLiteWorker;
import com.hjnerp.model.BusinessMenuResp;
import com.hjnerp.model.BusinessTableCreateModel;
import com.hjnerp.model.MenuContent;
import com.hjnerp.model.NBusinessTableCreateModel;
import com.hjnerp.net.HttpClientBuilder;
import com.hjnerp.net.HttpClientManager;
import com.hjnerp.net.HttpClientManager.HttpResponseHandler;
import com.hjnerp.util.Command;
import com.hjnerp.util.Log;
import com.hjnerp.widget.MyToast;
import com.hjnerp.widget.WaitDialogRectangle;
import com.hjnerpandroid.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@SuppressLint("ResourceAsColor")
public class BusinessFragment<Divider> extends Fragment {
    private static final String TAG = BusinessFragment.class.getSimpleName();

    private MyGridView gridView1;
    private MyGridView gridView2;
    private MyGridView gridView3;
    private MyGridView gridView4;
    private ArrayList<MenuContent> listCurrent;
    private ArrayList<MenuContent> listCurrent1;
    private ArrayList<MenuContent> listCurrent2;
    private ArrayList<MenuContent> listCurrent3;
    private ArrayList<MenuContent> listCurrent4;
    //	private BusinessExpandableListAdapter adapter;
    private BusinessGridViewAdapter adapter1;
    private BusinessGridViewAdapter adapter2;
    private BusinessGridViewAdapter adapter3;
    private BusinessGridViewAdapter adapter4;
    private Dialog noticeDialog;// 提示更新数据
    private MyHandler<Divider> myHandler;
    private static String clicked_id_model;// 用户想要进入的业务类型（用户点击了哪一个菜单）
    private static String clicked_xml_version;// 用户想要进入的业务类型的xml模板本地版本（用户点击的菜单的xml模板版本）
    private static String clicked_xml_nameModel;// 用户想要进入的业务类型的xml模板名称
    private static String CHECT_XML_EXIT = "check_xml_exit";
    private static String CHECT_XML_OK = "check_xml_ok";
    private static String CHECT_XML_OLD = "check_xml_OLD";
    private static String DOWNLOAD_XML_OK = "download_xml_ok";
    private static String DOWNLOAD_XML_ERROR = "download_xml_error";
    private static MenuContent menuContent;// 用户点击的菜单
    private static Context context;
    private Thread mThread;
    public static boolean isPoPoVisible;
    public static BusinessFragment businessFragment = null;
    private WaitDialogRectangle waitDialogRectangle;
    private LinearLayout visit_layout;
    private LinearLayout bus_error;
    private LinearLayout other_layout;
    private LinearLayout find_layout;
    private LinearLayout performance_layout;
    private static Intent intent = null;
    private BusinessGridViewAdapter adapter;

    private class MyHandler<Divider> extends Handler {
        ArrayList<MenuContent> listCurrent;
        BusinessFragment<Divider> fragment;

        public MyHandler(BusinessFragment<Divider> fragment,
                         ArrayList<MenuContent> listCurrent) {
            this.fragment = fragment;
            this.listCurrent = listCurrent;
        }

        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String flag = b.getString("flag");
            setMenuIntent(flag);
        }

        /**
         * 设置跳转界面
         */
        private void setMenuIntent(String flag) {
            Log.v("show", "XML模板名称：" + clicked_id_model);

            if (fragment.waitDialogRectangle != null && fragment.waitDialogRectangle.isShowing()) {
                fragment.waitDialogRectangle.dismiss();
            }

            // 下载菜单成功
            if (flag.equals("getmenusok"))
            {
                listCurrent = BusinessBaseDao.queryBusinessMenus();
                fragment.listCurrent = listCurrent;
                fragment.refreshList(listCurrent);
                Log.v("show", "菜单的数据:" + listCurrent.toString());
            } else if (CHECT_XML_EXIT.equalsIgnoreCase(flag)) {

            } else if (CHECT_XML_OK.equalsIgnoreCase(flag)
                    || CHECT_XML_OLD.equalsIgnoreCase(flag)
                    || DOWNLOAD_XML_OK.equalsIgnoreCase(flag)) {

                if (clicked_id_model == null || "".equals(clicked_id_model)) {
                    return;
                }
                if (clicked_id_model.substring(clicked_id_model.length() - 4).equals("html")) {
                    //原生以及HTML
                    fragmentIntent(fragment);
                } else {
                    //模板
                    intent = new Intent(fragment.getActivity(), BusinessActivity.class);
                    intent.putExtra("id_parentnode", "");
                    intent.putExtra("var_billno", "");
                    intent.putExtra("id_node", "");
                    intent.putExtra("id_model", clicked_id_model);
                    intent.putExtra("xml_version", clicked_xml_version);
                    fragment.startActivity(intent);
                }
            } else if (DOWNLOAD_XML_ERROR.equalsIgnoreCase(flag)) {
                if (clicked_id_model == null || "".equals(clicked_id_model)) {
                    new MyToast(context, "下载模板错误，请重新尝试。");
                    return;
                }
                fragmentIntent(fragment);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contextView = inflater.inflate(R.layout.fragment_business_ex, container, false);
        context = getActivity();
        businessFragment = this;

        // 从服务器获取显示菜单
        listCurrent = BusinessBaseDao.queryBusinessMenus();
        myHandler = new MyHandler<Divider>(this, listCurrent);
        initView(contextView);
        return contextView;
    }

    /**
     * 加载布局
     *
     * @param contextView
     */
    private void initView(View contextView) {
        listCurrent1 = new ArrayList<>();
        listCurrent2 = new ArrayList<>();
        listCurrent3 = new ArrayList<>();
        listCurrent4 = new ArrayList<>();

        gridView1 = (MyGridView) contextView.findViewById(R.id.gridview_businessmenu_ex1);
        gridView2 = (MyGridView) contextView.findViewById(R.id.gridview_businessmenu_ex2);
        gridView3 = (MyGridView) contextView.findViewById(R.id.gridview_businessmenu_ex3);
        gridView4 = (MyGridView) contextView.findViewById(R.id.gridview_businessmenu_ex4);
        visit_layout = (LinearLayout) contextView.findViewById(R.id.visit_layout);
        other_layout = (LinearLayout) contextView.findViewById(R.id.other_layout);
        find_layout = (LinearLayout) contextView.findViewById(R.id.find_layout);
        bus_error = (LinearLayout) contextView.findViewById(R.id.bus_error);
        performance_layout = (LinearLayout) contextView.findViewById(R.id.performance_layout);

        getBusinessMenus();
    }

    /**
     * 跳转界面
     *
     * @param fragment
     */
    private void fragmentIntent(Fragment fragment) {
        if (BusinessQueryDao.getUserInfo(context))
        {
            if (clicked_id_model.equals(Constant.ddisplocatphohtml))
            {
                intent = new Intent(fragment.getActivity(), BusinessDdisplocathActivity.class);
            }
            else
            if (clicked_id_model.equals(Constant.dgtdouthtml) ||
                    clicked_id_model.equals(Constant.dgtdothtml) ||
                    clicked_id_model.equals(Constant.dgtdvathtml) ||
                    clicked_id_model.equals(Constant.dgtdabnhtml))
            {
                intent = new Intent(fragment.getActivity(), TravelActivityNew.class);
            }
            else
            if (clicked_id_model.equals(Constant.dkpipostinputhtml))
                {
                intent = new Intent(fragment.getActivity(), BusinessPerformanceArrayList.class);
            }
            else
            if (clicked_id_model.equals(Constant.ddisplocatEJhtml))
            {
                intent = new Intent(fragment.getActivity(), BusinessEJLocation.class);
            }
            else
            if (clicked_id_model.equals(Constant.dkpipostreviewhtml) ||
                    clicked_id_model.equals(Constant.dkpipostconfimhtml) ||
                    clicked_id_model.equals(Constant.dkpipostreadmehtml) ||
                    clicked_id_model.equals(Constant.dkpipostratehtml) ||
                    clicked_id_model.equals(Constant.dkpipostidentificatehtml))
            {
                intent = new Intent(fragment.getActivity(), BusinessBillsActivity.class);
            }
            else
            if (clicked_id_model.equals(Constant.dgtdrechtml))
            {
                intent = new Intent(fragment.getActivity(), BusinessDgtdrechtml.class);
            }
            else
            {
                intent = new Intent(fragment.getActivity(), BussinessHtmlActivity.class);
                intent.putExtra("id_model", clicked_id_model);
            }
        }
        else
        {
            intent = new Intent(fragment.getActivity(), SetActivity.class);
        }

        fragment.startActivity(intent);
        Log.v("show", "HTML模板名称：" + clicked_id_model);
    }

//    /**
//          * 判断模板是否存在
//          *
//          * @param fragment
//          */
//    private static void fragmentFailIntent(Fragment fragment) {
//        if (BusinessQueryDao.getUserInfo(context))
//        {
//            if (clicked_id_model.equals(Constant.ddisplocatphohtml))
//            {
//                intent = new Intent(fragment.getActivity(), BusinessDdisplocathActivity.class);
//            }
//            else
//            if (clicked_id_model.equals(Constant.ddisplocatEJhtml))
//            {
//                intent = new Intent(fragment.getActivity(), BusinessEJLocation.class);
//            }
//            else
//            if (clicked_id_model.equals(Constant.dgtdouthtml) ||
//                    clicked_id_model.equals(Constant.dgtdothtml) ||
//                    clicked_id_model.equals(Constant.dgtdvathtml) ||
//                    clicked_id_model.equals(Constant.dgtdabnhtml))
//            {
//                intent = new Intent(fragment.getActivity(), TravelActivityNew.class);
//            }
//            else
//            if (clicked_id_model.equals(Constant.dkpipostinputhtml))
//            {
//                intent = new Intent(fragment.getActivity(), BusinessPerformanceArrayList.class);
//            }
//            else
//            if (clicked_id_model.equals(Constant.dgtdrechtml))
//            {
//                intent = new Intent(fragment.getActivity(), BusinessDgtdrechtml.class);
//            }
//            else
//            if (clicked_id_model.equals(Constant.dkpipostreviewhtml) ||
//                    clicked_id_model.equals(Constant.dkpipostconfimhtml) ||
//                    clicked_id_model.equals(Constant.dkpipostreadmehtml) ||
//                    clicked_id_model.equals(Constant.dkpipostratehtml) ||
//                    clicked_id_model.equals(Constant.dkpipostidentificatehtml))
//            {
//                    intent = new Intent(fragment.getActivity(), BusinessBillsActivity.class);
//            }
//            else
//            {
//                new MyToast(context,"模版下载错误！请重试");
//            }
//
//        }
//        else
//        {
//            intent = new Intent(fragment.getActivity(), SetActivity.class);
//        }
//
//        fragment.startActivity(intent);
//    }

    private void dogridview(final ArrayList<MenuContent> listCurrent, GridView gridView) {
        adapter = new BusinessGridViewAdapter(getActivity(), listCurrent);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
        final BusinessGridViewAdapter finalAdapter = adapter;

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuContent = finalAdapter.getItem(position);
                clicked_id_model = menuContent.getVarParm();
                Constant.ID_MENU = menuContent.getIdMenu();
                clicked_xml_nameModel = menuContent.getNameMenu();
                clicked_xml_version = listCurrent.get(position).getVarParm();
                if (checkSigleXMLModelexit(menuContent.getVarParm() + ".xml")) {// 检查当前模板是否存在
                    // TODO 1、检查xml版本，是否需要更新
                    String serverVersion = menuContent.getModelWindow();
                    String localVersion = BusinessBaseDao
                            .getTemlateVersion(menuContent.getVarParm());
                    if (serverVersion != null
                            && serverVersion.equalsIgnoreCase(localVersion)) {
                        // 不需要更新
                        sendToHandler(CHECT_XML_OK);
                    } else {
                        // 2、检查网络
                        if (hasInternetConnected(context)) {
                            updateModelThread();
                        } else {
                            // 网络无连接，用老版本
                            sendToHandler(CHECT_XML_OLD);
                        }
                    }
                } else {
                    updateModelThread();
                }
            }
        });
    }

    /**
     * 添加菜单
     *
     * @param listCurrent
     */
    private void sortlist(ArrayList<MenuContent> listCurrent) {

        listCurrent1 = new ArrayList<>();
        listCurrent2 = new ArrayList<>();
        listCurrent3 = new ArrayList<>();
        listCurrent4 = new ArrayList<>();

        for (int i = 0; i < listCurrent.size(); i++) {
            if (listCurrent.get(i).getVarParm1().equalsIgnoreCase("001")) {
                listCurrent1.add(listCurrent.get(i));
            } else if (listCurrent.get(i).getVarParm1().equalsIgnoreCase("002")) {
                listCurrent2.add(listCurrent.get(i));
            } else if (listCurrent.get(i).getVarParm1().equalsIgnoreCase("003")) {
                listCurrent3.add(listCurrent.get(i));
            } else {
                listCurrent4.add(listCurrent.get(i));
            }
        }
        if (listCurrent1.size() > 0) {
            visit_layout.setVisibility(View.VISIBLE);
        } else {
            visit_layout.setVisibility(View.GONE);
        }
        if (listCurrent2.size() > 0) {
            other_layout.setVisibility(View.VISIBLE);
        } else {
            other_layout.setVisibility(View.GONE);
        }
        if (listCurrent3.size() > 0) {
            find_layout.setVisibility(View.VISIBLE);
        } else {
            find_layout.setVisibility(View.GONE);
        }
        if (listCurrent4.size() > 0) {
            performance_layout.setVisibility(View.VISIBLE);
        } else {
            performance_layout.setVisibility(View.GONE);
        }
        addlist(listCurrent1);
        addlist(listCurrent2);
        addlist(listCurrent3);
        addlist(listCurrent4);

        dogridview(listCurrent1, gridView1);
        dogridview(listCurrent2, gridView2);
        dogridview(listCurrent3, gridView3);
        dogridview(listCurrent4, gridView4);
    }

    private void addlist(ArrayList<MenuContent> listCurrent) {
        int a = listCurrent.size() % 4;
        if (a != 0 || listCurrent.size() == 0) {
            for (int i = 0; i < (4 - a); i++) {
                MenuContent menuContent = new MenuContent();
                menuContent.setPicpath("");
                menuContent.setModelWindow("0");
                listCurrent.add(menuContent);
            }
        }
    }

    /**
     * 获得菜单
     */
    private void getBusinessMenus() {
        HttpPost post = null;
        try {
            post = HttpClientBuilder
                    .createParam(Constant.BUSINESS_SERVICE_ADDRESS)
                    .addKeyValue(Constant.BM_ACTION_TYPE, Constant.BMTYPE_BUSINESS_MENU).getHttpPost();
        } catch (UnsupportedEncodingException e1) {
            Log.e(e1);
        }

        HttpClientManager.addTask(new HttpResponseHandler() {
            @Override
            public void onResponse(HttpResponse resp) {
                try {
                    String msg = HttpClientManager.toStringContent(resp);
                    Gson gson = new Gson();
                    final BusinessMenuResp businessMenuResp = gson.fromJson(msg, BusinessMenuResp.class);
                    Log.d("businessresp", businessMenuResp.data.items.toString());
                    if ("result".equalsIgnoreCase(businessMenuResp.type)) {
                        if (businessMenuResp.data != null) {
                            SQLiteWorker.getSharedInstance().postDML(
                                    new SQLiteWorker.AbstractSQLable() {
                                        @Override
                                        public void onCompleted(Object event) {
                                            if (!(event instanceof Throwable)) {
                                                sendToHandler("getmenusok");
                                            }
                                        }

                                        @Override
                                        public Object doAysncSQL() {
                                            BusinessBaseDao.deleteBusinessMenus();// 插入新菜单前清除旧数据
                                            BusinessBaseDao.replaceBusinessMenus(businessMenuResp.data.items);
                                            return null;
                                        }
                                    });
                        } else {
                        }
                    }
                } catch (IOException e) {
                    onException(e);
                }
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        }, post);

        android.util.Log.e("show", post.toString());
    }

    /**
     * 刷新业务菜单
     */
    public void refreshList() {
        listCurrent = BusinessBaseDao.queryBusinessMenus();
        if (listCurrent.size() > 0) {
            refreshList(listCurrent);
        } else {
            getBusinessMenus();
        }
    }

    //刷新菜单
    private void refreshList(ArrayList<MenuContent> list) {
        if (list.size() > 0) {
            bus_error.setVisibility(View.GONE);
        } else {
            bus_error.setVisibility(View.VISIBLE);
        }

        sortlist(list);
        checkPaoPao();
    }

    private void sendToHandler(String msg) {
        Message Msg = new Message();
        Bundle b = new Bundle();
        b.putString("flag", msg);
        Msg.setData(b);
        myHandler.sendMessage(Msg);
    }

    // 检查xml文件是否全部存在
    boolean checkSigleXMLModelexit(String mfileName) {
        boolean flag = false;
        if (listCurrent == null || listCurrent.size() == 0) {
            Log.e(TAG, " listcurrent null");
            return false;
        }
        // 检查xml文件
        File file = EapApplication.getApplication().getFilesDir();
        if (file != null) {

            File[] files = file.listFiles();
            String[] filenames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                Log.i(TAG, "file name is " + files[i].getName());
                filenames[i] = files[i].getName();
                if (mfileName.equalsIgnoreCase(files[i].getName())) {
                    return true;
                }
            }

        } else {
            Log.e(TAG, "xmlmodel 为null");
            flag = false;
        }
        return flag;
    }


    public void showNoticeDialog() {

        // noticeDialog = new Dialog(getActivity(), R.style.noticeDialogStyle);
        noticeDialog = new Dialog(getActivity(), R.style.noticeDialogStyle);
        // noticeDialog.setContentView(R.layout.dialog_notice_withcancel);
        noticeDialog.setContentView(R.layout.dialog_notice_nocancel);

        TextView notice = (TextView) noticeDialog.findViewById(R.id.nc_notice);
        notice.setText("当前应用不存在，请到设置里更新应用");

        RelativeLayout dialog_confirm_rl = (RelativeLayout) noticeDialog
                .findViewById(R.id.dialog_nc_confirm_rl);

        dialog_confirm_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // new Ctlm1346Update().action();//下载xml
                noticeDialog.dismiss();
            }
        });

        noticeDialog.show();

    }

    private void checkPaoPao() {
        String localVersion;
        for (int i = 0; i < listCurrent.size(); i++) {
            localVersion = BusinessBaseDao.getTemlateVersion(listCurrent.get(i)
                    .getVarParm());
            if (!localVersion.equals(listCurrent.get(i).getModelWindow())) {
                isPoPoVisible = true;
                break;
            } else {
                isPoPoVisible = false;
            }
        }
    }


    public static boolean hasInternetConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo network = manager.getActiveNetworkInfo();
            if (network != null && network.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    private void updateModelThread() {
        if (!hasInternetConnected(getActivity())) {
        }
        if (waitDialogRectangle != null && waitDialogRectangle.isShowing()) {
            waitDialogRectangle.dismiss();
        }
        waitDialogRectangle = new WaitDialogRectangle(context);
        waitDialogRectangle.setCanceledOnTouchOutside(false);
        waitDialogRectangle.show();

        mThread = new Thread() {
            @Override
            public void run() {
                new Ctlm1346Update(new Command.OnMultiResultListener() {
                    @Override
                    public void onResult(List<Boolean> successes) {
                        if (successes.contains(false)) {
                            Log.e(TAG, "Ctlm1346Update contains false");
                            waitDialogRectangle.dismiss();
                            sendToHandler(DOWNLOAD_XML_ERROR);
                        } else {
                            waitDialogRectangle.dismiss();
                            sendToHandler(DOWNLOAD_XML_OK);
                        }

                    }
                }).action();// 下载xml
            }
        };
        mThread.start();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         * @author haijian 检查是否显示泡泡
		 */
        checkPaoPao();
        refreshList();
    }

    @Override
    public void onPause() {
        if (waitDialogRectangle != null && waitDialogRectangle.isShowing()) {
            waitDialogRectangle.dismiss();
        }
        super.onPause();
    }

}
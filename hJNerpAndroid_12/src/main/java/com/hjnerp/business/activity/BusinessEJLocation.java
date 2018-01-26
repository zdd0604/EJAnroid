package com.hjnerp.business.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.hjnerp.activity.SetActivity;
import com.hjnerp.adapter.BusinessSginImageViewAdapter;
import com.hjnerp.business.BusinessQueryDao.BusinessQueryDao;
import com.hjnerp.business.BusinessStringBuffer.BusinessEJBuffer;
import com.hjnerp.business.businessutils.BuinessImgUtils;
import com.hjnerp.business.businessutils.BusinessFileUtils;
import com.hjnerp.business.businessutils.BusinessTimeUtils;
import com.hjnerp.business.businessutils.MyOrientationListener;
import com.hjnerp.common.ActionBarWidgetActivity;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.dao.BusinessBaseDao;
import com.hjnerp.dao.OtherBaseDao;
import com.hjnerp.model.Ctlm1108;
import com.hjnerp.model.IDComConfig;
import com.hjnerp.util.StringUtil;
import com.hjnerp.util.ZipUtils;
import com.hjnerp.widget.HorizontalListView;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.sdyy.utils.XPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * EJ的考勤签到
 */
public class BusinessEJLocation extends ActionBarWidgetActivity implements View.OnClickListener,
        MyOrientationListener.OnOrientationListener {

    //布局
    @BindView(R.id.action_center_tv)
    TextView actionCenterTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.action_right_tv1)
    TextView actionRightTv1;
    @BindView(R.id.action_left_tv)
    TextView actionLeftTv;
    @BindView(R.id.ej_photo_list)
    HorizontalListView ej_photo_list;
    //地位地址
    @BindView(R.id.ej_location_path)
    TextView login_ej_location;

    //图片添加
    @BindView(R.id.ej_photo_add)
    ImageView ej_photo_add;
    //上下班View展示
    @BindView(R.id.sgin_view_up)
    View sgin_view_up;
    @BindView(R.id.sgin_view_down)
    View sgin_view_down;
    @BindView(R.id.sgin_view_prbar)
    ProgressBar sgin_view_prbar;
    //上下班时间
    @BindView(R.id.sgin_time_up)
    TextView sgin_time_up;
    @BindView(R.id.sgin_time_down)
    TextView sgin_time_down;
    //上下班打卡按钮
    @BindView(R.id.sign_img_up)
    ImageView sign_img_up;
    @BindView(R.id.sign_img_down)
    ImageView sign_img_down;

    //地图
    @BindView(R.id.ej_location_bdmap)
    MapView ej_mapview;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private BitmapDescriptor mIconLocation;
    private MyLocationListener myLocationListener;
    // 模式
    private MyLocationConfiguration.LocationMode locationMode;
    private LatLng latLng;

    //地图返回的信息
    private String location_path;
    private double sginLatitude = 0.00;
    private double sginLongitude = 0.00;

    //适配器
    private BusinessSginImageViewAdapter businessSginImageViewAdapter;

    //存储照片
    private List<Bitmap> photoBitmapList = new ArrayList<>();  //照片集合
    private List<File> photoFileList = new ArrayList<>();   //照片存放的文件夹的集合
    private List<String> photoNameList = new ArrayList<>();   //照片名称集合
    private List<String> photoPathList = new ArrayList<>();// 保存图片路径
    private String photoName = "";
    private String photoPath = "";
    private String photoLocation = "";
    private String photoUUID = "";
    private File photoFilePath; //照片保存的文件夹的路径
    private File zipPhotoFilePath;//照片压缩包的路径
    private Bitmap defaultBt;//水平list的默认图标

    //签到或者签退的状态
    private String sgin_type_Y = "Y";
    private String sgin_type_N = "N";
    private Boolean isSgin = true; //默认上上班
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;//方向

    //签到文件的信息
    //打卡时间
    private String sgin_time = "";
    //签到数据
    private String sginData = "";
    //是否在打卡范围之内
    private boolean isLocationTrue = false;
    //经纬度集合
    private List<Double> mLoca = new ArrayList<>();
    private List<LatLng> mLatlng = new ArrayList<>();
    private String todayTime;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String msgContent = (String) msg.obj;
            switch (msg.what) {
                case Constant.HANDLERTYPE_0:
                    setMapnfo();
                    break;
                case Constant.HANDLERTYPE_1:
                    addImageView();
                    break;
                case Constant.HANDLERTYPE_2:
                    if (StringUtil.isStrTrue(msgContent))
                        showFailToast(msgContent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_ejlocation);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        actionCenterTv.setText(getString(R.string.buess_Title_CenterTitle));
        actionRightTv.setVisibility(View.GONE);
        actionLeftTv.setOnClickListener(this);
        sign_img_up.setOnClickListener(this);
        sign_img_down.setOnClickListener(this);
        ej_photo_add.setOnClickListener(this);

        photoUUID = StringUtil.getMyUUID();
        photoPath = Constant.SGIN_SAVE_DIR + "/" + photoUUID;
        BusinessQueryDao.getSgin_Section("ctlm7161");

        initMapLocation();

        judgeLocationData();

        //设置默认添加图片
//        setHListImage();
    }

    /**
     * 定位及展示
     */
    private void initMapLocation() {

        //判断定位的权限
        if (!isPermissions(new String[]{XPermissions.ACCESS_COARSE_LOCATION,
                XPermissions.ACCESS_FINE_LOCATION})) {
            login_ej_location.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.icon_location_erro), null, null, null);
            login_ej_location.setText(getString(R.string.sgin_Hint_address4));
            login_ej_location.setTextColor(Color.parseColor("#ffa727"));
        }

        //查询地理位置信息表(默认有三个打卡地点)
        BusinessQueryDao.getDdisplocat_location("ddisplocat_location");
        if (Constant.mDdisplocatBean != null) {
            addLocation(Constant.mDdisplocatBean.getVar_lati(),
                    Constant.mDdisplocatBean.getVar_longi());
            addLocation(Constant.mDdisplocatBean.getVar_lati1(),
                    Constant.mDdisplocatBean.getVar_longi1());
            addLocation(Constant.mDdisplocatBean.getVar_lati2(),
                    Constant.mDdisplocatBean.getVar_longi2());
        }

        // 初始化图标
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.position0);

        mBaiduMap = ej_mapview.getMap();
        ej_mapview.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);// 地图放大级别
        mBaiduMap.setMapStatus(msu);

        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);

        //定位成功后的回调监听方向监听
        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    /**
     * 添加地理位置信息
     *
     * @param Latitude
     * @param Longtitude
     */
    private void addLocation(String Latitude, String Longtitude) {
        if (StringUtil.isStrTrue(Latitude) && StringUtil.isStrTrue(Longtitude)) {
            mLatlng.add(new LatLng(Double.valueOf(Latitude), Double.valueOf(Longtitude)));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_img_up:
                submitSginDatas(sgin_type_Y);
                isSgin = true;
                break;
            case R.id.sign_img_down:
                submitSginDatas(sgin_type_N);
                isSgin = false;
                break;
            case R.id.action_left_tv:
                finish();
                break;
            case R.id.ej_photo_add:
                if (!isPermissions(new String[]{
                        XPermissions.CAMERA,
                        XPermissions.READ_CONTACTS})) {
                    showFailToast("相机未授权");
                    return;
                }
                ej1345Exist();
                break;
        }
    }

    /**
     * 跳转相机
     */
    private void ej1345Exist() {
        if (BusinessQueryDao.getUserInfo(context)) {
            IntentCamera();
        } else {
            Intent intent = new Intent(BusinessEJLocation.this, SetActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 收集数据并提交
     */
    private void submitSginDatas(String sginType) {
        waitDialog.show();
        //获取照片集合名称
        String photoname = getPhotoName();
        //获取打卡时间
        sgin_time = BusinessTimeUtils.getCurrentTime(Constant.SGIN_FORMART);

        if (BusinessQueryDao.getUserInfo(context)) {
            IDComConfig idconfig = OtherBaseDao.queryReginfo(Constant.ej1345.getId_com());
            if (idconfig != null) {
                EapApplication.URL_SERVER_HOST_HTTP = idconfig.getUrl_http();
            }
        } else {
            Intent intent = new Intent(BusinessEJLocation.this, SetActivity.class);
            startActivity(intent);
        }

        if (!Constant.ctlm7161Is) {
            //7161表不存在的时候
            showFailToast("获取排班数据失败请联系管理员");
            remove();
            return;
        }

        if (!StringUtil.isStrTrue(Constant.ej1345.getVar_on())) {
            showFailToast("计划上班时间获取失败");
            remove();
            return;
        }

        if (!StringUtil.isStrTrue(Constant.ej1345.getVar_off())) {
            showFailToast("计划下班时间获取失败");
            remove();
            return;
        }

        if (!isLocationTrue) {
            showFailToast("定位点不在工作区,请到工作区内重新打卡");
            remove();
            return;
        }

        sginData = BusinessEJBuffer.getSginBuffer(
                Constant.MYUSERINFO.userID,
                Constant.MYUSERINFO.companyID, sgin_time,
                sginType,
                Constant.ej1345.getId_clerk(),
                Constant.ej1345.getId_com(),
                Constant.ej1345.getId_user(),
                "",
                location_path, photoname);


        //设置打卡时间以及View的状态
        if (sginType.equals("Y")) {
            BusinessTimeUtils.getIntegerTime(sgin_time, Constant.ctlm7161.getVar_on());
            sgin_time_up.setText(sgin_time);
            sgin_view_up.setBackgroundResource(R.drawable.circle_yellow_point);
        } else {
            BusinessTimeUtils.getIntegerTimeOut(sgin_time, Constant.ctlm7161.getVar_off());
            sgin_time_down.setText(sgin_time);
            sgin_view_down.setBackgroundResource(R.drawable.circle_yellow_point);
        }

        LogShow(sginData);
        zipPhotoFile();

        insertSginData(sginType);
    }


    /**
     * 更新签到本地数据
     *
     * @param sginType
     */
    private void insertSginData(String sginType) {
        List<Ctlm1108> ctlm1108List = BusinessBaseDao.selectCtlm1108(Constant.ej1345.getId_clerk(), todayTime);
        if (ctlm1108List.size() > 0) {
            BusinessBaseDao.updateCtlm1108(Constant.ej1345.getId_clerk(), todayTime, sgin_time, sginType);
        } else {
            if (sginType.equals(sgin_type_Y)) {
                BusinessBaseDao.insertCtlm1108(new String[]{
                        Constant.ej1345.getId_clerk(),
                        todayTime, getTvVaule(sgin_time_up), ""});
            } else {
                BusinessBaseDao.insertCtlm1108(new String[]{
                        Constant.ej1345.getId_clerk(), todayTime, "",
                        getTvVaule(sgin_time_down)});
            }
        }

        judgeLocationData();
    }

    /**
     * 判断考勤签到信息
     */
    private void judgeLocationData() {
        todayTime = BusinessTimeUtils.getCurrentTime(Constant.TIME_yyyy_MM_dd);
        List<Ctlm1108> ctlm1108List = BusinessBaseDao.selectCtlm1108(
                Constant.ej1345.getId_clerk(), todayTime);
        if (ctlm1108List.size() > 0) {
            sgin_time_up.setText(ctlm1108List.get(0).getSgin_time_up());
            sgin_time_down.setText(ctlm1108List.get(0).getSgin_time_down());
            setSginView(ctlm1108List.get(0).getSgin_time_up(),
                    ctlm1108List.get(0).getSgin_time_down());
        } else {
            LogShow("数据库没有数据");
        }
    }

    /**
     * 设置view 的状态
     *
     * @param sgin_time_up
     * @param sgin_time_down
     */
    private void setSginView(String sgin_time_up, String sgin_time_down) {
        int prgress = 0;
        if (StringUtil.isStrTrue(sgin_time_up) && StringUtil.isStrTrue(sgin_time_down)) {
            prgress = 100;
            sgin_view_up.setBackgroundResource(R.drawable.circle_yellow_point);
            sgin_view_down.setBackgroundResource(R.drawable.circle_yellow_point);
        } else if (StringUtil.isStrTrue(sgin_time_up) && !StringUtil.isStrTrue(sgin_time_down)) {
            prgress = 50;
            sgin_view_up.setBackgroundResource(R.drawable.circle_yellow_point);
            sgin_view_down.setBackgroundResource(R.drawable.circle_gray_point);
        } else if (!StringUtil.isStrTrue(sgin_time_up) && StringUtil.isStrTrue(sgin_time_down)) {
            prgress = 100;
            sgin_view_up.setBackgroundResource(R.drawable.circle_gray_point);
            sgin_view_down.setBackgroundResource(R.drawable.circle_yellow_point);
        } else {
            prgress = 0;
            sgin_view_up.setBackgroundResource(R.drawable.circle_gray_point);
            sgin_view_down.setBackgroundResource(R.drawable.circle_gray_point);
        }
        sgin_view_prbar.setProgress(prgress);
    }


    /**
     * 压缩照片
     */
    private void zipPhotoFile() {
        if (photoFileList.size() > 0) {
            zipPhotoFilePath = new File(photoPath + ".zip");
            try {
                if (ZipUtils.zipPhotoFiles(photoFileList, zipPhotoFilePath)) {
                    upLoadData(sginData, zipPhotoFilePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            upLoadData(sginData);
        }
    }


    /**
     * 添加默认的集合图片
     */
//    private void setHListImage() {
//        photoBitmapList.clear();
////        InputStream is = getResources().openRawResource(R.drawable.chat_normal_ddisp);
////        Bitmap mBitmap = BitmapFactory.decodeStream(is);
//        Resources r = this.getContext().getResources();
//        defaultBt = BitmapFactory.decodeResource(r, R.drawable.icon_sgin_list_img);
//        photoBitmapList.add(defaultBt);
//        businessSginImageViewAdapter = new BusinessSginImageViewAdapter(photoBitmapList, this);
//        businessSginImageViewAdapter.notifyDataSetChanged();
//        ej_photo_list.setAdapter(businessSginImageViewAdapter);
//        ej_photo_list.setOnItemClickListener(clickListener);
//    }


    /**
     * 跳转相机
     */
    private void IntentCamera() {
        try {
            photoName = "ph" + BusinessTimeUtils.getCurrentTime(Constant.SGIN_PHOTONAME) + Constant.ej1345.getId_user();
            photoLocation = photoPath + "/" + photoName + ".jpg";
            BusinessFileUtils.creatFile(photoPath);

            photoFilePath = new File(photoLocation);
            if (BusinessFileUtils.isCreate(photoPath)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // TODO
                    Uri imageUri = FileProvider.getUriForFile(this, "com.hjnerp.takephoto.fileprovider", photoFilePath);//通过FileProvider创建一个content类型的Uri
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                    startActivityForResult(intent, Activity.DEFAULT_KEYS_DIALER);
                } else {
                    // 跳转相机
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFilePath));
                    startActivityForResult(intent, Activity.DEFAULT_KEYS_DIALER);
                }
            } else {
                showFailToast("文件夹创建失败");
            }
        } catch (Exception b) {
            Log.v("show", photoLocation + "相机输出出错" + photoName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            showFailToast("获取图片失败,请重试");
            photoBitmapList.clear();
            photoBitmapList.add(defaultBt);
            return;
        }
        switch (requestCode) {
            case Activity.DEFAULT_KEYS_DIALER: {
                try {
                    photoFileList.add(photoFilePath);
                    photoNameList.add(photoName);
                    photoPathList.add(photoLocation);

                    Bitmap decodeBitmap = BuinessImgUtils.decodeBitmap(photoFilePath);
                    decodeBitmap.compress(Bitmap.CompressFormat.JPEG, 60, new FileOutputStream(photoFilePath));
                    photoBitmapList.add(decodeBitmap);
                    handler.sendEmptyMessage(Constant.HANDLERTYPE_1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 添加多张照片
     */
    private void addImageView() {
        businessSginImageViewAdapter = new BusinessSginImageViewAdapter(photoBitmapList, this);
        businessSginImageViewAdapter.notifyDataSetChanged();
        ej_photo_list.setAdapter(businessSginImageViewAdapter);
        ej_photo_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ShowSginPhotoFeleteDialog(position);
                return false;
            }
        });
    }


    /**
     * 提示是否删除照片
     */
    private void ShowSginPhotoFeleteDialog(final int position) {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("是否删除当前照片?")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        photoBitmapList.remove(position);
                        businessSginImageViewAdapter.notifyDataSetChanged();
                        if (photoPathList.size() > position) {
                            String sginPath = photoPathList.get(position);
                            if (BusinessFileUtils.deleteFile(sginPath, BusinessEJLocation.this))
                                photoPathList.remove(position);
                            photoNameList.remove(position);
                        }
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        }).show();
    }

    /**
     * 这是照片名称
     *
     * @return
     */
    private String getPhotoName() {
        try {
            if (photoNameList.size() >= 10) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < 10; i++) {
                    if (i == (photoNameList.size() - 1)) {
                        stringBuffer.append(photoNameList.get(i) + ".jpg");
                    } else {
                        stringBuffer.append(photoNameList.get(i) + ".jpg;");
                    }
                }
                String str = stringBuffer.toString();
                return str;
            } else if (photoNameList.size() > 0
                    && photoNameList.size() < 10) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < photoNameList.size(); i++) {
                    if (i == (photoNameList.size() - 1)) {
                        stringBuffer.append(photoNameList.get(i) + ".jpg");
                    } else {
                        stringBuffer.append(photoNameList.get(i) + ".jpg;");
                    }
                }
                String str = stringBuffer.toString();
                return str;
            }
        } catch (IndexOutOfBoundsException e) {
            Log.v("show", "数组下标越界");
        }
        return null;
    }


    @Override
    public void onOrientationChanged(float x) {

    }

    /**
     * 地图接口回调
     */
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 地理位置信息返回结果
            location_path = bdLocation.getAddrStr();
            sginLatitude = bdLocation.getLatitude();
            sginLongitude = bdLocation.getLongitude();
            MyLocationData myLocationData = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(bdLocation.getRadius())//
                    .latitude(bdLocation.getLatitude())//
                    .longitude(bdLocation.getLongitude())//
                    .build();

            mBaiduMap.setMyLocationData(myLocationData);

            LogShow(location_path + "-" + sginLatitude + "-" + sginLongitude);

            if (StringUtil.isStrTrue(location_path)) {
                handler.sendEmptyMessage(Constant.HANDLERTYPE_0);
            }
        }
    }

    /**
     * 设置地图
     */
    private void setMapnfo() {


        // 设置自定义图标
        MyLocationConfiguration configuration = new MyLocationConfiguration(locationMode, true, mIconLocation);
        mBaiduMap.setMyLocationConfiguration(configuration);


        latLng = new LatLng(sginLatitude, sginLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        if (mLatlng.size() > 0) {
            mLoca.clear();
            for (int i = 0; i < mLatlng.size(); i++) {
                //计算p1、p2两点之间的直线距离，单位：米
                Double mLocation = DistanceUtil.getDistance(latLng, mLatlng.get(i));
                mLoca.add(mLocation);
            }
            Collections.sort(mLoca);
            if (mLoca.size() > 0) {
                if (Double.valueOf(Constant.mDdisplocatBean.getVar_range()) >= mLoca.get(0)) {
                    login_ej_location.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                            R.drawable.icon_location_secd), null, null, null);
                    login_ej_location.setText(location_path);
                    isLocationTrue = true;
                    LogShow("在范围");
                } else {
                    login_ej_location.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                            R.drawable.icon_location_erro), null, null, null);
                    login_ej_location.setText(getString(R.string.sgin_Hint_address3));
                    login_ej_location.setTextColor(Color.parseColor("#ffa727"));
                    isLocationTrue = false;
                    LogShow("不在范围");
                }
            }
        } else {
            isLocationTrue = true;
            LogShow("获取经纬度失败");
        }
        mBaiduMap.animateMapStatus(msu);
    }

    /**
     * 上传文件
     *
     * @param datas
     * @param zipFile
     */
    private void upLoadData(String datas, File zipFile) {
//        OkGo.post("http://172.16.12.243:8085/hjmerp/servlet/DataUpdateServlet")//
//        OkGo.post("http://192.168.199.215:8085/hjmerp/servlet/DataUpdateServlet")//
//        Log.v("show", EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataUpdateServlet");
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataUpdateServlet")
                .tag(this)//
                .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .params("datas", datas)        // 这里可以上传参数
                .params("download", zipFile)   // 可以添加文件上传
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (isSgin) {
                            switch (Constant.sginType) {
                                case 0:
                                    sendHandler("新的一天开始了，要努力工作哦！");
                                    break;
                                case 1:
                                    sendHandler("您迟到了哟！");
                                    break;
                                case 2:
                                    sendHandler("本次打卡异常，请不要忘记填写考勤异常申诉单！");
                                    break;
                            }
                        } else {
                            switch (Constant.outType) {
                                case 0:
                                    sendHandler("工作辛苦了，再见！");
                                    break;
                                case 1:
                                    sendHandler("打卡成功，不要早退哦！");
                                    break;
                                case 2:
                                    sendHandler("本次打卡异常，请不要忘记填写考勤异常申诉单！");
                                    break;
                            }
                        }
                        remove();
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (e instanceof SocketTimeoutException) {
                            sendHandler("请求超时请重新上传");
                        } else {
                            sendHandler("上传失败请重新上传");
                        }
                        remove();
                    }
                });
    }


    /**
     * 上传文件
     *
     * @param datas
     */
    private void upLoadData(String datas) {
//        OkGo.post("http://172.16.12.243:8085/hjmerp/servlet/DataUpdateServlet")//
//        OkGo.post("http://192.168.199.215:8085/hjmerp/servlet/DataUpdateServlet")//
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataUpdateServlet")
                .tag(this)//
                .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .params("datas", datas)        // 这里可以上传参数
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (isSgin) {
                            switch (Constant.sginType) {
                                case 0:
                                    sendHandler("新的一天开始了，要努力工作哦！");
                                    break;
                                case 1:
                                    sendHandler("您迟到了哟！");
                                    break;
                                case 2:
                                    sendHandler("本次打卡异常，请不要忘记填写考勤异常申诉单！");
                                    break;
                            }
                        } else {
                            switch (Constant.outType) {
                                case 0:
                                    sendHandler("工作辛苦了，再见！");
                                    break;
                                case 1:
                                    sendHandler("打卡成功，不要早退哦！");
                                    break;
                                case 2:
                                    sendHandler("本次打卡异常，请不要忘记填写考勤异常申诉单！");
                                    break;
                            }
                        }
                        remove();
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (e instanceof SocketTimeoutException) {
                            sendHandler("请求超时请重新上传");
                        } else {
                            sendHandler("上传失败请重新上传");
                        }
                        remove();

                    }
                });
    }

    private void sendHandler(Object o) {
        Message message = new Message();
        message.obj = o;
        message.what = Constant.HANDLERTYPE_2;
        handler.sendMessage(message);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        XPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults); // 回调函数
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 开启定位的允许
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        myOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //停止方向传感器
        myOrientationListener.stop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        remove();
    }


    private void remove() {
        waitDialog.dismiss();
        if (zipPhotoFilePath != null && zipPhotoFilePath.exists()) {
            zipPhotoFilePath.delete();
        }
        if (photoPathList.size() > 0) {
            BusinessFileUtils.deleteFiles(photoPath);
        }
        photoBitmapList.clear();
        photoFileList.clear();
        photoNameList.clear();
        photoPathList.clear();
        photoName = "";

        addImageView();
//        setHListImage();
    }
}

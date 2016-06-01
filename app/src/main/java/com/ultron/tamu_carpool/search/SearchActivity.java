package com.ultron.tamu_carpool.search;



import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.StatusBarCompat;
import com.ultron.tamu_carpool.match.MatchDetailActivity;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.AMapUtil;
import com.ultron.tamu_carpool.util.InteractUtil;
import com.ultron.tamu_carpool.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * AMapV1地图中简单介绍poisearch搜索
 */
public class SearchActivity extends FragmentActivity implements
        OnMarkerClickListener, InfoWindowAdapter, TextWatcher,
        OnPoiSearchListener, OnClickListener, InputtipsListener,
        AMapLocationListener, LocationSource, OnRouteSearchListener,
        RadioGroup.OnCheckedChangeListener{
    private User user;
    private AMap aMap;
    private AutoCompleteTextView searchText;// 输入搜索关键字
    private String keyWord = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条
    private EditText editCity;// 要输入的城市名字或者城市区号
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    private LatLonPoint mCurPoint = new LatLonPoint(31.883333, 121.2);
    private LatLonPoint mStartPoint = null;//起点
    private LatLonPoint mEndPoint = null;//终点
    private LatLng curPosition = AMapUtil.convertToLatLng(mCurPoint);
    private LatLng startPosition = null;
    private LatLng destPosition = null;
    private String startName = null;
    private String destName;
    private String tarTimeString = null;
    private Date tarTime = null;
    private Date appointTime = null;
    private int mPoolType = 1;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private Context mContext;
    private final int ROUTE_TYPE_DRIVE = 2;
    private RelativeLayout mBottomLayout;
    //private TextView mRotueTimeDes;

    private MatchTask mMatchTask = null;
    private String mMatchResult;

    private Timer updateLocTimer;
    RadioGroup poolTypeRadioGroup;
    RadioButton realTimeBtn, appointmentBtn;
    Button setDateBtn, setTimeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, 0xFF009688);
        setContentView(R.layout.activity_search);
        Intent faIntent = getIntent();
        user = (User)faIntent.getSerializableExtra("user");
        init();
    }
    //TODO:Add time option:realtime or appointment

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            setUpMap();

        }
    }

    /**
     * 设置页面监听
     */
    private void setUpMap() {
        Button searButton = (Button) findViewById(R.id.search_button);
        searButton.setOnClickListener(this);
        Button goForMatch = (Button) findViewById(R.id.goForMatch);
        goForMatch.setOnClickListener(this);

        mContext = this.getApplicationContext();

        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        //mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);

        searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
        searchText.addTextChangedListener(this);// 添加文本输入框监听事件
        editCity = (EditText) findViewById(R.id.city);

        poolTypeRadioGroup = (RadioGroup)findViewById(R.id.pool_type_select);
        realTimeBtn = (RadioButton)findViewById(R.id.realtime_btn);
        appointmentBtn = (RadioButton)findViewById(R.id.appointment_btn);
        setDateBtn = (Button)findViewById(R.id.set_date);
        setTimeBtn = (Button)findViewById(R.id.set_time);
        setDateBtn.setOnClickListener(this);
        setTimeBtn.setOnClickListener(this);
        realTimeBtn.setChecked(true);
        poolTypeRadioGroup.setOnCheckedChangeListener(this);

        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件


        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        aMap.moveCamera(CameraUpdateFactory.zoomTo(11));

        updateLocTimer = new Timer();
        updateLocation();
    }

    public void updateLocation() {
        updateLocTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                InteractUtil interactUtil = new InteractUtil();
                interactUtil.updateLocation(user, mCurPoint);
            }
        },5000, 10000);
    }

    /**
     * 点击搜索按钮
     */
    public void searchButton() {
        keyWord = AMapUtil.checkEditText(searchText);
        if ("".equals(keyWord)) {
            ToastUtil.show(SearchActivity.this, "请输入搜索关键字");
            return;
        } else {
            doSearchQuery();
        }
    }

    /**
     * 点击下一页按钮
     */
    public void nextButton() {
        if (query != null && poiSearch != null && poiResult != null) {
            if (poiResult.getPageCount() - 1 > currentPage) {
                currentPage++;
                query.setPageNum(currentPage);// 设置查后一页
                poiSearch.searchPOIAsyn();
            } else {
                ToastUtil.show(SearchActivity.this,
                        R.string.no_result);
            }
        }
    }

    /**
     * 匹配按钮
     */
    public void goForMatch(){
        if (destPosition == null || mDriveRouteResult == null ){
            ToastUtil.show(mContext, "终点未设定");
            return;
        }

        if (mPoolType == 1){
            if (tarTimeString == null) {
                Date now = new Date();
                tarTimeString = dateFormat.format(now);
            }
        }
        else{
            tarTime = appointTime;
            if (tarTime == null)
                ToastUtil.show(mContext,"预约拼车请选定时间");
            else
                tarTimeString = dateFormat.format(tarTime);
        }


        if (startName == null) startName = "当前位置";
        Log.e("press goformatch", "chuo");
        mMatchTask = new MatchTask();
        mMatchTask.execute((Void) null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1){
            ToastUtil.show(mContext, "请求成功！");
        }
    }


    /**
     * 搜索进度条
     */
    private void showSearchProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        showSearchProgressDialog();// 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", editCity.getText().toString());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());
        ImageButton setStartButton = (ImageButton) view.findViewById(R.id.set_start_point);
        ImageButton setDestButton = (ImageButton) view.findViewById(R.id.set_dest_point);
        // 设置目的地
        setStartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setStart(marker);
            }
        });
        setDestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDestination(marker);
            }
        });
        return view;
    }

    /**
     * 当一个标记被点击确定为目的地时
     *
     */
    private void setStart(Marker marker) {
        startPosition = marker.getPosition();
        startName = marker.getTitle();
        mStartPoint = new LatLonPoint(startPosition.latitude, startPosition.longitude);
        aMap.clear();
        aMap.addMarker(new MarkerOptions()
                .position(startPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        if (destPosition != null) {
            aMap.addMarker(new MarkerOptions()
                    .position(destPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
            searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(startPosition).include(destPosition).build();
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
        }
        else{
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(startPosition).build();
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
        }

        // 移动地图，所有marker自适应显示。LatLngBounds与地图边缘10像素的填充区域

    }
    private void setDestination(Marker marker) {
        destPosition = marker.getPosition();
        destName = marker.getTitle();
        mEndPoint = new LatLonPoint(destPosition.latitude, destPosition.longitude);
        aMap.clear();
        if (startPosition == null) {
            mStartPoint = mCurPoint;
            startPosition = curPosition;
        }
            aMap.addMarker(new MarkerOptions()
                .position(startPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions()
                .position(destPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(startPosition).include(destPosition).build();
        // 移动地图，所有marker自适应显示。LatLngBounds与地图边缘10像素的填充区域
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }




    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(SearchActivity.this, infomation);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, editCity.getText().toString());
            Inputtips inputTips = new Inputtips(SearchActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }


    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                //startPosition = getLocationFromClient();
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
//                        aMap.addMarker(new MarkerOptions()
//                                .position(startPosition)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(SearchActivity.this,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(SearchActivity.this,
                        R.string.no_result);
            }
        } else {
            ToastUtil.showerror(SearchActivity.this, rCode);
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        // Auto-generated method stub

    }





    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {// 正确返回
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString);
            searchText.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showerror(SearchActivity.this, rCode);
        }


    }


    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(10000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }


    /**
     * 失活定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    /**
     * 定位回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                mCurPoint = new LatLonPoint(mlocationClient.getLastKnownLocation().getLatitude(),
                        mlocationClient.getLastKnownLocation().getLongitude());
                curPosition = AMapUtil.convertToLatLng(mCurPoint);
                //InteractUtil interactUtil = new InteractUtil();
                //interactUtil.updateLocation(mCurPoint);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    /**
     * 从定位信息中获得位置
     */
    private LatLng getLocationFromClient() {
        mCurPoint = new LatLonPoint(mlocationClient.getLastKnownLocation().getLatitude(),
                                                    mlocationClient.getLastKnownLocation().getLongitude());
        curPosition = AMapUtil.convertToLatLng(mCurPoint);
        return AMapUtil.convertToLatLng(mCurPoint);
    }

    /**
     * 搜索路径
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            //ToastUtil.show(mContext, "定位中，稍后再试...");
            mStartPoint = mCurPoint;
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showRouteProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    /**
     * 显示进度框
     */
    private void showRouteProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }


    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        return;
    }

    /**
     * 搜索到路径
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    //mBottomLayout.setVisibility(View.VISIBLE);
                    //int dis = (int) drivePath.getDistance();
                    //int dur = (int) drivePath.getDuration();
                    //String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    //mRotueTimeDes.setText(des);
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        return;
    }

    /**
     * Button点击事件回调方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 点击搜索按钮
             */
            case R.id.search_button:
                searchButton();
                break;
            case R.id.set_date:
                setDate();
                break;
            case R.id.set_time:
                setTime();
                break;
            case R.id.goForMatch:
                goForMatch();
                break;
            default:
                break;
        }
    }

    public void setDate(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String time = dateFormat.format(new Date());
                time = time.substring(11);
                String yearString = String.format("%04d", year);
                String monthString = String.format("%02d", monthOfYear+1);
                String dayOfMonthString = String.format("%02d", dayOfMonth);
                time = yearString + "/" + monthString + "/" + dayOfMonthString + " " +time;
                try {
                    appointTime = dateFormat.parse(time);
                    //tarTimeString = time;
                }catch(Exception e){throw new RuntimeException(e);}

            }
        }, 2016, 5, 30);
        datePickerDialog.show();
    }

    public void setTime(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(SearchActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = dateFormat.format(new Date());
                time = time.substring(0, 11);
                String hourOfDayString = String.format("%02d", hourOfDay);
                String minuteString = String.format("%02d", minute);
                time = time + hourOfDayString + ":" + minuteString + ":" + "00";
                try {
                    Log.e("appointtime",time);
                    appointTime = dateFormat.parse(time);
                    //tarTimeString = time;
                }catch(Exception e){throw new RuntimeException(e);}
            }
        },12, 0, true);
        timePickerDialog.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == realTimeBtn.getId()){
            mPoolType = 1;
            tarTime = new Date();
            tarTimeString = dateFormat.format(tarTime);
            setDateBtn.setVisibility(View.INVISIBLE);
            setTimeBtn.setVisibility(View.INVISIBLE);
        }
        else{
            mPoolType = 2;
            setDateBtn.setVisibility(View.VISIBLE);
            setTimeBtn.setVisibility(View.VISIBLE);
        }
    }


    public class MatchTask extends AsyncTask<Void, Void, Boolean> {
        MatchTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            InteractUtil interactUtil = new InteractUtil();
            if (!interactUtil.socketSuccess){
                mMatchResult = null;
            }
            mMatchResult = interactUtil.match(user, mDriveRouteResult, mPoolType, tarTimeString, startName, destName, mStartPoint, mEndPoint);
            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mMatchTask = null;
            //showProgress(false);
            if (success) {
                Intent matchIntent = new Intent(SearchActivity.this, MatchDetailActivity.class);
                //matchIntent.putExtra("drive_route_result", mDriveRouteResult);
                matchIntent.putExtra("start_name", startName);
                //matchIntent.putExtra("start_point", mStartPoint);
                matchIntent.putExtra("dest_name", destName);
                //matchIntent.putExtra("dest_point", mEndPoint);
                matchIntent.putExtra("pool_type", mPoolType);
                matchIntent.putExtra("time", tarTimeString);
                matchIntent.putExtra("user", user);
                Log.e("putextra matchresult", mMatchResult);
                matchIntent.putExtra("match_result", mMatchResult);
                startActivityForResult(matchIntent, 1);

            }
        }

        @Override
        protected void onCancelled() {
            mMatchTask = null;
        }

    }

}

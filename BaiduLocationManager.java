package com.jiahao.baidulocation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

/**
 * Created by Administrator on 2017/5/25.
 */

public class BaiduLocationManager {

    private static  BaiduLocationManager mInstance;

    private LocationClient locationClient;

    private static BDLocationListener myListener ;

    private LocationBean mLocationBean;

    private Handler mHandler;

    private onLocationListener mLocationListener;

    private BaiduLocationManager(Context context){
        //声明LocationClient类
        locationClient = new LocationClient(context);
        //注册监听函数
        myListener = new MyLocationListener();
        locationClient.registerLocationListener(myListener);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static BaiduLocationManager getInstance(Context context){
        if (mInstance == null && myListener == null){
            mInstance = new BaiduLocationManager(context);
        }
        return mInstance;
    }


    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocationBean = new LocationBean();
            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间
            mLocationBean.setLocationTime(location.getTime());

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型


            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息
            mLocationBean.setLocationLatitude(location.getLatitude());

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息
            mLocationBean.setLocationLongitude(location.getLongitude());

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度

            if (location.getLocType() == BDLocation.TypeGpsLocation){

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息
                mLocationBean.setLocationAddress(location.getAddrStr());

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");


            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息
                mLocationBean.setLocationAddress(location.getAddrStr());

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

            }

            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息
            mLocationBean.setLocationDescribe(location.getLocationDescribe());

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
                mLocationListener.onSuccess(mLocationBean);
            }else{
                mLocationListener.onFile();
            }

           // Log.i("BaiduLocationApiDem", sb.toString());

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    /**
     * 开始定位
     */
    public void startBaiduLocation(){
        locationClient.start();
        initLocation();
    }

    public void getmLocationBean(onLocationListener listener){
       this.mLocationListener = listener;
    }

    /**
     * 结束定位
     */
    public void stopBaiduLocation(){
        locationClient.stop();
    }

    /**
     * 设置参数
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span=1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要


        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        locationClient.setLocOption(option);
    }

    /**
     * 自己定义的bean类
     */
    public class LocationBean {

        //定位时间
        private String locationTime;
        //纬度
        private Double locationLatitude;
        //经度
        private Double locationLongitude;
        //定位位置
        private String locationAddress;
        //语义化
        private String locationDescribe;

        public String getLocationTime() {
            return locationTime;
        }

        public void setLocationTime(String locationTime) {
            this.locationTime = locationTime;
        }

        public Double getLocationLatitude() {
            return locationLatitude;
        }

        public void setLocationLatitude(Double locationLatitude) {
            this.locationLatitude = locationLatitude;
        }

        public Double getLocationLongitude() {
            return locationLongitude;
        }

        public void setLocationLongitude(Double locationLongitude) {
            this.locationLongitude = locationLongitude;
        }

        public String getLocationAddress() {
            return locationAddress;
        }

        public void setLocationAddress(String locationAddress) {
            this.locationAddress = locationAddress;
        }

        public String getLocationDescribe() {
            return locationDescribe;
        }

        public void setLocationDescribe(String locationDescribe) {
            this.locationDescribe = locationDescribe;
        }

        @Override
        public String toString() {
            return "LocationBean{" +
                    "locationTime='" + locationTime + '\'' +
                    ", locationLatitude=" + locationLatitude +
                    ", locationLongitude=" + locationLongitude +
                    ", locationAddress='" + locationAddress + '\'' +
                    ", locationDescribe='" + locationDescribe + '\'' +
                    '}';
        }
    }

    interface  onLocationListener{
        void onSuccess(LocationBean locationBean);

        void onFile();
    }

}

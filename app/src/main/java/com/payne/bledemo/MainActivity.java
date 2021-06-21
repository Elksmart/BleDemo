package com.payne.bledemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.payne.okux.BleCommunication.CompressDataUtils;
import com.payne.okux.BleCommunication.RxBleHelper;
import com.payne.okux.BleCommunication.enu.Result;
import com.payne.okux.BleCommunication.utls.ArrayUtils;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.scan.ScanResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private ScanResult mScanResult;
    private TextView mTvReceive;
    private ScrollView mSvReceive;
    private TextView mTvMac;
    private TextView mTvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvReceive = findViewById(R.id.tv_receive);
        mSvReceive = findViewById(R.id.sv_receive);
        mTvMac = findViewById(R.id.tv_mac);
        mTvStatus = findViewById(R.id.tv_status);
        //连接状态和接收数据回调
        RxBleHelper.getInstance(getApplicationContext()).addBleListener(mOnBleListener);
        //获取权限
        findViewById(R.id.btn_permission).setOnClickListener(v -> {
            checkPerission();
        });
        //扫描设备
        findViewById(R.id.btn_scan).setOnClickListener(v -> {
            scanDevice();
        });
        //连接
        findViewById(R.id.btn_connect).setOnClickListener(v -> {
            connect();
        });
        //断开连接
        findViewById(R.id.btn_disconnect).setOnClickListener(v -> {
            RxBleHelper.getInstance(getApplicationContext()).disconnect();
        });
        //发送数据
        findViewById(R.id.btn_write).setOnClickListener(v -> {
            sendData();
        });
    }

    /**
     * 扫描设备
     */
    private void scanDevice() {
        List<String> filters = new ArrayList<>();
        filters.add("IR");
        RxBleHelper.getInstance(getApplicationContext()).startScan(filters, scanResult -> {
            if (mScanResult == null || !mScanResult.getBleDevice().getMacAddress().equals(scanResult.getBleDevice().getMacAddress())) {
                Log.d("testble", "testscan name:" + scanResult.getBleDevice().getName());
                Log.d("testble", "testscan mac:" + scanResult.getBleDevice().getMacAddress());

                mScanResult = scanResult;
                mTvMac.setText(mScanResult.getBleDevice().getMacAddress());
            }
        }, throwable -> {
            Log.d("testble", "testscan error:" + throwable.getMessage());
        });
    }

    /**
     * 连接设备
     */
    private void connect() {
        if (mScanResult != null) {
            RxBleHelper.getInstance(getApplicationContext()).connect(mScanResult.getBleDevice().getMacAddress(), new Consumer<Result>() {
                @Override
                public void accept(Result result) throws Exception {
                    Log.d("testble", "testconnect result:" + result);
                }
            });
        } else {
            Log.d("testble", "testconnect please first scan device");
        }
    }

    /**
     * 发送数据
     */
    private void sendData() {
        //从第三方获取的美的空调开机指令
        int arrary[] = new int[]{4440, 4400, 544, 1616, 544, 568, 544, 1616, 544, 1616, 544, 568, 544, 568, 544,
                1616, 544, 568, 544, 568, 544, 1616, 544, 568, 544, 568, 544, 1616, 544, 1616, 544, 568, 544, 1616,
                544, 568, 544, 568, 544, 568, 544, 1616, 544, 1616, 544, 1616, 544, 1616, 544, 1616, 544, 1616, 544,
                1616, 544, 1616, 544, 568, 544, 568, 544, 568, 544, 568, 544, 568, 544, 1616, 544, 1616, 544, 568, 544,
                1616, 544, 1616, 544, 568, 544, 568, 544, 568, 544, 568, 544, 568, 544, 1616, 544, 568, 544, 568, 544,
                1616, 544, 1616, 544, 1616, 544, 5320, 4440, 4400, 544, 1616, 544, 568, 544, 1616, 544, 1616, 544, 568,
                544, 568, 544, 1616, 544, 568, 544, 568, 544, 1616, 544, 568, 544, 568, 544, 1616, 544, 1616, 544, 568,
                544, 1616, 544, 568, 544, 568, 544, 568, 544, 1616, 544, 1616, 544, 1616, 544, 1616, 544, 1616, 544, 1616,
                544, 1616, 544, 1616, 544, 568, 544, 568, 544, 568, 544, 568, 544, 568, 544, 1616, 544, 1616, 544, 568, 544,
                1616, 544, 1616, 544, 568, 544, 568, 544, 568, 544, 568, 544, 568, 544, 1616, 544, 568, 544, 568, 544, 1616,
                544, 1616, 544, 1616, 544, 40000
        };
        //压缩指令
        List<byte[]> bytesList = CompressDataUtils.getCompressData(arrary);
        //发送指令
        RxBleHelper.getInstance(getApplicationContext()).sendData(bytesList);
    }

    private void checkPerission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }

    private RxBleHelper.OnBleListener mOnBleListener = new RxBleHelper.OnBleListener() {
        @Override
        public void onNotification(byte[] bytes) {
            Log.d("testble", "接收数据成功: " + ArrayUtils.bytesToHexString(bytes));
            mTvReceive.append(ArrayUtils.bytesToHexString(bytes) + "\n\n");
            mTvReceive.getHandler().post(() -> mSvReceive.scrollBy(0, 1000));
        }

        @Override
        public void onConnectionState(RxBleConnection.RxBleConnectionState state) {
            Log.d("testble", "onConnectionState: " + state);
            mTvStatus.setText(state.toString().replace("RxBleConnectionState", "").replace("{", "").replace("}", ""));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBleHelper.getInstance(getApplicationContext()).removeBleListener(mOnBleListener);
        RxBleHelper.getInstance(getApplicationContext()).disconnect();
    }
}
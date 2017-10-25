package pers.wtt.module_bluetooth.presenter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pers.wtt.module_bluetooth.bean.BlueTooth;
import pers.wtt.module_bluetooth.interfaces.IVBTManager;
import pers.wtt.module_bluetooth.model.IModelBTManager;
import pers.wtt.module_bluetooth.model.impl.ModelBTManagerImpl;

/**
 * Created by 王亭 on 2017/10/24.
 */

public class BTManagerPresenter {

    private IModelBTManager iModelBTManager;
    private IVBTManager ivbtManager;
    private BluetoothAdapter mBluetoothAdapter;
    private ExecutorService mExcutorService;
    private Map<String, BluetoothSocket> bluetoothSocketMap;

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Logger.d("mBluetoothReceiver action ="+action);
            if(BluetoothDevice.ACTION_FOUND.equals(action)){//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(scanDevice == null || scanDevice.getName() == null) return;
                Logger.d("name="+scanDevice.getName()+"address="+scanDevice.getAddress());
                //蓝牙设备名称
                String name = scanDevice.getName();
                if(name != null){
//                    mBluetoothAdapter.cancelDiscovery();
                    BlueTooth blueTooth = new BlueTooth();
                    blueTooth.setName(name);
                    blueTooth.setAddress(scanDevice.getAddress());
                    blueTooth.setBondState(scanDevice.getBondState());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        blueTooth.setType(scanDevice.getType());
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        blueTooth.setUuids(scanDevice.getUuids());
                    }

                    ivbtManager.setBTInfo(blueTooth);
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                mBluetoothAdapter.cancelDiscovery();
                ivbtManager.stopRefresh();
            }
        }
    };

    public BTManagerPresenter(IVBTManager ivbtManager) {
        this.ivbtManager = ivbtManager;
        iModelBTManager = new ModelBTManagerImpl();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mExcutorService = Executors.newCachedThreadPool();
        bluetoothSocketMap = new HashMap<String, BluetoothSocket>();
    }

    public void startScanBT(){
        //如果当前在搜索，就先取消搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        boolean starting = mBluetoothAdapter.startDiscovery();
        Logger.d(starting);
    }

    public void stopScanBT(){
        mBluetoothAdapter.cancelDiscovery();
    }
    public void stopThread(){
        mExcutorService.shutdownNow();
    }

    public void registerBt(Activity ac){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.setPriority(Integer.MAX_VALUE);
        //注册广播
        ac.registerReceiver(mBluetoothReceiver, filter);
    }

    public void unRegisterBt(Activity ac){
        ac.unregisterReceiver(mBluetoothReceiver);
    }

    public void getBondBTs(){
        mExcutorService.submit(new Runnable() {
            @Override
            public void run() {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> mBluetoothDeviceSets = mBluetoothAdapter.getBondedDevices();
                Iterator<BluetoothDevice> mBluetoothDeviceIterator = mBluetoothDeviceSets.iterator();
                List<BlueTooth> blueTooths = new ArrayList<BlueTooth>();
                while (mBluetoothDeviceIterator.hasNext()){
                    BluetoothDevice mBluetoothDevice = mBluetoothDeviceIterator.next();
                    BluetoothSocket mBluetoothSocket = null;
//                    try {
                        BlueTooth blueTooth = new BlueTooth();
                        blueTooth.setName(mBluetoothDevice.getName());
                        blueTooth.setAddress(mBluetoothDevice.getAddress());
                        blueTooth.setBondState(mBluetoothDevice.getBondState());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            blueTooth.setType(mBluetoothDevice.getType());
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            blueTooth.setUuids(mBluetoothDevice.getUuids());
                        }

                        Logger.d("准备：" + mBluetoothDevice.getName());
                        blueTooths.add(blueTooth);
//                        mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001106-0000-1000-8000-00805F9B34FB"));
//                        Logger.d("准备连接：" + mBluetoothDevice.getName());
//                        mBluetoothSocket.connect();
//                        Logger.d("连接状态：" + mBluetoothSocket.isConnected());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Logger.d("连接失败：" + mBluetoothDevice.getName());
//                    }
                }
                ivbtManager.setBondBTInfos(blueTooths);
            }
        });

    }

    public boolean bondBT(BlueTooth blueTooth){
        BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(blueTooth.getAddress());
        Method creMethod = null;
        try {
            creMethod =BluetoothDevice.class
                    .getMethod("createBond");
            Logger.d("开始配对");
            creMethod.invoke(mBluetoothDevice);
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    //模拟串口服务：00001101-0000-1000-8000-00805F9B34FB
    //信息同步服务：00001104-0000-1000-8000-00805F9B34FB
    //文件传输服务：00001106-0000-1000-8000-00805F9B34FB
    public void connection(final BlueTooth blueTooth) {
        if(mBluetoothAdapter.isDiscovering()) {
            stopScanBT();
        }
        mExcutorService.submit(new Runnable() {
            @Override
            public void run() {
                if(blueTooth.getBondState()==BluetoothDevice.BOND_NONE){
                    bondBT(blueTooth);
                }
                try {
                    mBluetoothAdapter.setName(blueTooth.getName());
                    BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(blueTooth.getAddress());
                    final BluetoothSocket mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001106-0000-1000-8000-00805F9B34FB"));
                    Logger.d("连接状态：" + mBluetoothSocket.isConnected());
                    mBluetoothSocket.connect();
                    bluetoothSocketMap.put(blueTooth.getAddress(), mBluetoothSocket);
                    mExcutorService.submit(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               BufferedReader br = new BufferedReader(new InputStreamReader(mBluetoothSocket.getInputStream()));
                               OutputStream outputStream = mBluetoothSocket.getOutputStream();
                               String str = "";
                               while (true){
                                   if((str = br.readLine())!=null) {
                                        ivbtManager.showPrompt("str:"+str);
                                   }
                               }
                           } catch (IOException e) {
                               e.printStackTrace();
                           }

                       }
                   });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

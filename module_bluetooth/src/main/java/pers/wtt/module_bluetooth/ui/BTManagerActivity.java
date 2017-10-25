package pers.wtt.module_bluetooth.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.icu.util.Measure;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.orhanobut.logger.Logger;

import java.util.List;

import pers.wtt.lib_common.base.BaseActivity;
import pers.wtt.module_bluetooth.BTLstAdapter;
import pers.wtt.module_bluetooth.R;
import pers.wtt.module_bluetooth.bean.BlueTooth;
import pers.wtt.module_bluetooth.interfaces.IVBTManager;
import pers.wtt.module_bluetooth.presenter.BTManagerPresenter;

/**
 * Created by 王亭 on 2017/10/23.
 */

public class BTManagerActivity extends BaseActivity implements IVBTManager {

    private EasyRecyclerView erv_btBond;
    private BTLstAdapter btBondAdapter;
    private EasyRecyclerView erv_btLst;
    private BTLstAdapter btLstAdapter;
    private BTManagerPresenter btManagerPresenter;
    private final int REQUEST_PERMISSION_BT = 111;

    // onResume 中进行调用
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //TODO 提示权限已经被禁用 且不在提示
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_BT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_BT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO 请求权限成功
                    Logger.d("请求权限成功");
                } else {
                    //TODO 提示权限已经被禁用
                    Logger.d("提示权限已经被禁用");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btmanager);

        erv_btBond = (EasyRecyclerView) findViewById(R.id.erv_btBond);
        erv_btBond.setLayoutManager(new LinearLayoutManager(this));
        btBondAdapter = new BTLstAdapter(this);
        btBondAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BlueTooth blueTooth = (BlueTooth) btBondAdapter.getItem(position);
                btManagerPresenter.connection(blueTooth);
            }
        });
        erv_btBond.setAdapter(btBondAdapter);

        erv_btLst = (EasyRecyclerView) findViewById(R.id.erv_btLst);
        erv_btLst.setLayoutManager(new LinearLayoutManager(this));
        btLstAdapter = new BTLstAdapter(this);
        btLstAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BlueTooth blueTooth = (BlueTooth) btLstAdapter.getItem(position);
                btManagerPresenter.connection(blueTooth);
            }
        });

        erv_btLst.setAdapter(btLstAdapter);
        erv_btLst.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                btLstAdapter.clear();
                btManagerPresenter.startScanBT();
            }
        });

        btManagerPresenter = new BTManagerPresenter(this);
        btManagerPresenter.getBondBTs();
        btManagerPresenter.registerBt(this);
        btManagerPresenter.startScanBT();
    }

    @Override
    public void setBondBTInfos(final List<BlueTooth> btInfos) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            btBondAdapter.addAll(btInfos);
            }
        });
    }

    @Override
    public void setBTInfo(final BlueTooth btInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btLstAdapter.add(btInfo);
            }
        });

    }

    @Override
    public void showPrompt(String msg) {

    }

    @Override
    public void stopRefresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                erv_btLst.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            btManagerPresenter.stopScanBT();
            btManagerPresenter.unRegisterBt(this);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        btManagerPresenter.stopThread();
    }
}

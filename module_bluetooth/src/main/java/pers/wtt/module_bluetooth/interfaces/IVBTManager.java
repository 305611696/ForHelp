package pers.wtt.module_bluetooth.interfaces;

import java.util.List;

import pers.wtt.module_bluetooth.bean.BlueTooth;

/**
 * Created by 王亭 on 2017/10/24.
 */

public interface IVBTManager {


    /**
     * 添加绑定蓝牙数据
     * @param btInfos
     */
    void setBondBTInfos(List<BlueTooth> btInfos);

    /**
     * 添加蓝牙数据
     * @param btInfo
     */
    void setBTInfo(BlueTooth btInfo);

    /**
     * 提示
     * @param msg
     */
    void showPrompt(String msg);

    /**
     * 停止刷新
     */
    void stopRefresh();
}

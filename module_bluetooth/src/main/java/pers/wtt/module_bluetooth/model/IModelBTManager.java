package pers.wtt.module_bluetooth.model;

import java.util.List;
import pers.wtt.module_bluetooth.bean.BlueTooth;
import pers.wtt.module_bluetooth.presenter.BTManagerPresenter;

/**
 * Created by 王亭 on 2017/10/24.
 */

public interface IModelBTManager {

    /**
     * 获取蓝牙设备
     * @return
     * @param callback
     */
    List<BlueTooth> getBTInfos();

}

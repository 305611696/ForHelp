package pers.wtt.module_bluetooth.bean;

import android.os.ParcelUuid;

/**
 * Created by 王亭 on 2017/10/23.
 */

public class BlueTooth {

    private ParcelUuid[] uuids;
    private String name;
    private int type;
    private int bondState;
    private String address;

    public ParcelUuid[] getUuids() {
        return uuids;
    }

    public void setUuids(ParcelUuid[] uuids) {
        this.uuids = uuids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBondState() {
        return bondState;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

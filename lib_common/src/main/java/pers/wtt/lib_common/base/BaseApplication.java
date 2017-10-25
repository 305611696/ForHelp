package pers.wtt.lib_common.base;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王亭 on 2017/10/23.
 */

public class BaseApplication extends Application {

    private static BaseApplication mBaseApplication;
    private List<Activity> acs = new ArrayList<Activity>();

    public static BaseApplication getIns() {
        return mBaseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBaseApplication = this;
    }

    public void addActivity(Activity ac) {
        acs.add(ac);
    }

    public void finishActivity(Activity ac){
        acs.remove(ac);
        ac.finish();
        ac = null;
    }

    public void finishAllAc(){
        for (int i = 0; i<acs.size(); i++){
            finishActivity(acs.get(i));
        }
    }
}

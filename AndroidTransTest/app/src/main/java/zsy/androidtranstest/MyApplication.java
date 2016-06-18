package zsy.androidtranstest;

import android.app.Application;

/**
 * Created by zsy on 16/6/16.
 */
public class MyApplication extends Application{

    public String serverIP;
    public String picSavePath;
    public String stitchServer;
    public String newOldServer;

    @Override
    public void onCreate() {
        super.onCreate();
        serverIP = "192.168.1.107";
        picSavePath = "picture";
        stitchServer = "UpdateServer";
        newOldServer = "NewOldImageServer";
    }


}

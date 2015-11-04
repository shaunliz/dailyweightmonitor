package com.brs.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.brs.constvalue.ConstValues;
import com.brs.dailyweightmonitor.MainActivity;

import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ikban on 2015-11-02.
 */

// TODO: auto update
public class VersionControl {
    public Context MainContext;
    public String VERSION = null;
    public Runnable VersionCheck = null;

    public int LATEST_VERSION = 0;
    public int CURRENT_VERSION = 0;

    public VersionControl(final Context context){
        MainContext = context;

        VersionCheck = new Runnable() {
            // 까페 글제목으로 부터 정보 획득하여 버전 업그레이드 여부 체크.
            @Override
            public void run() {
                VERSION = getURLtoText(ConstValues.VERSION_CHECK_URL);

                if(VERSION != null && VERSION.contains("DWM_VERSION_CODE"))
                {
                    int str_idx = VERSION.indexOf("DWM_VERSION_CODE");

                    try{
                        PackageInfo pkg_info = MainContext.getPackageManager().getPackageInfo(MainContext.getPackageName(), 0);
                        CURRENT_VERSION = pkg_info.versionCode;
                        DM.i(this, "CURRENT_VERSION:[" + CURRENT_VERSION + "]");
                    }catch(PackageManager.NameNotFoundException e){
                        e.printStackTrace();
                    }
                    DM.i("DWM_VERSION_CODE / str_idx:" + str_idx);
                    LATEST_VERSION = Integer.parseInt(VERSION.substring(str_idx + 17, str_idx +19));
                    DM.i("LATEST_VERSION:[" + LATEST_VERSION + "]");

                    if(LATEST_VERSION > CURRENT_VERSION){
                        DM.v(this, "****  업그레이드 필요함  ****");
                        ((MainActivity)MainContext).sendUpgradeMsg();
                    }
                    else{
                        DM.v(this, "----  업그레이드 필요 없음  ----");
                    }
                }
                else{
                    DM.i("버전 획득 실패");
                    VERSION = null;
                    LATEST_VERSION = 0;
                    CURRENT_VERSION = 0;
                }
            }
        };
    }


    // version check 실행 함수
    public void checkVersionUpgrade()
    {
        Thread thread = new Thread(VersionCheck);
        thread.start();
    }

    // 정보 획득
    public String getURLtoText(String UrlString)
    {
        Source source = null;
        String content = null;

        try{
            source = new Source(new URL(UrlString));
            ((Source) source).fullSequentialParse();
            content = source.getTextExtractor().toString();
            content = new String(content.getBytes(source.getEncoding()), "UTF-8");
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return content;
    }
}

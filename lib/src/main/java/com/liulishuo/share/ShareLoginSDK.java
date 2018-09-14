package com.liulishuo.share;

import java.util.Arrays;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.qq.QQPlatform;
import kale.sharelogin.weibo.WeiBoPlatform;
import kale.sharelogin.weixin.WeiXinPlatform;

/**
 * Created by echo on 5/18/15.
 */
public class ShareLoginSDK {

    public static void init(Application application, @NonNull SlConfig cfg) {
        ShareLoginLib.init(application, SlConfig.appName, SlConfig.pathTemp, SlConfig.isDebug);

        ArrayMap<String, String> map = new ArrayMap<>();
        
        map.put(QQPlatform.KEY_APP_ID, SlConfig.qqAppId);
        map.put(QQPlatform.KEY_SCOPE, SlConfig.qqScope);

        map.put(WeiBoPlatform.KEY_APP_KEY, SlConfig.weiBoAppId);
        map.put(WeiBoPlatform.KEY_REDIRECT_URL, SlConfig.weiBoRedirectUrl);
        map.put(WeiBoPlatform.KEY_SCOPE, SlConfig.weiBoScope);

        map.put(WeiXinPlatform.KEY_APP_ID, SlConfig.weiXinAppId);
        map.put(WeiXinPlatform.KEY_SECRET_KEY, SlConfig.weiXinSecret);
        
        ShareLoginLib.initPlatforms(map, Arrays.asList(
                QQPlatform.class, WeiBoPlatform.class, WeiXinPlatform.class
        ));
    }

    public static boolean isQQInstalled(@NonNull Context context) {
        return ShareLoginLib.isAppInstalled(context, QQPlatform.class);
    }

    public static boolean isWeiBoInstalled(@NonNull Context context) {
        return ShareLoginLib.isAppInstalled(context, WeiBoPlatform.class);
    }

    public static boolean isWeiXinInstalled(Context context) {
        return ShareLoginLib.isAppInstalled(context, WeiXinPlatform.class);
    }

}

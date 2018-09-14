package com.liulishuo.engzo;

import android.app.Application;
import android.util.Log;

import com.liulishuo.share.ShareLoginSDK;
import com.liulishuo.share.SlConfig;

/**
 * @author Kale
 * @date 2016/8/11
 */
public class AppApplication extends Application {

    private static final String TAG = "Application";

    protected static String qq_app_id, qq_scope,
            weibo_app_key, weibo_scope, weibo_redirect_url,
            weixin_app_id, weixin_secret;

    @Override
    public void onCreate() {
        super.onCreate();

        initConstant();

        Log.d(TAG, "onCreate: weixin:" + ShareLoginSDK.isWeiXinInstalled(this));
        Log.d(TAG, "onCreate: weibo:" + ShareLoginSDK.isWeiBoInstalled(this));
        Log.d(TAG, "onCreate: qq:" + ShareLoginSDK.isQQInstalled(this));

        SlConfig cfg = new SlConfig.Builder()
                .debug(true)
                .appName("test app")
                .picTempFile(null)
                .qq(qq_app_id, qq_scope)
                .weiBo(weibo_app_key, weibo_redirect_url, weibo_scope)
                .weiXin(weixin_app_id, weixin_secret)
                .build();

        ShareLoginSDK.init(this, cfg);
    }

    /**
     * 初始化一些常量
     */
    protected void initConstant() {
        qq_app_id = "xxxxxxxxxxxx";
        qq_scope = "xxxxxxxxxxxx";
        weibo_app_key = "xxxxxxxxxxxx";
        weibo_redirect_url = "xxxxxxxxxxxx";
        weixin_app_id = "xxxxxxxxxxxx";
        weixin_secret = "xxxxxxxxxxxx";
        weibo_scope = "xxxxxxxxxxxx";
    }
}

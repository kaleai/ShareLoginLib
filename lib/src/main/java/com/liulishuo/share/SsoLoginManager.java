package com.liulishuo.share;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.type.SsoLoginType;

import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.qq.QQPlatform;
import kale.sharelogin.weibo.WeiBoPlatform;
import kale.sharelogin.weixin.WeiXinPlatform;

import static com.liulishuo.share.type.SsoLoginType.QQ;
import static com.liulishuo.share.type.SsoLoginType.WEIBO;
import static com.liulishuo.share.type.SsoLoginType.WEIXIN;

/**
 * @author Kale
 * @date 2016/3/30
 */
public class SsoLoginManager {

    public static void login(
            @NonNull Activity activity,
            @SsoLoginType String type,
            @Nullable LoginListener listener) {

        login(activity, type, listener, null);
    }

    /**
     * @param weixinCodeRespListener 得到微信code的listener。如果不为空，loginListener将不会被自动调用，必须要手动调用。
     */
    public static void login(@NonNull Activity activity,
            @SsoLoginType String type,
            @Nullable final LoginListener listener,
            @Nullable WXLoginRespListener weixinCodeRespListener) {

        String LoginType = null;

        switch (type) {
            case QQ:
                LoginType = QQPlatform.LOGIN;
                break;
            case WEIBO:
                LoginType = WeiBoPlatform.LOGIN;
                break;
            case WEIXIN:
                LoginType = WeiXinPlatform.LOGIN;
                break;
        }

        ShareLoginLib.doLogin(activity, LoginType, new kale.sharelogin.LoginListener() {

            @Override
            public void onReceiveToken(String accessToken, String uId, long expiresIn, @Nullable String wholeData) {
                super.onReceiveToken(accessToken, uId, expiresIn, wholeData);
                if (listener != null) {
                    listener.onSuccess(accessToken, uId, expiresIn, wholeData);
                }
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (listener != null) {
                    listener.onCancel();
                }
            }

            @Override
            public void onError(String errorMsg) {
                super.onError(errorMsg);
                if (listener != null) {
                    listener.onError(errorMsg);
                }
            }

            @Override
            public void onComplete() {
                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }

    public static class LoginListener {

        public void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData) {
        }

        public void onError(String errorMsg) {
        }

        public void onCancel() {
        }

        protected void onComplete() {
        }
    }

    public interface WXLoginRespListener {

        void onLoginResp(String respCode, SsoLoginManager.LoginListener listener);
    }

}

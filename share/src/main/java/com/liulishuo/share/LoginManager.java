package com.liulishuo.share;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.activity.SL_QQHandlerActivity;
import com.liulishuo.share.activity.SL_WeiBoHandlerActivity;
import com.liulishuo.share.activity.SL_WeiXinHandlerActivity;
import com.liulishuo.share.type.LoginType;

import static com.liulishuo.share.type.LoginType.QQ;
import static com.liulishuo.share.type.LoginType.WEIBO;
import static com.liulishuo.share.type.LoginType.WEIXIN;

/**
 * @author Kale
 * @date 2016/3/30
 */
public class LoginManager {

    @Nullable
    public static LoginListener listener;

    public static void login(@NonNull Activity activity, @LoginType String type, @Nullable LoginListener listener) {
        login(activity, type, listener, null);
    }

    /**
     * @param respListener 得到微信code的listener。如果不为空，loginListener将不会被自动调用，必须要手动调用。
     */
    public static void login(@NonNull Activity activity, @LoginType String type,
            @Nullable LoginListener listener, @Nullable LoginRespListener respListener) {
        LoginManager.listener = listener;
        switch (type) {
            case QQ:
                if (ShareBlock.isQQInstalled(activity)) {
                    activity.startActivity(
                            new Intent(activity, SL_QQHandlerActivity.class)
                                    .putExtra(ShareBlock.KEY_IS_LOGIN_TYPE, true)
                    );
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    if (listener != null) {
                        listener.onError("未安装QQ");
                    }
                }
                break;
            case WEIBO:
                activity.startActivity(
                        new Intent(activity, SL_WeiBoHandlerActivity.class)
                                .putExtra(ShareBlock.KEY_IS_LOGIN_TYPE, true)
                );
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case WEIXIN:
                if (ShareBlock.isWeiXinInstalled(activity)) {
                    SL_WeiXinHandlerActivity.respListener = respListener;
                    SL_WeiXinHandlerActivity.login(activity.getApplicationContext());
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    if (listener != null) {
                        listener.onError("未安装微信");
                    }
                }
                break;
        }
    }

    public static void recycle() {
        listener = null;
        SL_WeiXinHandlerActivity.respListener = null;
    }

    public interface LoginListener {

        void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData);

        void onError(String msg);

        void onCancel();
    }

    public interface LoginRespListener {

        void onLoginResp(String respCode, LoginManager.LoginListener listener);
    }

}

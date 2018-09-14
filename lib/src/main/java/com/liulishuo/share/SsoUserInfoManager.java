package com.liulishuo.share;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.type.SsoLoginType;

import kale.sharelogin.IPlatform;
import kale.sharelogin.LoginListener;
import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.qq.QQPlatform;
import kale.sharelogin.weibo.WeiBoPlatform;
import kale.sharelogin.weixin.WeiXinPlatform;

/**
 * @author Kale
 * @date 2016/4/5
 */
public class SsoUserInfoManager {

    public static void getQQUserInfo(Context context, @NonNull final String accessToken, @NonNull final String uid,
            @Nullable final UserInfoListener listener) {
        getUserInfo(context, SsoLoginType.QQ, accessToken, uid, listener);
    }

    public static void getWeiBoUserInfo(Context context, final @NonNull String accessToken, final @NonNull String uid,
            @Nullable final UserInfoListener listener) {
        getUserInfo(context, SsoLoginType.WEIBO, accessToken, uid, listener);
    }

    public static void getWeiXinUserInfo(Context context, @NonNull final String accessToken, @NonNull final String uid,
            @Nullable final UserInfoListener listener) {
        getUserInfo(context, SsoLoginType.WEIXIN, accessToken, uid, listener);
    }
    
    public static void getUserInfo(Context context, @SsoLoginType String type, @NonNull String accessToken, @NonNull String uid,
            @Nullable final UserInfoListener listener) {

        Class<? extends IPlatform> platform = null;
        
        switch (type) {
            case SsoLoginType.QQ:
                platform = QQPlatform.class;
                break;
            case SsoLoginType.WEIBO:
                platform = WeiBoPlatform.class;
                break;
            case SsoLoginType.WEIXIN:
                platform = WeiXinPlatform.class;
                break;
        }

        ShareLoginLib.getUserInfo(context, platform, accessToken, uid, new LoginListener() {
            @Override
            public void onReceiveUserInfo(@NonNull kale.sharelogin.OAuthUserInfo userInfo) {
                super.onReceiveUserInfo(userInfo);
                OAuthUserInfo info = new OAuthUserInfo();
                info.headImgUrl = userInfo.headImgUrl;
                info.nickName = userInfo.nickName;
                info.sex = userInfo.sex;
                info.userId = userInfo.userId;
                if (listener != null) {
                    listener.onSuccess(info);
                }
            }

            @Override
            public void onError(String errorMsg) {
                super.onError(errorMsg);
                if (listener != null) {
                    listener.onError(errorMsg);
                }
            }
        });
    }

    public interface UserInfoListener {

        void onSuccess(@NonNull OAuthUserInfo userInfo);

        void onError(String msg);
    }
}

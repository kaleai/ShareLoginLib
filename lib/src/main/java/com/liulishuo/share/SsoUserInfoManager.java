package com.liulishuo.share;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.type.SsoLoginType;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kale
 * @date 2016/4/5
 */
public class SsoUserInfoManager {

    public static void getUserInfo(Context context, @SsoLoginType String type, @NonNull String accessToken, @NonNull String uid,
            @Nullable final UserInfoListener listener) {
        switch (type) {
            case SsoLoginType.QQ:
                getQQUserInfo(context, accessToken, uid, listener);
                break;
            case SsoLoginType.WEIBO:
                getWeiBoUserInfo(context, accessToken, uid, listener);
                break;
            case SsoLoginType.WEIXIN:
                getWeiXinUserInfo(context, accessToken, uid, listener);
                break;
        }
    }

    /**
     * 得到qq的用户信息
     * 
     * @see "http://wiki.open.qq.com/wiki/website/get_simple_userinfo"
     */
    public static void getQQUserInfo(Context context, @NonNull final String accessToken, @NonNull final String userId,
            @Nullable final UserInfoListener listener) {

        AsyncWeiboRunner runner = new AsyncWeiboRunner(context);
        WeiboParameters params = new WeiboParameters(null);
        params.put("access_token", accessToken);
        params.put("openid", userId);
        params.put("oauth_consumer_key", SlConfig.qqAppId);
        params.put("format", "json");

        runner.requestAsync("https://graph.qq.com/user/get_simple_userinfo", params, "GET", new UserInfoRequestListener(listener) {
            @Override
            OAuthUserInfo onSuccess(JSONObject jsonObj) throws JSONException {
                OAuthUserInfo userInfo = new OAuthUserInfo();
                userInfo.nickName = jsonObj.getString("nickname");
                userInfo.sex = jsonObj.getString("gender");
                userInfo.headImgUrl = jsonObj.getString("figureurl_qq_1");
                userInfo.userId = userId;
                return userInfo;
            }
        });
    }

    /**
     * 得到微博用户的信息
     *
     * @see "http://open.weibo.com/wiki/2/users/show"
     */
    public static void getWeiBoUserInfo(Context context, final @NonNull String accessToken, final @NonNull String uid,
            @Nullable final UserInfoListener listener) {

        AsyncWeiboRunner runner = new AsyncWeiboRunner(context);
        WeiboParameters params = new WeiboParameters(null);
        params.put("access_token", accessToken);
        params.put("uid", uid);
        runner.requestAsync("https://api.weibo.com/2/users/show.json", params, "GET", new UserInfoRequestListener(listener) {
            @Override
            OAuthUserInfo onSuccess(JSONObject jsonObj) throws JSONException {
                OAuthUserInfo userInfo = new OAuthUserInfo();
                userInfo.nickName = jsonObj.getString("screen_name");
                userInfo.sex = jsonObj.getString("gender");
                userInfo.headImgUrl = jsonObj.getString("avatar_large");
                userInfo.userId = jsonObj.getString("id");
                return userInfo;
            }
        });

    }

    ///////////////////////////////////////////////////////////////////////////
    // 微信
    ///////////////////////////////////////////////////////////////////////////

    public static void getWeiXinUserInfo(Context context, @NonNull final String accessToken, @NonNull final String uid,
            @Nullable final UserInfoListener listener) {

        AsyncWeiboRunner runner = new AsyncWeiboRunner(context);
        WeiboParameters params = new WeiboParameters(null);
        params.put("access_token", accessToken);
        params.put("openid", uid);

        runner.requestAsync("https://api.weixin.qq.com/sns/userinfo", params, "GET", new UserInfoRequestListener(listener) {

            @Override
            OAuthUserInfo onSuccess(JSONObject jsonObj) throws JSONException {
                OAuthUserInfo userInfo = new OAuthUserInfo();
                userInfo.nickName = jsonObj.getString("nickname");
                userInfo.sex = jsonObj.getString("sex");
                // 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
                userInfo.headImgUrl = jsonObj.getString("headimgurl");
                userInfo.userId = jsonObj.getString("unionid");
                return userInfo;
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Common
    ///////////////////////////////////////////////////////////////////////////

    private abstract static class UserInfoRequestListener implements RequestListener {

        private UserInfoListener listener;

        UserInfoRequestListener(UserInfoListener listener) {
            this.listener = listener;
        }

        @Override
        public void onComplete(String s) {
            OAuthUserInfo userInfo = null;
            try {
                userInfo = onSuccess(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
            if (listener != null && userInfo != null) {
                listener.onSuccess(userInfo);
            }
        }

        abstract OAuthUserInfo onSuccess(JSONObject jsonObj) throws JSONException;

        @Override
        public void onWeiboException(WeiboException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }
    }

    public interface UserInfoListener {

        void onSuccess(@NonNull OAuthUserInfo userInfo);

        void onError(String msg);
    }
}

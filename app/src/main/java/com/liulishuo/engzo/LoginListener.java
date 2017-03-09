package com.liulishuo.engzo;

import com.liulishuo.share.AuthUserInfo;
import com.liulishuo.share.LoginManager;
import com.liulishuo.share.UserInfoManager;
import com.liulishuo.share.type.LoginType;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Kale
 * @date 2016/4/5
 */
class LoginListener implements LoginManager.LoginListener {

    private static final String TAG = "LoginListener";

    private MainActivity activity;

    private
    @LoginType
    String type;

    LoginListener(MainActivity activity, @LoginType String type) {
        this.activity = activity;
        this.type = type;
    }

    @Override
    public void onSuccess(String accessToken, String userId, long expiresIn, String data) {
        Log.d(TAG, "accessToken = " + accessToken + "\nuid = " + userId + "\nexpires_in = " + expiresIn);
        loadUserInfo(accessToken, userId);
        
        String result = "登录成功";
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    @Override
    public void onError(String msg) {
        String result = "登录失败,失败信息：" + msg;
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    @Override
    public void onCancel() {
        String result = "取消登录";
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    /**
     * 加载用户的个人信息
     */
    private void loadUserInfo(String accessToken, String userId) {
        UserInfoManager.getUserInfo(activity, type, accessToken, userId, new UserInfoManager.UserInfoListener() {
            @Override
            public void onSuccess(@NonNull final AuthUserInfo userInfo) {
                final String info = " nickname = " + userInfo.nickName + "\n"
                        + " sex = " + userInfo.sex + "\n"
                        + " id = " + userInfo.userId;

                activity.onGotUserInfo(info, userInfo.headImgUrl);
            }

            @Override
            public void onError(String msg) {
                activity.onGotUserInfo(" 出错了！\n" + msg, null);
            }
        });
    }
}

package com.liulishuo.share;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.type.ShareContentType;
import com.liulishuo.share.type.SsoShareType;

import kale.sharelogin.ShareListener;
import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.content.ShareContentPic;
import kale.sharelogin.content.ShareContentText;
import kale.sharelogin.content.ShareContentWebPage;
import kale.sharelogin.qq.QQPlatform;
import kale.sharelogin.weibo.WeiBoPlatform;
import kale.sharelogin.weixin.WeiXinPlatform;

import static com.liulishuo.share.type.SsoShareType.QQ_FRIEND;
import static com.liulishuo.share.type.SsoShareType.QQ_ZONE;
import static com.liulishuo.share.type.SsoShareType.WEIBO_TIME_LINE;
import static com.liulishuo.share.type.SsoShareType.WEIXIN_FAVORITE;
import static com.liulishuo.share.type.SsoShareType.WEIXIN_FRIEND;
import static com.liulishuo.share.type.SsoShareType.WEIXIN_FRIEND_ZONE;

/**
 * @author Kale
 * @date 2016/3/30
 */
public class SsoShareManager {

    public static void share(@NonNull final Activity activity,
            @SsoShareType final String shareType,
            @NonNull final ShareContent shareContent,
            @Nullable final ShareStateListener listener) {

        String type = null;

        switch (shareType) {
            case QQ_FRIEND:
                type = QQPlatform.FRIEND;
                break;
            case QQ_ZONE:
                type = QQPlatform.ZONE;
                break;
            case WEIBO_TIME_LINE:
                type = WeiBoPlatform.TIME_LINE;
                break;
            case WEIXIN_FRIEND:
                type = WeiXinPlatform.FRIEND;
                break;
            case WEIXIN_FRIEND_ZONE:
                type = WeiXinPlatform.FRIEND_ZONE;
                break;
            case WEIXIN_FAVORITE:
                type = WeiXinPlatform.FAVORITE;
                break;
        }

        kale.sharelogin.content.ShareContent newContent = null;

        switch (shareContent.getType()) {
            case ShareContentType.TEXT:
                newContent = new ShareContentText(shareContent.getSummary());
                break;
            case ShareContentType.PIC:
                newContent = new ShareContentPic(((com.liulishuo.share.content.ShareContentPic) shareContent).getLargeBmp());
                break;
            case ShareContentType.WEBPAGE:
                newContent = new ShareContentWebPage(
                        shareContent.getTitle(),
                        shareContent.getSummary(),
                        shareContent.getURL(),
                        ((com.liulishuo.share.content.ShareContentPic) shareContent).getThumbBmp());
                break;
        }

        ShareLoginLib.doShare(activity, type, newContent, new ShareListener() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                if (listener != null) {
                    listener.onSuccess();
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

    public static class ShareStateListener {

        public void onSuccess() {
        }

        public void onCancel() {
        }

        public void onError(String msg) {
        }

        protected void onComplete() {
        }
    }

}

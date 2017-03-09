package com.liulishuo.share.type;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.support.annotation.StringDef;

/**
 * @author Kale
 * @date 2016/3/30
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({ShareType.WEIXIN_FRIEND,
        ShareType.WEIXIN_FRIEND_ZONE,
        ShareType.WEIXIN_FAVORITE,
        ShareType.WEIBO_TIME_LINE,
        ShareType.QQ_ZONE, ShareType.QQ_FRIEND})
public @interface ShareType {

    String
            QQ_ZONE = "QQ_ZONE", QQ_FRIEND = "QQ_FRIEND",
            WEIBO_TIME_LINE = "WEIBO_TIME_LINE",
            WEIXIN_FRIEND = "WEIXIN_FRIEND", WEIXIN_FRIEND_ZONE = "WEIXIN_FRIEND_ZONE", WEIXIN_FAVORITE = "WEIXIN_FAVORITE";
}
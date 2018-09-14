package com.liulishuo.share.content;

import com.liulishuo.share.type.ShareContentType;

/**
 * Created by echo on 5/18/15.
 */

public interface ShareContent {

    /**
     * @return 分享的方式
     */
    @ShareContentType
    int getType();

    /**
     * 分享的描述信息(摘要)
     */
    String getSummary();

    /**
     * 分享的标题
     */
    String getTitle();

    /**
     * 获取跳转的链接
     */
    String getURL();

}

package com.liulishuo.share.content;

import java.io.Serializable;

import com.liulishuo.share.type.ContentType;

/**
 * Created by echo on 5/18/15.
 */

public interface ShareContent extends Serializable {

    /**
     * @return 分享的方式
     */
    @ContentType
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

    /**
     * 分享的缩略图片
     */
    byte[] getThumbBmpBytes();

    /**
     * 分享的大图
     */
    String getLargeBmpPath();

    /**
     * 音频url
     */
    String getMusicUrl();

}

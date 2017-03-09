package com.liulishuo.share.content;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.type.ContentType;

/**
 * Created by echo on 5/18/15.
 * 分享网页模式
 */
public class ShareContentWebPage extends ShareContentPic {

    protected final String title;

    private final String summary;

    private final String url;

    /**
     * @param title   标题
     * @param summary 描述
     * @param url     点击分享的内容后跳转的链接
     * @param thumb   图片的bitmap。保证在32kb以内,如果要分享图片，那么必传
     * @param large   大图的bitmap。10m以内，如果要分享图片，那么必传
     */
    public ShareContentWebPage(@NonNull String title, @NonNull String summary, String url,
            @Nullable Bitmap thumb, @Nullable Bitmap large) {
        super(thumb, large);
        this.title = title;
        this.summary = summary;
        this.url = url;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public int getType() {
        return ContentType.WEBPAGE;
    }

}

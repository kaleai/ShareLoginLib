package com.liulishuo.share.content;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.type.ShareContentType;

/**
 * Created by echo on 5/18/15.
 * 分享网页模式
 */
public class ShareContentWebPage extends ShareContentPic {

    private String title, summary, url;

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

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int getType() {
        return ShareContentType.WEBPAGE;
    }
}

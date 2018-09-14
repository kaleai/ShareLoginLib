package com.liulishuo.share.content;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.liulishuo.share.type.ShareContentType;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic implements ShareContent {

    private Bitmap thumbBmp, largeBmp;

    /**
     * @param thumbBmp 如果需要分享图片，则必传
     */
    public ShareContentPic(@Nullable Bitmap thumbBmp, @Nullable Bitmap largeBmp) {
        this.thumbBmp = thumbBmp;
        this.largeBmp = largeBmp;
    }

    @ShareContentType
    @Override
    public int getType() {
        return ShareContentType.PIC;
    }

    @Override
    public String getSummary() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    public Bitmap getThumbBmp() {
        return thumbBmp;
    }

    public Bitmap getLargeBmp() {
        return largeBmp;
    }
}

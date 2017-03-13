package com.liulishuo.share.content;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.type.ContentType;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic implements ShareContent {

    /**
     * 图片的byte数组
     */
    private byte[] thumbBmpBytes;

    private String largeBmpPath;

    /**
     * @param thumbBmp 如果需要分享图片，则必传
     */
    public ShareContentPic(@Nullable Bitmap thumbBmp, @Nullable Bitmap largeBmp) {
        if (thumbBmp != null) {
            thumbBmpBytes = getImageThumbByteArr(thumbBmp);
        }
        if (largeBmp != null) {
            largeBmpPath = saveLargeBitmap(largeBmp);
        }
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

    @Override
    public byte[] getThumbBmpBytes() {
        return thumbBmpBytes;
    }

    @Override
    public String getLargeBmpPath() {
        return largeBmpPath;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @ContentType
    @Override
    public int getType() {
        return ContentType.PIC;
    }

    /**
     * Note:外部传入的bitmap可能会被用于其他的地方，所以这里不能做recycle()
     */
    private
    @Nullable
    byte[] getImageThumbByteArr(@NonNull Bitmap src) {
        final Bitmap bitmap;
        if (src.getWidth() > 250 || src.getHeight() > 250) {
            bitmap = ThumbnailUtils.extractThumbnail(src, 250, 250);
        } else {
            bitmap = src;
        }

        byte[] thumbData = null;
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            thumbData = outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitmap != null && !bitmap.isRecycled()) {
//                bitmap.recycle();
            }
        }
        return thumbData;
    }

    /**
     * 此方法是耗时操作，如果对于特别大的图，那么需要做异步
     *
     * Note:外部传入的bitmap可能会被用于其他的地方，所以这里不能做recycle()
     */
    private String saveLargeBitmap(final Bitmap bitmap) {
        String path = ShareBlock.Config.pathTemp;
        if (!TextUtils.isEmpty(path)) {
            String imagePath = path + "sl_large_pic";
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bitmap != null && !bitmap.isRecycled()) {
//                    bitmap.recycle();
                }
            }
            return imagePath;
        } else {
            return null;
        }
    }
}

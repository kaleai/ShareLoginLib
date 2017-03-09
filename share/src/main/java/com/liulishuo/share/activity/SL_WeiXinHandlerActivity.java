package com.liulishuo.share.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.liulishuo.share.LoginManager;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.ShareManager;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.type.ContentType;
import com.liulishuo.share.type.ShareType;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by echo on 5/19/15.
 * 用来处理微信登录、微信分享的activity。这里真不知道微信非要个activity干嘛，愚蠢的设计!
 * 参考文档: https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317853&lang=zh_CN
 */
public class SL_WeiXinHandlerActivity extends Activity implements IWXAPIEventHandler {

    /**
     * BaseResp的getType函数获得的返回值。1:第三方授权， 2:分享
     */
    private static final int TYPE_LOGIN = 1;

    private IWXAPI api;

    public static LoginManager.LoginRespListener respListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, ShareBlock.Config.weiXinAppId, true);
        api.handleIntent(getIntent(), this);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (api != null) {
            api.handleIntent(getIntent(), this);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp != null) {
            if (resp instanceof SendAuth.Resp && resp.getType() == TYPE_LOGIN) {
                parseLoginResp(this, (SendAuth.Resp) resp, LoginManager.listener);
            } else {
                parseShareResp(resp, ShareManager.listener);
            }
        }
        finish();
    }

    ///////////////////////////////////////////////////////////////////////////
    // login
    ///////////////////////////////////////////////////////////////////////////

    public static void login(@NonNull Context context) {
        String appId = ShareBlock.Config.weiXinAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化WeiXinAppId");
        }

        IWXAPI api = WXAPIFactory.createWXAPI(context.getApplicationContext(), appId, true);
        api.registerApp(appId);

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        api.sendReq(req); // 这里的请求的回调会在activity中收到，然后通过parseLoginResp方法解析
    }

    /**
     * 解析用户登录的结果
     */
    protected void parseLoginResp(final Activity activity, SendAuth.Resp resp,
            @Nullable LoginManager.LoginListener listener) {
        // 有可能是listener传入的是null，也可能是调用静态方法前没初始化当前的类
        if (listener != null) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK: // 登录成功
                    if (respListener != null) {
                        respListener.onLoginResp(resp.code, listener);
                    } else {
                        handlerLoginResp(activity, resp.code, listener); // 登录成功后开始通过code换取token
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    listener.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    listener.onError("用户拒绝授权");
                    break;
                default:
                    listener.onError("未知错误");
            }
        }
    }

    /**
     * 返回：
     * {
     * "access_token":"ACCESS_TOKEN", // token
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    private void handlerLoginResp(Context context, String code,
            final @Nullable LoginManager.LoginListener listener) {

        WeiboParameters params = new WeiboParameters(null);
        params.put("appid", ShareBlock.Config.weiXinAppId);
        params.put("secret", ShareBlock.Config.weiXinSecret);
        params.put("grant_type", "authorization_code");
        params.put("code", code);

        new AsyncWeiboRunner(context).requestAsync("https://api.weixin.qq.com/sns/oauth2/access_token", params, "GET", new RequestListener() {
            @Override
            public void onComplete(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String token = jsonObject.getString("access_token"); // 接口调用凭证
                    String openid = jsonObject.getString("openid"); // 授权用户唯一标识
                    long expires_in = jsonObject.getLong("expires_in"); // access_token接口调用凭证超时时间，单位（秒）

                    if (listener != null) {
                        listener.onSuccess(token, openid, expires_in, jsonObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // share
    ///////////////////////////////////////////////////////////////////////////

    public void sendShareMsg(@NonNull Context context, @NonNull ShareContent shareContent,
            @ShareType String shareType) {
        String weChatAppId = ShareBlock.Config.weiXinAppId;
        if (TextUtils.isEmpty(weChatAppId)) {
            throw new NullPointerException("请通过shareBlock初始化WeChatAppId");
        }

        IWXAPI IWXAPI = WXAPIFactory.createWXAPI(context, weChatAppId, true);
        IWXAPI.registerApp(weChatAppId);
        IWXAPI.sendReq(createShareRequest(shareContent, shareType)); // factory
    }

    @NonNull
    private SendMessageToWX.Req createShareRequest(@NonNull ShareContent shareContent, @ShareType String shareType) {
        // 建立信息体
        WXMediaMessage msg = new WXMediaMessage();
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getSummary();
        msg.thumbData = shareContent.getThumbBmpBytes(); // 这里没有做缩略图的配置，缩略图和原图是同一个对象
        msg.mediaObject = createMediaObject(shareContent);

        // 发送信息
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg; // msg
        switch (shareType) {
            case ShareType.WEIXIN_FRIEND:
                req.scene = SendMessageToWX.Req.WXSceneSession;
                break;
            case ShareType.WEIXIN_FRIEND_ZONE:
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;
            case ShareType.WEIXIN_FAVORITE:
                req.scene = SendMessageToWX.Req.WXSceneFavorite;
                break;
        }
        return req;
    }

    private WXMediaMessage.IMediaObject createMediaObject(@NonNull ShareContent shareContent) {
        WXMediaMessage.IMediaObject mediaObject;
        switch (shareContent.getType()) {
            case ContentType.TEXT:
                // 纯文字
                mediaObject = getTextObj(shareContent);
                break;
            case ContentType.PIC:
                // 纯图片
                mediaObject = getImageObj(shareContent);
                break;
            case ContentType.WEBPAGE:
                // 网页
                mediaObject = getWebPageObj(shareContent);
                break;
            case ContentType.MUSIC:
                // 音乐
                mediaObject = getMusicObj(shareContent);
                break;
            default:
                throw new UnsupportedOperationException("不支持的分享内容");
        }
        if (!mediaObject.checkArgs()) {
            throw new IllegalArgumentException("分享信息的参数类型不正确");
        }
        return mediaObject;
    }

    private WXMediaMessage.IMediaObject getTextObj(ShareContent shareContent) {
        WXTextObject text = new WXTextObject();
        text.text = shareContent.getSummary();
        return text;
    }

    private WXMediaMessage.IMediaObject getImageObj(ShareContent shareContent) {
        WXImageObject image = new WXImageObject();
        image.imagePath = shareContent.getLargeBmpPath();
        return image;
    }

    private WXMediaMessage.IMediaObject getMusicObj(ShareContent shareContent) {
        WXMusicObject music = new WXMusicObject();
        //Str1+"#wechat_music_url="+str2（str1是跳转的网页地址，str2是音乐地址）
        music.musicUrl = shareContent.getURL() + "#wechat_music_url=" + shareContent.getMusicUrl();
        return music;
    }

    private WXMediaMessage.IMediaObject getWebPageObj(ShareContent shareContent) {
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = shareContent.getURL();
        return webPage;
    }

    /**
     * 解析分享到微信的结果
     *
     * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419318634&token=&lang=zh_CN
     */
    private void parseShareResp(BaseResp resp, ShareManager.ShareStateListener listener) {
        if (listener != null) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    listener.onSuccess();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    listener.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    listener.onError("用户拒绝授权");
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    listener.onError("发送失败");
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    listener.onError("一般错误");
                    break;
                default:
                    listener.onError("未知错误");
            }
        }
    }
}

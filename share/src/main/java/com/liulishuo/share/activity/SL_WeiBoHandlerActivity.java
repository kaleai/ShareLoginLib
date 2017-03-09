package com.liulishuo.share.activity;

import android.app.Activity;
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
import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboResponse;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jack Tony
 * @date 2015/10/26
 *
 * https://github.com/sinaweibosdk/weibo_android_sdk
 */
public class SL_WeiBoHandlerActivity extends Activity implements IWeiboHandler.Response {

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler ssoHandler;

    private boolean isFirstIn = true;

    private boolean mIsLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLogin = getIntent().getBooleanExtra(ShareBlock.KEY_IS_LOGIN_TYPE, true);

        String appId = ShareBlock.Config.weiBoAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化weiBoAppId");
        }

        if (mIsLogin) {
            ssoHandler = new SsoHandler(this, new AuthInfo(this.getApplicationContext(), appId,
                    ShareBlock.Config.weiBoRedirectUrl,
                    ShareBlock.Config.weiBoScope));
            if (savedInstanceState == null) {
                // 防止不保留活动情况下activity被重置后直接进行操作的情况
                doLogin(LoginManager.listener);
            }
        } else {
            if (savedInstanceState == null) {
                // 防止不保留活动情况下activity被重置后直接进行操作的情况
                doShare(this, appId);
            } else {
                /**
                 * 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
                 * 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
                 * 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
                 * 失败返回 false，不调用上述回调
                 */
                IWeiboShareAPI API = WeiboShareSDK.createWeiboAPI(getApplicationContext(),
                        ShareBlock.Config.weiBoAppId);
                boolean success = API.handleWeiboResponse(getIntent(), this);
            }
        }

        if (savedInstanceState != null) {
            isFirstIn = false;
        }
    }

    /**
     * 因为微博客户端在用户取消分享后，用户点击保存到草稿箱后就不能接收到回调。
     * 因此，在这里必须进行强制关闭，不能依赖回调来关闭。
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstIn) {
            isFirstIn = false;
        } else {
            if (mIsLogin) {
                // 这里处理通过网页登录无回调的问题
                finish();
            } else {
                // 这里处理保存到草稿箱的逻辑
                BaseResponse response = new SendMessageToWeiboResponse();
                response.errCode = WBConstants.ErrorCode.ERR_CANCEL;
                response.errMsg = "weibo cancel";
                onResponse(response);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!mIsLogin) {
            /**
             * 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
             * 来接收微博客户端返回的数据；执行成功，返回 true，并调用
             * {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
             */
            IWeiboShareAPI API = WeiboShareSDK.createWeiboAPI(getApplicationContext(), ShareBlock.Config.weiBoAppId);
            API.handleWeiboResponse(intent, this); // 当前应用唤起微博分享后，返回当前应用
        }
    }

    /**
     * 解析用户【登录】的结果
     * SSO 授权回调   重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mIsLogin) {
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
            finish();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // login
    ///////////////////////////////////////////////////////////////////////////

    private void doLogin(final LoginManager.LoginListener listener) {
        WeiboAuthListener authListener = new WeiboAuthListener() {
            /*
             * 此种授权方式会根据手机是否安装微博客户端来决定使用sso授权还是网页授权
             * 1. SSO 授权时，需要在 onActivityResult 中调用 {@link SsoHandler#authorizeCallBack} 后，该回调才会被执行。
             * 2. 非SSO 授权时，当授权结束后，该回调就会被执行
             */
            @Override
            public void onComplete(Bundle values) {
                final Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
                if (listener != null && accessToken != null) {
                    if (accessToken.isSessionValid()) {
                        listener.onSuccess(accessToken.getToken(), accessToken.getUid(),
                                accessToken.getExpiresTime() / 1000000, oAuthData2Json(accessToken));
                    } else {
                        String errorCode = values.getString("code");
                        listener.onError("签名不正确，error-code = " + errorCode);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }

            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        };
        ssoHandler.authorize(authListener); // 启动微博的activity进行微博登录
    }

    @Nullable
    private String oAuthData2Json(@NonNull Oauth2AccessToken data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", data.getUid());
            jsonObject.put("refresh_token", data.getRefreshToken());
            jsonObject.put("access_token", data.getToken());
            jsonObject.put("expires_in", String.valueOf(data.getExpiresTime() / 1000000));
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // share
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onResponse(BaseResponse baseResponse) {
        ShareManager.ShareStateListener listener = ShareManager.listener;
        if (listener == null) {
            return;
        }

        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                listener.onSuccess();
                break;

            case WBConstants.ErrorCode.ERR_CANCEL:
                listener.onCancel();
                break;

            case WBConstants.ErrorCode.ERR_FAIL:
                listener.onError(baseResponse.errMsg);
                break;
            default:
                listener.onError("未知错误");
        }
        finish();
    }

    private void doShare(Activity activity, String appId) {
        // 建立请求体
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
        ShareContent content = (ShareContent) getIntent().getSerializableExtra(ShareManager.KEY_CONTENT);
        if (content == null) {
            throw new NullPointerException("ShareContent is null，intent = " + getIntent());
        }
        request.multiMessage = createShareObject(content);

        IWeiboShareAPI api = WeiboShareSDK.createWeiboAPI(activity, appId);
        api.registerApp();  // 将应用注册到微博客户端
        api.sendRequest(activity, request);
    }

    private WeiboMultiMessage createShareObject(@NonNull ShareContent shareContent) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        switch (shareContent.getType()) {
            case ContentType.TEXT:
                // 纯文字
                weiboMultiMessage.textObject = getTextObj(shareContent);
                break;
            case ContentType.PIC:
                // 纯图片
                weiboMultiMessage.imageObject = getImageObj(shareContent);
                break;
            case ContentType.WEBPAGE:
                // 网页
                if (shareContent.getURL() == null) {
                    weiboMultiMessage.imageObject = getImageObj(shareContent);
                    weiboMultiMessage.textObject = getTextObj(shareContent);
                } else {
                    weiboMultiMessage.mediaObject = getWebPageObj(shareContent);
                }
                break;
            case ContentType.MUSIC:
                // 音乐
                weiboMultiMessage.mediaObject = getMusicObj(shareContent);
                break;
        }
        if (!weiboMultiMessage.checkArgs()) {
            throw new IllegalArgumentException("分享信息的参数类型不正确");
        }
        return weiboMultiMessage;
    }

    /**
     * 创建文本消息对象
     */
    private TextObject getTextObj(ShareContent shareContent) {
        TextObject textObject = new TextObject();
        textObject.text = shareContent.getSummary();
        return textObject;
    }

    /**
     * 创建图片消息对象
     */
    private ImageObject getImageObj(ShareContent shareContent) {
        ImageObject imageObject = new ImageObject();
        imageObject.imagePath = shareContent.getLargeBmpPath();
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象
     */
    private WebpageObject getWebPageObj(ShareContent shareContent) {
        WebpageObject mediaObject = new WebpageObject();
        buildMediaObj(mediaObject, shareContent);

        mediaObject.defaultText = shareContent.getSummary();
        mediaObject.actionUrl = shareContent.getURL();
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj(ShareContent shareContent) {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        buildMediaObj(musicObject, shareContent);

        musicObject.defaultText = shareContent.getSummary();
        musicObject.actionUrl = shareContent.getMusicUrl();
        musicObject.dataUrl = ShareBlock.Config.weiBoRedirectUrl;
        musicObject.dataHdUrl = ShareBlock.Config.weiBoRedirectUrl;
        musicObject.duration = 10;
        return musicObject;
    }

    private void buildMediaObj(BaseMediaObject mediaObject, ShareContent shareContent) {
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareContent.getTitle();
        mediaObject.description = shareContent.getSummary();
        mediaObject.thumbData = shareContent.getThumbBmpBytes();
    }

   /* *//**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     *//*
    private VideoObject getVideoObj(ShareContent shareContent) {
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = shareContent.getTitle();
        videoObject.description = shareContent.getSummary();
        // 设置 Bitmap 类型的图片到视频对象里        
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        videoObject.thumbData = shareContent.getImageBmpBytes();
        videoObject.actionUrl = shareContent.getURL();
        videoObject.dataUrl = ShareBlock.Config.weiBoRedirectUrl;
        videoObject.dataHdUrl = ShareBlock.Config.weiBoRedirectUrl;
        videoObject.duration = 10;
        videoObject.defaultText = shareContent.getSummary(); // 默认文案
        return videoObject;
    }*/

    /*public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }*/
}

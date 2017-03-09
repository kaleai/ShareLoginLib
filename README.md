# ShareLoginLib   
[![](https://jitpack.io/v/tianzhijiexian/ShareLoginLib.svg)](https://jitpack.io/#tianzhijiexian/ShareLoginLib)  

ShareLoginLib likes simple sharesdk or umeng in China . It is a tool to help developers to share their content (image , text or webpage ) to WeChat,Weibo and QQ.  

![](./screenshot/logo.png)

## 示例
![](./screenshot/login.png) ![](./screenshot/share.png) ![](./screenshot/wechat.png)

## 添加依赖

1.在项目外层的build.gradle中添加JitPack仓库

```
repositories {
  // ...
	maven {
		url "https://jitpack.io"
	}
}
```

2.在用到的项目中添加依赖  
>	compile 'com.github.tianzhijiexian:ShareLoginLib:[Latest release](https://github.com/tianzhijiexian/ShareLoginLib/releases) (< click it)'  

**举例：**
```
compile 'com.github.tianzhijiexian:ShareLoginLib:1.3.7'
```

## 使用

### 登录、分享  
```JAVA  
// 登录
LoginManager.login(this, LoginType.XXX, new LoginManager.LoginListener() {

      public void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeJsonData) {}

      public void onError(String errorMsg) {}

      public void onCancel() {}
  });


// 分享
ShareManager.share(MainActivity.this, ShareType.XXX
        new ShareContentWebpage("title", "summary", "http://www.kale.com", mBitmap),
        new ShareManager.ShareStateListener() {

                  public void onSuccess() {}

                  public void onCancel() {}

                  public void onError(String errorMsg) {}
              });

```   

### 判断是否已安装第三方客户端  
```JAVA
ShareBlock.isWeiXinInstalled(this);
ShareBlock.isWeiBoInstalled(this);
ShareBlock.isQQInstalled(this);
```

部分手机上需要读取手机app列表的权限。


### 通过token和id得到用户信息
```JAVA
UserInfoManager.getUserInfo(context, LoginType.XXX, accessToken, userId,
    new UserInfoManager.UserInfoListener() {

        public void onSuccess(@NonNull AuthUserInfo userInfo) {
            // 可以得到：昵称、性别、头像、用户id
        }

        public void onError(String errorMsg) {
        }
    });
```  

更多详细的操作请参考项目的demo。

## 配置工作

### 1. 在build.gradle中配置QQ的key  

```java
defaultConfig {
	// ...
    applicationId "xxx.xxx.xxx" // 你的app包名
    manifestPlaceholders = ["tencentAuthId": "tencent123456"]   // tencent+你的AppId
}
```

### 2. 在使用功能前配置常量

```java  
Config config = Config.getInstance()
            .debug(false)
            .appName("Your App Name")
            .picTempFile(null) // 指定缓存缩略图的目录名字，如无特殊要求可以是null
            .qq(QQ_APPID, QQ_SCOPE)
            .weiXin(WEIXIN_APPID, WEIXIN_SECRET)
            .weiBo(WEIBO_APPID, WEIBO_REDIRECT_URL, WEIBO_SCOPE);

ShareBlock.init(this, config);
```

## 重要说明

- 签名后的app才可以进行测试
- 使用者要在第三方平台进行注册后才可测试
- 库作者不会提供任何和签名、密码、AppId等有关信息
- 测试app需要有和第三方sdk约定好的正确签名

## 推荐的测试环境  

- 开启不保留活动
- 未安装第三方应用  
- 安装第三方应用，但第三方应用未登录  
- 未开启不保留活动，并且第三方应用已经登录

## 已知的第三方SDK的bug（本lib无法解决）
1. 不能信任第三方的回调，操作后可能会得不到任何回调
2. 分享到了微信，用户留在了微信，那么就永远接收不到回调了
1. 如果没进行微博的登录，直接调用微博分享，有一定概率会出现分享失败
2. 分享途中通过通知消息进入别的app后，可能会因为内存不足等奇葩情况，你的应用被杀死，没有回调
3. 如果你手机中安装了微信，并且微信已经登录。直接从你的应用分享到微信是没有任何回调的，只有在你用微信登录你的应用（无论登录是否成功，取消也行）后，才能有回调 
4. 当开启不保留活动后，有可能会出现界面的显示异常，这个和第三方的应用有密切关系，微博尤其明显

## 配置运行本demo的环境


如果你要运行该项目给出的demo，那么可以修改本地的`gradle.properties`文件，将下列信息修改成你自己的值。   

```
STORE_FILE_PATH	       xxxxx
STORE_PASSWORD	       xxxxx
KEY_ALIAS		       xxxxx
KEY_PASSWORD	       xxxxx
PACKAGE_NAME_SUFFIX    xxxxx
TENCENT_AUTHID tencent xxxxx
```

## LICENCE

  The MIT License (MIT)

  Copyright (c) 2015-2017 kale Inc.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.

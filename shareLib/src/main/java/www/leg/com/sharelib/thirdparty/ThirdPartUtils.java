package www.leg.com.sharelib.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import www.leg.com.sharelib.BaseWXEntryActivity;
import www.leg.com.sharelib.IThirdParty;
import www.leg.com.sharelib.MD5Util;
import www.leg.com.sharelib.bean.PayResult;
import www.leg.com.sharelib.bean.QQAuthInfo;
import www.leg.com.sharelib.bean.QQUserInfo;
import www.leg.com.sharelib.bean.WxCode;
import www.leg.com.sharelib.bean.WxPayBean;
import www.leg.com.sharelib.exception.ThirdPartExp;

import static android.graphics.BitmapFactory.decodeResource;

/**
 * Created by liushenghan on 2017/2/25.
 */

public class ThirdPartUtils implements Serializable {

    public static final String USER_CANCLE_AUTH = "用户取消授权";
    public static final String USER_REFUSE_AUTH = "用户拒绝授权";
    public static final String GET_AUTH_FAILURE = "获取授权失败";
    public static final String GET_USER_INFO_FAILURE = "获取用户信息失败";

    /**
     * 获取 qq授权
     *
     * @param APPID
     * @param activity
     * @return
     */
    public static <T extends Activity & IThirdParty> Observable<QQAuthInfo> getQQToken(final String APPID, final T activity) {
        return Observable.create(new Observable.OnSubscribe<QQAuthInfo>() {
            @Override
            public void call(final Subscriber<? super QQAuthInfo> subscriber) { // 获取Token

                final Tencent mTencent = Tencent.createInstance(APPID, activity.getApplicationContext());
                final IUiListener loginLs = new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        if (null == response) {
                            subscriber.onError(new ThirdPartExp(GET_AUTH_FAILURE));
                            return;
                        }
                        JSONObject jsonResponse = (JSONObject) response;
                        if (null != jsonResponse && jsonResponse.length() == 0) {
                            subscriber.onError(new ThirdPartExp(GET_AUTH_FAILURE));
                            return;
                        }
                        QQAuthInfo info = new Gson().fromJson(response.toString(), QQAuthInfo.class);
                        if (info != null && !TextUtils.isEmpty(info.access_token) && !TextUtils.isEmpty(info.expires_in)
                                && !TextUtils.isEmpty(info.openid)) {
                            mTencent.setAccessToken(info.access_token, info.expires_in);
                            mTencent.setOpenId(info.openid);
                            subscriber.onNext(info);
                        } else {
                            subscriber.onError(new ThirdPartExp(GET_AUTH_FAILURE));
                        }

                    }

                    @Override
                    public void onError(UiError uiError) {
                        subscriber.onError(new Throwable(uiError.errorMessage));
                    }

                    @Override
                    public void onCancel() {
                        subscriber.onError(new Throwable(USER_CANCLE_AUTH));
                    }
                };
                activity.setOnActivityResultListener(new PreferenceManager.OnActivityResultListener() {
                    @Override
                    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                        if (requestCode == Constants.REQUEST_LOGIN ||
                                requestCode == Constants.REQUEST_APPBAR) {
                            Tencent.onActivityResultData(requestCode, resultCode, data, loginLs);
                        }
                        return false;
                    }
                });
                mTencent.login(activity, "all", loginLs);
            }
        });
    }

    /**
     * 根据授权获取用户信息
     *
     * @param APPID
     * @param activity
     * @return
     */
    public static <T extends Activity & IThirdParty> Observable<QQUserInfo> getQQUserInfo(final String APPID, final T activity) {

        return getQQToken(APPID, activity).flatMap(new Func1<QQAuthInfo, Observable<QQUserInfo>>() {
            @Override
            public Observable<QQUserInfo> call(QQAuthInfo qqAuthInfo) {
                return Observable.create(new Observable.OnSubscribe<QQUserInfo>() {
                    @Override
                    public void call(final Subscriber<? super QQUserInfo> subscriber) {

                        final Tencent mTencent = Tencent.createInstance(APPID, activity.getApplicationContext());

                        IUiListener listener = new IUiListener() {

                            @Override
                            public void onError(UiError e) {
                                subscriber.onError(new Throwable(e.errorMessage));
                            }

                            @Override
                            public void onComplete(final Object response) {
                                Log.d("SDKQQAgentPref", "updateUserInfo onComplete:" + response.toString());
                                Gson gson = new Gson();
                                QQUserInfo info = gson.fromJson(response.toString(), QQUserInfo.class);
                                subscriber.onNext(info);
                            }

                            @Override
                            public void onCancel() {
                                subscriber.onError(new Throwable(USER_CANCLE_AUTH));
                            }
                        };

                        UserInfo mInfo = new UserInfo(activity, mTencent.getQQToken());
                        mInfo.getUserInfo(listener);
                    }
                });
            }
        });


    }

//
//    public static final String SWB_REDIRECT_URL = "http://123.57.244.52:8080/app/user/sinaAuth.do";//新浪微博回调页面
//    public static final String SWB_SCOPE = "email,direct_messages_read,direct_messages_write,"
//            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
//            + "follow_app_official_microblog," + "invitation_write";

    /**
     * 获取新浪微博Token
     *
     * @param appKey
     * @param activity
     * @param ssoHandler
     * @param <T>
     * @return
     */
    public static <T extends Activity & IThirdParty> Observable<Oauth2AccessToken> getSinaToken(final String appKey, final T activity, final SsoHandler ssoHandler) {
        return Observable.create(new Observable.OnSubscribe<Oauth2AccessToken>() {
            @Override
            public void call(final Subscriber<? super Oauth2AccessToken> subscriber) {

                IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, appKey);
                if (Looper.myLooper() == null)
                    Looper.prepare();                // 注册
                mWeiboShareAPI.registerApp();
                mWeiboShareAPI.handleWeiboResponse(activity.getIntent(), new IWeiboHandler.Response() {
                    @Override
                    public void onResponse(BaseResponse baseResp) {
                        Log.e("getSinaToken", "WEIBO code = " + baseResp.errCode + " msg = " + baseResp.errMsg);
                    }
                });

                ssoHandler.authorize(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        Log.e("getSinaToken", "Third token  " + mAccessToken.getToken() + "  uid " + mAccessToken.getUid() + "   " + mAccessToken.getExpiresTime());
                        if (mAccessToken.isSessionValid()) {
                            Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(bundle);
                            subscriber.onNext(token);
                        } else {
                            // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                            String code = bundle.getString("code");
                            subscriber.onError(new ThirdPartExp("签名不正确"));
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        Log.e("getSinaToken", "onWeiboException" + e.getMessage());
                        subscriber.onError(new ThirdPartExp(e.getMessage()));
                    }

                    @Override
                    public void onCancel() {
                        Log.e("getSinaToken", "onCancel");
                        subscriber.onError(new Throwable(USER_CANCLE_AUTH));
                    }
                });

                activity.setOnActivityResultListener(new PreferenceManager.OnActivityResultListener() {
                    @Override
                    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                        if (null != ssoHandler) {
                            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
                        }
                        return false;
                    }
                });
                Log.e("getSinaToken", "end");
            }
        });
    }


    /**
     * 获取微信code
     *
     * @param activity
     * @return
     */
    public static Observable<WxCode> getWxCodeOther(final Activity activity, final String wxAppId) {
        Log.e("WXEntryActivity", "call getWxCodeOther");
        return Observable.create(new Observable.OnSubscribe<WxCode>() {

            @Override
            public void call(final Subscriber<? super WxCode> subscriber) {

                IWXAPI api = WXAPIFactory.createWXAPI(activity.getApplicationContext(), wxAppId, false);
                if (!api.isWXAppInstalled()) {
                    subscriber.onError(new ThirdPartExp("未安装微信"));
                    return;
                }
                api.registerApp(wxAppId);
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "org_uuyi_youyi";

                BaseWXEntryActivity.iwxapiEventHandler = new IWXAPIEventHandler() {
                    @Override
                    public void onReq(BaseReq baseReq) {
                        Log.e("WXEntryActivity", "call baseReq");
                    }

                    @Override
                    public void onResp(BaseResp baseResp) {
                        Log.e("WXEntryActivity", "call onResp BaseResp");
                        Gson gson = new Gson();
                        String str = gson.toJson(baseResp);
                        Log.e("WXEntryActivity", "call onResp " + str);
                        WxCode code = gson.fromJson(str, WxCode.class);
                        //ERR_OK = 0(用户同意)
                        // ERR_AUTH_DENIED = -4（用户拒绝授权）
                        // ERR_USER_CANCEL = -2（用户取消）
                        if (code != null && code.errCode >= 0) {
                            subscriber.onNext(code);
                        } else if (code.errCode == BaseResp.ErrCode.ERR_AUTH_DENIED) {
                            subscriber.onError(new ThirdPartExp(USER_REFUSE_AUTH));
                        } else if (code.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                            subscriber.onError(new ThirdPartExp(USER_CANCLE_AUTH));
                        } else {
                            subscriber.onError(new ThirdPartExp(GET_AUTH_FAILURE));
                        }
                    }
                };
                api.sendReq(req);
            }
        });
    }

    /**
     * 分享到朋友圈 描述没有用
     *
     * @param activity
     * @param appID
     * @param res
     * @return
     */
    public static Observable<BaseResp> shareWXSceneTimeline(final Activity activity, final String appID, @DrawableRes final int res) {
        return Observable.create(new Observable.OnSubscribe<BaseResp>() {
            @Override
            public void call(final Subscriber<? super BaseResp> subscriber) {
                IWXAPI api = WXAPIFactory.createWXAPI(activity, appID, false);
                api.registerApp(appID);
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = "https://www.baidu.com";

                // 用WXTextObject对象初始化一个WXMediaMessage对象
                WXMediaMessage msg = new WXMediaMessage(webpage);
                // 发送文本类型的消息时，title字段不起作用

                msg.description = "描述";
//        if (flag) {
//            msg.thumbData = Util.bmpToByteArray(decodeResource(getResources(), R.drawable.logo_p4u_ic_144), true);
//            msg.title = String.format(getString(R.string.share_we_chat),title);
//        } else {
                msg.thumbData = bmpToByteArray(decodeResource(activity.getResources(), res), true);
                msg.title = "标题";
//        }
                // 构造一个Req
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage"); // transaction字段用于唯一标识一个请求
                req.message = msg;

//        req.scene = flag ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;


                BaseWXEntryActivity.iwxapiEventHandler = new IWXAPIEventHandler() {
                    @Override
                    public void onReq(BaseReq baseReq) {
                        Log.e("WXEntryActivity", "call baseReq");
                    }

                    @Override
                    public void onResp(BaseResp baseResp) {
                        // 以前都是直接抛异常的
                        subscriber.onNext(baseResp);
//                        Log.e("WXEntryActivity", "call onResp BaseResp");
//                        Gson gson = new Gson();
//                        String str = gson.toJson(baseResp);
//                        Log.e("WXEntryActivity", "call onResp " + str);
//                        WxCode code = gson.fromJson(str, WxCode.class);
//                        //ERR_OK = 0(用户同意)
//                        // ERR_AUTH_DENIED = -4（用户拒绝授权）
//                        // ERR_USER_CANCEL = -2（用户取消）
//                        if (code != null && code.errCode >= 0) {
//                            subscriber.onNext(code);
//                        } else if (code.errCode == BaseResp.ErrCode.ERR_AUTH_DENIED) {
//                            subscriber.onError(new ThirdPartExp(USER_REFUSE_AUTH));
//                        } else if (code.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
//                            subscriber.onError(new ThirdPartExp(USER_CANCLE_AUTH));
//                        } else {
//                            subscriber.onError(new ThirdPartExp(GET_AUTH_FAILURE));
//                        }
                    }
                };
                api.sendReq(req);
            }
        });

    }

    /**
     * 分享到微信会话
     *
     * @param activity
     * @param appID
     * @param res
     * @return
     */

    public static Observable<BaseResp> shareWXSceneSession(final Activity activity, final String appID, @DrawableRes final int res) {
        return Observable.create(new Observable.OnSubscribe<BaseResp>() {
            @Override
            public void call(final Subscriber<? super BaseResp> subscriber) {
                IWXAPI api = WXAPIFactory.createWXAPI(activity, appID, false);
                api.registerApp(appID);
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = "https://www.baidu.com";

                // 用WXTextObject对象初始化一个WXMediaMessage对象
                WXMediaMessage msg = new WXMediaMessage(webpage);
                // 发送文本类型的消息时，title字段不起作用

                msg.description = "描述";
                msg.thumbData = bmpToByteArray(decodeResource(activity.getResources(), res), true);
                msg.title = "标题";
                // 构造一个Req
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage"); // transaction字段用于唯一标识一个请求
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                BaseWXEntryActivity.iwxapiEventHandler = new IWXAPIEventHandler() {
                    @Override
                    public void onReq(BaseReq baseReq) {
                        Log.e("WXEntryActivity", "call baseReq");
                    }

                    @Override
                    public void onResp(BaseResp baseResp) {
                        subscriber.onNext(baseResp);
                    }
                };
                api.sendReq(req);
            }
        });

    }


    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public static <T extends Activity & IThirdParty> Observable<Object> shareQQSession(final String appId, final T activity) {

        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                final Tencent mTencent = Tencent.createInstance(appId, activity);
                // 腾讯Appid 和签名一定要对上 分享回调一直是 onCancel
                final IUiListener listener = new IUiListener() {

                    @Override
                    public void onCancel() {
                        Log.e("shareQQ", "onCancel");
                        subscriber.onNext("用户取消");
                        mTencent.releaseResource();
                    }

                    @Override
                    public void onError(UiError e) {
                        // TODO Auto-generated method stub
                        Log.e("shareQQ", "UiError" + e.errorDetail);
                        subscriber.onNext(e);
                        mTencent.releaseResource();

                    }

                    @Override
                    public void onComplete(Object response) {
                        // TODO Auto-generated method stub
                        Log.e("shareQQ", "onComplete" + response);
                        subscriber.onNext(response);
                        mTencent.releaseResource();
                    }

                };


                Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, "SHARE_TO_QQ_TITLE");
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "SHARE_TO_QQ_SUMMARY");
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://www.baidu.com");
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, "/mnt/sdcard/TouchSprite/res/1.png");
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://www.play4u.cn/img/p4u_app_icon.png");
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "1234");
                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);
                mTencent.shareToQQ(activity, params, listener);

                activity.setOnActivityResultListener(new PreferenceManager.OnActivityResultListener() {
                    @Override
                    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                        Log.e("shareQQ", "onActivityResult" + requestCode);
                        if (requestCode == Constants.REQUEST_QQ_SHARE) {
                            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
                        }
                        return false;
                    }
                });

            }
        });

    }

    public static <T extends Activity & IThirdParty> Observable<Object> shareQQZone(final String appId, final T activity) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                final Tencent mTencent = Tencent.createInstance(appId, activity);
                // 腾讯Appid 和签名一定要对上 分享回调一直是 onCancel
                final IUiListener listener = new IUiListener() {

                    @Override
                    public void onCancel() {
                        Log.e("shareQQ", "onCancel");
                        subscriber.onNext("用户取消");
                        mTencent.releaseResource();
                    }

                    @Override
                    public void onError(UiError e) {
                        // TODO Auto-generated method stub
                        Log.e("shareQQ", "UiError" + e.errorDetail);
                        subscriber.onNext(e);
                        mTencent.releaseResource();

                    }

                    @Override
                    public void onComplete(Object response) {
                        // TODO Auto-generated method stub
                        Log.e("shareQQ", "onComplete" + response);
                        subscriber.onNext(response);
                        mTencent.releaseResource();

                    }

                };
                activity.setOnActivityResultListener(new PreferenceManager.OnActivityResultListener() {
                    @Override
                    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                        Log.e("shareQQ", "onActivityResult" + requestCode);
                        if (requestCode == Constants.REQUEST_QZONE_SHARE) {
                            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
                        }
                        return false;
                    }
                });
                final Bundle params = new Bundle();
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "SHARE_TO_QQ_TITLE");
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "SHARE_TO_QQ_SUMMARY");
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "https://www.baidu.com");
//        params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, "http://www.play4u.cn/img/p4u_app_icon.png");
                // 支持传多个imageUrl 多个 imageurl 只有在 说说中有用
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, new ArrayList<String>() {
                    {
//                add("http://www.play4u.cn/img/p4u_app_icon.png");
                        add("/mnt/sdcard/TouchSprite/res/1.png");
                    }
                });
                mTencent.shareToQzone(activity, params, listener);
            }
        });


    }

    /**
     * 微博分享
     * 注意事项 在AndroidManifest.xml Activity中添加
     * <intent-filter>
     * <action android:name="com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY"/>
     * <category android:name="android.intent.category.DEFAULT"/>
     * </intent-filter>
     * 否则不能回调 IWeiboHandler.Response中的方法
     * Response 不能使匿名类,只能是Activity实现IWeiboHandler.Response
     *
     * @param t
     * @param res
     * @param mWeiboShareAPI
     * @param <T>
     */
    public static <T extends Activity & IWeiboHandler.Response> void shareWeibo(T t, @DrawableRes int res, IWeiboShareAPI mWeiboShareAPI) {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        TextObject textObject = new TextObject();
        textObject.text = "SINA_TITLE";
        weiboMessage.textObject = textObject;


        ImageObject imageObject = new ImageObject();
        //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        Bitmap bitmap = decodeResource(t.getResources(), res);
        imageObject.setImageObject(bitmap);
        weiboMessage.imageObject = imageObject;

        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "SINA_TITLE";
        mediaObject.description = "描述,总结";
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = "https://www.baidu.com";

        weiboMessage.mediaObject = mediaObject;
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(t, request);

    }

    private static final int SDK_PAY_FLAG = 1;

    /**
     * 支付宝支付
     * <p>
     * //https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.DQPYjx&treeId=204&articleId=105302&docType=1
     * getResultStatus 状态对应列表
     * 9000	订单支付成功
     * 8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     * 4000	订单支付失败
     * 5000	重复请求
     * 6001	用户中途取消
     * 6002	网络连接出错
     * 6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     * 其它	其它支付错误
     */
    public static Observable<PayResult> aliPay(final Activity activity, final String orderInfo) {

        return Observable.create(new Observable.OnSubscribe<PayResult>() {
            @Override
            public void call(final Subscriber<? super PayResult> subscriber) {
                final Handler mHandler = new Handler() {
                    @SuppressWarnings("unused")
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case SDK_PAY_FLAG: {
                                PayResult payResult = new PayResult((String) msg.obj);
                                subscriber.onNext(payResult);
                                break;
                            }
                            default:
                                subscriber.onError(new ThirdPartExp("UnKonw"));
                                break;
                        }
                    }
                };
                Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(activity);
                        // 调用支付接口，获取支付结果
                        String result = alipay.pay(orderInfo, true);

                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };

                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
        });
    }

    /**
     * 微信支付
     *
     * @param activity
     * @param bean     微信服务器返回订单详情本地构建微信支付请求
     * @return
     */
    public static Observable<BaseResp> wxPay(final String wxAppID, final Activity activity, final WxPayBean bean) {

        return Observable.create(new Observable.OnSubscribe<BaseResp>() {
            @Override
            public void call(final Subscriber<? super BaseResp> subscriber) {


                IWXAPI msgApi = WXAPIFactory.createWXAPI(activity, wxAppID, false);
                // 将该app注册到微信
                msgApi.registerApp(wxAppID);
                if (!msgApi.isWXAppInstalled()) {
                    subscriber.onError(new Throwable("未安装微信"));
                    return;
                }
                PayReq request = new PayReq();
                Map<String, String> dataMap = new TreeMap<String, String>(new Comparator<String>() {

                    @Override
                    public int compare(String o1, String o2) {
                        for (int i = 0; i < o1.length() && i < o2.length(); i++) {
                            if (o1.charAt(i) - o2.charAt(i) != 0) {
                                return o1.charAt(i) - o2.charAt(i);
                            }
                        }
                        return o1.length() - o2.length();
                    }
                });
                dataMap.put("appid", bean.appid);
                dataMap.put("partnerid", bean.mch_id);
                dataMap.put("prepayid", bean.prepay_id);
                dataMap.put("package", "Sign=WXPay");
                dataMap.put("noncestr", bean.nonce_str);
                dataMap.put("timestamp", bean.time);


                StringBuilder strA = new StringBuilder();
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    if (value != null && !"".equals(value)) {
                        strA.append(key + "=" + value + "&");
                    }

                }
                strA.append("key=" + wxAppID);
                String sign = MD5Util.makeMD5(strA.toString()).toUpperCase();

                request.appId = bean.appid;
                request.partnerId = bean.mch_id;
                request.prepayId = bean.prepay_id;
                request.packageValue = "Sign=WXPay";
                request.nonceStr = bean.nonce_str;
                request.timeStamp = bean.time;
                request.sign = bean.client_sign;
                BaseWXEntryActivity.iwxapiEventHandler = new IWXAPIEventHandler() {
                    @Override
                    public void onReq(BaseReq baseReq) {
                    }

                    @Override
                    public void onResp(BaseResp baseResp) {

                        subscriber.onNext(baseResp);
                    }
                };
                msgApi.sendReq(request);
            }
        });

    }

}

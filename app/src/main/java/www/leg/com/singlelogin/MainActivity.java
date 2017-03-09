package www.leg.com.singlelogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.modelbase.BaseResp;

import java.util.Locale;

import okhttp3.OkHttpClient;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import www.leg.com.sharelib.IThirdParty;
import www.leg.com.sharelib.bean.QQUserInfo;
import www.leg.com.sharelib.bean.SinaUserInfo;
import www.leg.com.sharelib.bean.WxUserInfo;
import www.leg.com.sharelib.thirdparty.ThirdPartUtils;
import www.leg.com.singlelogin.download.DownLoadInfo;
import www.leg.com.singlelogin.download.OkHttpUitls;

import static android.graphics.BitmapFactory.decodeResource;


public class MainActivity extends AppCompatActivity implements IThirdParty, IWeiboHandler.Response {

    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();// 管理订阅者者

    private PreferenceManager.OnActivityResultListener OnActivityResultListener;
    IWeiboShareAPI mWeiboShareAPI;

    String url = "http://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/3.6.0/okhttp-3.6.0.jar";
    String filePath = "/mnt/sdcard/okhttp/okhttp.jar";

    TextView index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sina
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, "275810222");
        // 注册
        mWeiboShareAPI.registerApp();
        mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        index = (TextView) findViewById(R.id.index);

    }

    @Override
    public void onResponse(BaseResponse baseResp) {
        Log.e("MainActivity", "WEIBO code = " + baseResp.errCode + " msg = " + baseResp.errMsg);
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    // 分享成功
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    public void weChatLogin(View view) {

        toast("微信登录");
//        Subscription subscription = ThirdPartUtils.getWxCodeOther(this, Config.WE_CHAT_APPID)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<WxCode>() {
//                    @Override
//                    public void call(WxCode wxCode) {
//                        Log.e("WXEntryActivity", "WxCode = " + wxCode);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.e("WXEntryActivity", "Throwable = " + throwable.getMessage());
//                    }
//                });
//

//        Subscription subscription = GetInfo.$().getWxAuthInfo(this, Config.WE_CHAT_APPID)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<WxAuthInfo>() {
//                    @Override
//                    public void call(WxAuthInfo wxAuthInfo) {
//                        Log.e("WXEntryActivity", "WxAuthInfo = " + wxAuthInfo);
//
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.e("WXEntryActivity", "Throwable = " + throwable.getMessage());
//
//                    }
//                });

        Subscription subscription = GetInfo.$().getWxUserInfo(this, Config.WE_CHAT_APPID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WxUserInfo>() {
                    @Override
                    public void call(WxUserInfo wxUserInfo) {
                        Log.e("WXEntryActivity", "WxUserInfo = " + wxUserInfo);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("WXEntryActivity", "Throwable = " + throwable.getMessage());
                    }
                });
        mCompositeSubscription.add(subscription);

    }

    public void qqLogin(View view) {
        toast("QQ登录");
//        Subscription subscription = ThirdPartUtils.getQQUserInfo(Config.QQ_APP_ID, this, this)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<QQUserInfo>() {
//                    @Override
//                    public void call(QQUserInfo qqAuthInfo) {
//                        Log.e("getQQToken", "QQAuthInfo = " + qqAuthInfo.toString());
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.e("getQQToken", "throwable = " + throwable.getMessage());
//                    }
//                });

//        Subscription subscription = ThirdPartUtils.getQQToken(Config.QQ_APP_ID, this, this).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<QQAuthInfo>() {
//                    @Override
//                    public void call(QQAuthInfo qqAuthInfo) {
//                        Log.e("getQQToken", "QQAuthInfo = " + qqAuthInfo.toString());
//
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.e("getQQToken", "Throwable = " + throwable.getMessage());
//
//                    }
//                });

        Subscription subscription = GetInfo.$().getQQUserInfo(Config.QQ_APP_ID, this)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<QQUserInfo>() {
                    @Override
                    public void call(QQUserInfo qqUserInfo) {
                        Log.e("getQQToken", "QQUserInfo = " + qqUserInfo);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("getQQToken", "Throwable = " + throwable.getMessage());

                    }
                });
        mCompositeSubscription.add(subscription);
    }

    SsoHandler ssoHandler;

    public void sinaLogin(View view) {
        toast("新浪登录");
        AuthInfo authInfo = new AuthInfo(this, Config.SINA_APP_KEY, Config.SWB_REDIRECT_URL, Config.SWB_SCOPE);
        ssoHandler = new SsoHandler(this, authInfo);
//        Subscription subscription = ThirdPartUtils.getSinaToken(Config.SINA_APP_KEY, this, ssoHandler, this).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Oauth2AccessToken>() {
//                    @Override
//                    public void call(Oauth2AccessToken oauth2AccessToken) {
//                        Log.e("getSinaToken", "Oauth2AccessToken " + oauth2AccessToken);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.e("getSinaToken", "Throwable " + throwable.getMessage());
//                    }
//                });
        Subscription subscription = GetInfo.$().getSinaUserInfo(Config.SINA_APP_KEY, this, ssoHandler).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SinaUserInfo>() {
                    @Override
                    public void call(SinaUserInfo sinaUserInfo) {
                        Log.e("getSinaToken", "SinaUserInfo " + sinaUserInfo);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("getSinaToken", "Throwable " + throwable.getMessage());

                    }
                });

        mCompositeSubscription.add(subscription);

    }

    /**
     * 朋友圈
     *
     * @param view
     */
    public void wxMoment(View view) {

        Subscription subscription = ThirdPartUtils.shareWXSceneTimeline(this, Config.WE_CHAT_APPID, R.mipmap.ic_launcher)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResp>() {
                    @Override
                    public void call(BaseResp baseResp) {
                        Log.e("WXEntryActivity", "shareWXSceneTimeline onResp" +
                                String.format(Locale.getDefault(), " errCode = %d  errStr = %s  openId = %s  transaction = %s ", baseResp.errCode, baseResp.errStr, baseResp.openId, baseResp.transaction));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        mCompositeSubscription.add(subscription);
    }


    public void wxChat(View view) {
        Subscription subscription = ThirdPartUtils.shareWXSceneSession(this, Config.WE_CHAT_APPID, R.mipmap.ic_launcher)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResp>() {
                    @Override
                    public void call(BaseResp baseResp) {
                        Log.e("WXEntryActivity", "shareWXSceneSession onResp" +
                                String.format(Locale.getDefault(), " errCode = %d  errStr = %s  openId = %s  transaction = %s ", baseResp.errCode, baseResp.errStr, baseResp.openId, baseResp.transaction));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        mCompositeSubscription.add(subscription);
    }


    public void qqzone(View view) {
        Subscription subscription = ThirdPartUtils.shareQQZone(Config.QQ_APP_ID, this)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        Log.e("shareQQ", "MainActivity call" + o);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("shareQQ", "MainActivity Throwable" + throwable.getMessage());
                    }
                });
        mCompositeSubscription.add(subscription);

    }


    public void qqchat(View view) {
        Subscription subscription = ThirdPartUtils.shareQQSession(Config.QQ_APP_ID, this)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        Log.e("shareQQ", "MainActivity call" + o);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("shareQQ", "MainActivity Throwable" + throwable.getMessage());
                    }
                });
        mCompositeSubscription.add(subscription);

    }


    public void sinaweibo(View view) {
        Log.e("SHARE_WEIBO", "sinaweibo code ");

        ThirdPartUtils.shareWeibo(this, R.mipmap.ic_launcher, mWeiboShareAPI);

    }


    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = "SINA_TITLE";
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        Bitmap bitmap = decodeResource(getResources(), R.mipmap.ic_launcher);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "SINA_TITLE";
        mediaObject.description = "描述,总结";
        Bitmap bitmap = decodeResource(getResources(), R.mipmap.ic_launcher);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = "https://www.baidu.com";
        return mediaObject;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {// TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (OnActivityResultListener != null)
            OnActivityResultListener.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setOnActivityResultListener(PreferenceManager.OnActivityResultListener onActivityResultListener) {
        OnActivityResultListener = onActivityResultListener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (OnActivityResultListener != null) OnActivityResultListener = null;
        mCompositeSubscription.unsubscribe();
    }


    private void sendMultiMessage() {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj();
        weiboMessage.imageObject = getImageObj();
        weiboMessage.mediaObject = getWebpageObj();
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(MainActivity.this, request);
    }


    public void cancel(View w) {
        OkHttpClient client = OkHttpUitls.getClient();
        client.dispatcher().cancelAll();

    }

    public void start(View w) {
        OkHttpClient client = OkHttpUitls.getClient();
        Subscription subscription = OkHttpUitls.downFileNew(url, filePath, client)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DownLoadInfo>() {
                    @Override
                    public void call(DownLoadInfo info) {
                        index.setText(String.format(Locale.getDefault(), "%d/%d", info.sum, info.total));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("OkHttpUitls", "Throwable" + throwable.getMessage());

                    }
                });
        mCompositeSubscription.add(subscription);

    }
}

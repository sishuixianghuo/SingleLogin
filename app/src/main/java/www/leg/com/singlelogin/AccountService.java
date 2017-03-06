package www.leg.com.singlelogin;


import com.lzy.okgo.OkGo;
import com.lzy.okrx.RxAdapter;

import rx.Observable;
import www.leg.com.sharelib.bean.QQUserInfo;
import www.leg.com.sharelib.bean.SinaUserInfo;
import www.leg.com.sharelib.bean.WxAuthInfo;
import www.leg.com.sharelib.bean.WxUserInfo;
import www.leg.com.singlelogin.callback.UndefinedJsonConvert;

public class AccountService {
    private AccountService() {
    }

    private static class SingletonHolder {
        private static final AccountService INSTANCE = new AccountService();
    }

    public static AccountService $() {
        return AccountService.SingletonHolder.INSTANCE;
    }

    public Observable<WxAuthInfo> getWxAccessToken(String grant_type, String code) {
        return OkGo.get("https://api.weixin.qq.com/sns/oauth2/access_token")
                .params("grant_type", grant_type)
                .params("appid", Config.WE_CHAT_APPID)
                .params("secret", Config.WE_CHAT_APPSECRET)
                .params("code", code)
                .getCall(new UndefinedJsonConvert<WxAuthInfo>() {
                }, RxAdapter.<WxAuthInfo>create());
    }

    public Observable<WxUserInfo> getWxUserInfo(String access_token, String openid) {
        return OkGo.get("https://api.weixin.qq.com/sns/userinfo")
                .params("access_token", access_token)
                .params("openid", openid)
                .getCall(new UndefinedJsonConvert<WxUserInfo>() {
                }, RxAdapter.<WxUserInfo>create());
    }

    public Observable<QQUserInfo> getQQUserInfo(String access_token, String openId) {
        return OkGo.get("https://graph.qq.com/user/get_user_info")
                .params("access_token", access_token)
                .params("oauth_consumer_key", Config.QQ_APP_ID)
                .params("openid", openId)
                .params("format", "json")
                .getCall(new UndefinedJsonConvert<QQUserInfo>() {
                }, RxAdapter.<QQUserInfo>create());
    }

    //https://api.weibo.com/2/users/show.json?
    public Observable<SinaUserInfo> getSinaUserInfo(String access_token, String uid) {
        return OkGo.get("https://api.weibo.com/2/users/show.json")
                .params("access_token", access_token)
                .params("uid", uid)
                .getCall(new UndefinedJsonConvert<SinaUserInfo>() {
                }, RxAdapter.<SinaUserInfo>create());
    }

}

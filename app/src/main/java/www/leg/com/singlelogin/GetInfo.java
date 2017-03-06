package www.leg.com.singlelogin;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import rx.Observable;
import rx.functions.Func1;
import www.leg.com.sharelib.IThirdParty;
import www.leg.com.sharelib.bean.QQAuthInfo;
import www.leg.com.sharelib.bean.QQUserInfo;
import www.leg.com.sharelib.bean.SinaUserInfo;
import www.leg.com.sharelib.bean.WxAuthInfo;
import www.leg.com.sharelib.bean.WxCode;
import www.leg.com.sharelib.bean.WxUserInfo;
import www.leg.com.sharelib.exception.ThirdPartExp;
import www.leg.com.sharelib.thirdparty.ThirdPartUtils;

/**
 * Created by liushenghan on 2017/2/28.
 */

public class GetInfo {

    private volatile static GetInfo instance;

    private GetInfo() {
    }

    public static GetInfo $() {
        if (instance == null) {
            synchronized (GetInfo.class) {
                if (instance == null) {
                    instance = new GetInfo();
                }
            }
        }
        return instance;
    }

    public Observable<WxAuthInfo> getWxAuthInfo(Activity activity, String wxAppId) {
        return ThirdPartUtils.getWxCodeOther(activity, wxAppId).flatMap(new Func1<WxCode, Observable<WxAuthInfo>>() {
            @Override
            public Observable<WxAuthInfo> call(WxCode wxCode) {
                if (wxCode.errCode == 0 && !TextUtils.isEmpty(wxCode.code)) {
                    return AccountService.$().getWxAccessToken("authorization_code", wxCode.code);
                } else {
                    throw new ThirdPartExp("获取用户AccessToken失败");
                }
            }
        });
    }

    public Observable<WxUserInfo> getWxUserInfo(Activity activity, String wxAppId) {
        return getWxAuthInfo(activity, wxAppId).flatMap(new Func1<WxAuthInfo, Observable<WxUserInfo>>() {
            @Override
            public Observable<WxUserInfo> call(WxAuthInfo wxAuthInfo) {
                Log.e("WXEntryActivity", "wxAuthInfo = " + wxAuthInfo);
                if (wxAuthInfo == null || TextUtils.isEmpty(wxAuthInfo.getAccess_token()) || TextUtils.isEmpty(wxAuthInfo.getOpenid())) {
                    throw new ThirdPartExp(ThirdPartUtils.GET_USER_INFO_FAILURE);
                } else {
                    return AccountService.$().getWxUserInfo(wxAuthInfo.getAccess_token(), wxAuthInfo.getOpenid());
                }
            }
        });
    }


    public <T extends Activity & IThirdParty> Observable<QQUserInfo> getQQUserInfo(String appId, T activity) {
        return ThirdPartUtils.getQQToken(appId, activity).flatMap(new Func1<QQAuthInfo, Observable<QQUserInfo>>() {
            @Override
            public Observable<QQUserInfo> call(QQAuthInfo qqAuthInfo) {
                if (qqAuthInfo == null || TextUtils.isEmpty(qqAuthInfo.access_token) || TextUtils.isEmpty(qqAuthInfo.openid)) {
                    throw new ThirdPartExp(ThirdPartUtils.GET_USER_INFO_FAILURE);
                } else {
                    return AccountService.$().getQQUserInfo(qqAuthInfo.access_token, qqAuthInfo.openid);
                }
            }
        });
    }


    public <T extends Activity & IThirdParty> Observable<SinaUserInfo> getSinaUserInfo(final String appKey, T activity, SsoHandler ssoHandler) {
        return ThirdPartUtils.getSinaToken(appKey, activity, ssoHandler).flatMap(new Func1<Oauth2AccessToken, Observable<SinaUserInfo>>() {
            @Override
            public Observable<SinaUserInfo> call(Oauth2AccessToken oauth2AccessToken) {
                if (oauth2AccessToken == null || TextUtils.isEmpty(oauth2AccessToken.getToken()) || TextUtils.isEmpty(oauth2AccessToken.getUid())) {
                    throw new ThirdPartExp(ThirdPartUtils.GET_USER_INFO_FAILURE);
                } else {
                    return AccountService.$().getSinaUserInfo(oauth2AccessToken.getToken(), oauth2AccessToken.getUid());
                }
            }
        });
    }

}

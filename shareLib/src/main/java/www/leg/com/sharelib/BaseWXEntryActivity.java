package www.leg.com.sharelib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Locale;

public class BaseWXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI iwxapi;

    public static volatile IWXAPIEventHandler iwxapiEventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("WXEntryActivity", "call onCreate");
        super.onCreate(savedInstanceState);
        if (isAutoCreateWXAPI() && iwxapi == null) {
            initWXApi();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    /**
     * 初始化 IWXAPI
     */
    private void initWXApi() {
        Log.e("WXEntryActivity", "initWXApi");
        iwxapi = WXAPIFactory.createWXAPI(this, getAppId(), true);
        if (iwxapi.isWXAppInstalled()) {
            iwxapi.registerApp(getAppId());
        }
        iwxapi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e("WXEntryActivity", "onReq openId = " + baseReq.openId + "  transaction = " + baseReq.transaction);
        iwxapiEventHandler.onReq(baseReq);
        if (isAutoFinishAfterOnReq()) {
            finish();
        }
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.e("WXEntryActivity", "onResp" +
                String.format(Locale.getDefault(), " errCode = %d  errStr = %s  openId = %s  transaction = %s ", baseResp.errCode, baseResp.errStr, baseResp.openId, baseResp.transaction));
        if (null != iwxapiEventHandler) iwxapiEventHandler.onResp(baseResp);
        if (isAutoFinishAfterOnResp()) {
            finish();
        }
    }


    protected boolean isAutoFinishAfterOnReq() {
        return true;
    }

    protected boolean isAutoFinishAfterOnResp() {
        return true;
    }

    protected boolean isAutoCreateWXAPI() {
        return true;
    }

    /**
     * @return 微信AppID
     */
    protected String getAppId() {
        return "wxb2dc2adf1a07d618";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iwxapiEventHandler != null) {
            iwxapiEventHandler = null;
        }
    }

}

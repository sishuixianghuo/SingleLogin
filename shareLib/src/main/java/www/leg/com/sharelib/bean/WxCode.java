package www.leg.com.sharelib.bean;

import java.io.Serializable;

/**
 * Created by liushenghan on 2016/12/26.
 * 获取用微信的code
 */

public class WxCode implements Serializable {
    public String code;//		:	021pfWol1Kchoj0DAxrl1em2pl1pfWof

    public String country;//		:	CN

    public String lang;//		:	zh_CN

    public String state;//		:	com_p4u_android

    public String url;//		:	wx6b1e001fd47936d9://oauth?code=021pfWol1Kchoj0DAxrl1em2pl1pfWof&state=com_p4u_android

    public int errCode;//	:	0


    @Override
    public String toString() {
        return "WxCode{" +
                "code='" + code + '\'' +
                ", country='" + country + '\'' +
                ", lang='" + lang + '\'' +
                ", state='" + state + '\'' +
                ", url='" + url + '\'' +
                ", errCode=" + errCode +
                '}';
    }
}

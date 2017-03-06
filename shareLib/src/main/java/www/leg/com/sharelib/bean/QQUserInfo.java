package www.leg.com.sharelib.bean;

import java.io.Serializable;

/**
 * Created by liushenghan on 2016/12/25.
 * <p>
 * {"is_yellow_year_vip":"0","ret":0,"figureurl_qq_1":"http:\/\/q.qlogo.cn\/qqapp\/1105610454\/BDD393156389EBF57A67BB97B920F22E\/40","figureurl_qq_2":"http:\/\/q.qlogo.cn\/qqapp\/1105610454\/BDD393156389EBF57A67BB97B920F22E\/100",
 * "nickname":"伱要の행복я給卜⑦","yellow_vip_level":"0","is_lost":0,"msg":"","city":"","figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/1105610454\/BDD393156389EBF57A67BB97B920F22E\/50","vip":"0","level":"0",
 * "figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/1105610454\/BDD393156389EBF57A67BB97B920F22E\/100","province":"","is_yellow_vip":"0","gender":"男","figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/1105610454\/BDD393156389EBF57A67BB97B920F22E\/30"}
 */

public class QQUserInfo implements Serializable {

    public int is_yellow_year_vip;
    public int ret;
    public String figureurl_qq_1;
    public String figureurl_qq_2;
    public String nickname;
    int yellow_vip_level;
    public int is_lost;
    public String msg;
    public String city;
    public String figureurl_1;
    public int vip;
    public int level;
    public String figureurl_2;
    public String province;
    public int is_yellow_vip;
    public String gender;
    public String figureurl;

    @Override
    public String toString() {
        return "QQUserInfo{" +
                "is_yellow_year_vip=" + is_yellow_year_vip +
                ", ret=" + ret +
                ", figureurl_qq_1='" + figureurl_qq_1 + '\'' +
                ", figureurl_qq_2='" + figureurl_qq_2 + '\'' +
                ", nickname='" + nickname + '\'' +
                ", yellow_vip_level=" + yellow_vip_level +
                ", is_lost=" + is_lost +
                ", msg='" + msg + '\'' +
                ", city='" + city + '\'' +
                ", figureurl_1='" + figureurl_1 + '\'' +
                ", vip=" + vip +
                ", level=" + level +
                ", figureurl_2='" + figureurl_2 + '\'' +
                ", province='" + province + '\'' +
                ", is_yellow_vip=" + is_yellow_vip +
                ", gender='" + gender + '\'' +
                ", figureurl='" + figureurl + '\'' +
                '}';
    }
}

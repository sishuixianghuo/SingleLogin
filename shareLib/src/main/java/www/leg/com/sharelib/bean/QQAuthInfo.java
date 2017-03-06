package www.leg.com.sharelib.bean;

import java.io.Serializable;

/**
 * qq授权信息对象
 */
public class QQAuthInfo implements Serializable {

    public int ret;// ret==0，表示登录成功
    public String pay_token;
    public String pf;
    public int query_authority_cost;
    public int authority_cost;
    public String openid;
    public String expires_in;
    public String pfkey;
    public String msg;
    public String access_token;
    public String login_cost;

    @Override
    public String toString() {
        return "QQAuthInfo{" +
                "ret=" + ret +
                ", pf='" + pf + '\'' +
                ", openid='" + openid + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", pfkey='" + pfkey + '\'' +
                ", msg='" + msg + '\'' +
                ", access_token='" + access_token + '\'' +
                ", login_cost='" + login_cost + '\'' +
                '}';
    }


    //	http://wiki.connect.qq.com/get_user_info
//	参数说明 	描述
//	ret 	返回码
//	msg 	如果ret<0，会有相应的错误信息提示，返回数据全部用UTF-8编码。
//	nickname 	用户在QQ空间的昵称。
//	figureurl 	大小为30×30像素的QQ空间头像URL。
//	figureurl_1 	大小为50×50像素的QQ空间头像URL。
//	figureurl_2 	大小为100×100像素的QQ空间头像URL。
//	figureurl_qq_1 	大小为40×40像素的QQ头像URL。
//	figureurl_qq_2 	大小为100×100像素的QQ头像URL。需要注意，不是所有的用户都拥有QQ的100x100的头像，但40x40像素则是一定会有。
//	gender 	性别。 如果获取不到则默认返回"男"
//	is_yellow_vip 	标识用户是否为黄钻用户（0：不是；1：是）。
//	vip 	标识用户是否为黄钻用户（0：不是；1：是）
//	yellow_vip_level 	黄钻等级
//	level 	黄钻等级
//	is_yellow_year_vip 	标识是否为年费黄钻用户（0：不是； 1：是）

}

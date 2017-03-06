package www.leg.com.singlelogin;

/**
 * Created by liushenghan on 2017/2/27.
 */

public interface Config {


    String QQ_APP_ID = "1105447429";
    String QQ_APP_KEY = "iHaIX7HrJBmbC11Q";
    String WE_CHAT_APPID = "wxb2dc2adf1a07d618";
    String WE_CHAT_APPSECRET = "6cd4d907b8e66eda06a6dd9849379fb7";
    String SINA_APP_KEY = "275810222";
    String MCHID = "1405572602";


    String SWB_REDIRECT_URL = "http://123.57.244.52:8080/app/user/sinaAuth.do";//新浪微博回调页面
    String SWB_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

}

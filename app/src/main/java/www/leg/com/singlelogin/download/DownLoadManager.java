package test.test;

import java.lang.ref.SoftReference;
import java.util.WeakHashMap;

import okhttp3.Call;

/**
 * Created by liushenghan on 2017/3/9.
 */

public class DownLoadManager {
    private static volatile DownLoadManager manager;

    /**
     * 管理Call 进行取消
     */
    private WeakHashMap<String, SoftReference<Call>> calls;

    private DownLoadManager() {
        this.calls = new WeakHashMap<>();
    }

    public static DownLoadManager getManager() {
        if (null == manager) {
            synchronized (DownLoadManager.class) {
                if (null == manager) {
                    manager = new DownLoadManager();
                }
            }
        }
        return manager;
    }

    /**
     * @param url
     * @param call
     * @return
     */
    public SoftReference<Call> put(String url, Call call) {
        return calls.put(url, new SoftReference(call));
    }

    /**
     * @param url
     * @return
     */
    public Call get(String url) {
        SoftReference<Call> t = calls.get(url);
        return t.get();
    }
}

package www.leg.com.singlelogin.download;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
// Rx 2.x
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;

/**
 * Created by liushenghan on 2017/3/9.
 * <p>
 * 文件下载  文件支持断点下载
 * 文件上传
 */

public class OkHttpUitls {

    private static volatile OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            synchronized (OkHttpUitls.class) {
                if (client == null) {
                    client = new OkHttpClient();
                }
            }
        }
        return client;
    }

    /**
     * 获取文件总长度
     *
     * @param url
     * @param client
     * @return
     */
    public static long getFileLength(String url, OkHttpClient client) {
        Request request = new Request.Builder().url(url).tag(url).head().build();
        try {
            Response re = client.newCall(request).execute();
            if (re.isSuccessful()) {
                return re.body().contentLength();
            }
            re.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static Observable<DownLoadInfo> downFileNew(final String url, final String path, final OkHttpClient client) {

        return Observable.create(new Observable.OnSubscribe<DownLoadInfo>() {

            @Override
            public void call(final Subscriber<? super DownLoadInfo> subscriber) {
                Request request = new Request.Builder().url(url).tag(url).build();
                long length = getFileLength(url, client);// 文件的总长度
                final File file = new File(path);
                final DownLoadInfo info = new DownLoadInfo();
                info.total = length;
                if (file.exists()) {
                    Log.e("OkHttpUitls", String.format(Locale.getDefault(), "File length = %d   length = %d", file.length(), length));
                    if (length == file.length()) {
                        // 下载完成
                        info.sum = length;
                        subscriber.onNext(info);
                        subscriber.onCompleted();
                        return;
                    }
                    request = request.newBuilder()
                            .addHeader("RANGE", "bytes=" + file.length() + "-" + length).build();

                }
                Call call = client.newCall(request);
                // 添加到队列
                DownLoadManager.getManager().put(url, call);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException exc) {
                        subscriber.onError(exc);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        long length = response.body().contentLength();
                        Log.e("OkHttpUitls", String.format(Locale.getDefault(), "length = %d", length));
                        InputStream is = response.body().byteStream();
                        FileOutputStream fos = new FileOutputStream(file, file.exists());
                        if (file.exists()) {
                            info.sum = file.length();
                        }
                        byte[] buff = new byte[4 * 1024];
                        int len;
                        while ((len = is.read(buff)) != -1) {
                            fos.write(buff, 0, len);
                            info.sum += len;
                            Log.e("OkHttpUitls", String.format(Locale.getDefault(), "sum = %d   isDisposed = %b", info.sum, subscriber.isUnsubscribed()));
                            if (subscriber.isUnsubscribed()) {
                                call.cancel();
                            }
                            subscriber.onNext(info);
                        }
                        if (null != fos) {
                            fos.close();
                        }
                    }
                });
            }
        });
    }

//    private static Observable<DownLoadInfo> downFile(String url, String path, OkHttpClient client) {
//
//        return Observable.create(new ObservableOnSubscribe<DownLoadInfo>() {
//            @Override
//            public void subscribe(ObservableEmitter<DownLoadInfo> e) throws Exception {
//                Request request = new Request.Builder().url(url).tag(url).build();
//                Call call = client.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        long length = response.body().contentLength();
//                        Log.e("OkHttpUitls", String.format(Locale.getDefault(), "length = %d", length));
//                        InputStream is = response.body().byteStream();
//                        FileOutputStream fos = new FileOutputStream(path);
//                        DownLoadInfo info = new DownLoadInfo();
//                        info.total = response.body().contentLength();
//                        byte[] buff = new byte[4 * 1024];
//                        int len;
//                        while ((len = is.read(buff)) != -1) {
//                            fos.write(buff, 0, len);
//                            info.sum += len;
//                            Log.e("OkHttpUitls", String.format(Locale.getDefault(), "sum = %d   isDisposed = %b", info.sum, e.isDisposed()));
//                            if (e.isDisposed()) {
//                                call.cancel();
//                            }
//                            e.onNext(info);
//                        }
//                        if (null != fos) {
//                            fos.close();
//                        }
//                    }
//                });
//            }
//        });
//
//    }


}

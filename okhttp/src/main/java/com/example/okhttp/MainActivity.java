package com.example.okhttp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dns;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class MainActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/text; charset=utf-8");

    private OkHttpClient okHttpClient;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text_view);
        okHttpClient = new OkHttpClient.Builder()
                // 多个代理
                .proxySelector(new ProxySelector() {
                    @Override
                    public List<Proxy> select(URI uri) {
                        return null;
                    }

                    @Override
                    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

                    }
                })
                // 单独的代理
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("www.baidu.com", 8888)))
                // dns
                .dns(new Dns() {
                    @Override
                    public List<InetAddress> lookup(String hostname) {
                        return null;
                    }
                })
                .build();

        // 通过newBuilder方法，克隆okhttpClient，然后进行配置
//        OkHttpClient otherClient = okHttpClient.newBuilder()
//                .cache(new Cache())
//                .eventListener(new EventListener(){
//
//                }).build();


        findViewById(R.id.get_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get();
            }
        });
        findViewById(R.id.post_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    private void get() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Request request = new Request.Builder()
                        .url("http://www.baidu.com")
                        .build();
                try {
                    // execute为同步方法，不会新起线程
                    final Response response = okHttpClient.newCall(request).execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                textView.setText(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void post() {
        RequestBody body = RequestBody.create(JSON, "test");
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .post(body)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        okHttpClient.newCall(request)
                // enqueue为异步方法，在线程池中运行
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                        final String result = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(result);
                            }
                        });
                    }
                });
    }
}

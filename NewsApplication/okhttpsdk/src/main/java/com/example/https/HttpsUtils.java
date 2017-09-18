package com.example.https;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by yj on 2017/9/18.
 */

public class HttpsUtils {

    public static SSLSocketFactory getSslSocketFactory() {
        //生成一个信任管理器类
        X509TrustManager mTrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        //创建ssl上下文
        SSLContext sslContext = null;
        try {
            //与服务器保持一致
            sslContext = SSLContext.getInstance("SSL");
            X509TrustManager[] x509TrustArray = new X509TrustManager[]{mTrustManager};

            sslContext.init(null, x509TrustArray, new SecureRandom());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext.getSocketFactory();
    }
}

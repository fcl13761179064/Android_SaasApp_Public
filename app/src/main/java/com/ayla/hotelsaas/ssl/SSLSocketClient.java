package com.ayla.hotelsaas.ssl;

import android.content.Context;

import com.ayla.hotelsaas.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/*
*
* 相关格式说明
JKS：数字证书库。JKS里有KeyEntry和CertEntry，在库里的每个Entry都是靠别名（alias）来识别的。
P12：是PKCS12的缩写。同样是一个存储私钥的证书库，由.jks文件导出的，用户在PC平台安装，用于标示用户的身份。
CER：俗称数字证书，目的就是用于存储公钥证书，任何人都可以获取这个文件 。
BKS：由于Android平台不识别.keystore和.jks格式的证书库文件，因此Android平台引入一种的证书库格式，BKS。
作者：fanchunlei
*
* */
public class SSLSocketClient {
    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型
    private static final String KEY_STORE_PASSWORD = "123456";//证书密码（应该是客户端证书密码）
    private static final String KEY_STORE_TRUST_PASSWORD = "123456";//授信证书密码（应该是服务端证书密码）

    private final static String CLIENT_PRI_KEY = "client.bks";
    private final static String TRUSTSTORE_PUB_KEY = "truststore.bks";
    private final static String CLIENT_BKS_PASSWORD = "123456";
    private final static String TRUSTSTORE_BKS_PASSWORD = "123456";
    private final static String KEYSTORE_TYPE = "BKS";
    private final static String PROTOCOL_TYPE = "TLS";
    private final static String CERTIFICATE_FORMAT = "X509";

    /*
     *Android App编写BKS读取创建证书自定义的SSLSocketFactory
     * 客户端和服务端都是bks  双向证书验证
     * 客户端持有服务端的公钥证书，并持有自己的私钥，服务端持有客户的公钥证书，并持有自己私钥，
     */
    public static SSLSocketFactory getSSLCertifcation(Context context) {
        SSLSocketFactory sslSocketFactory = null;
        try {
            // 服务器端需要验证的客户端证书，其实就是客户端的keystore
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);// 客户端信任的服务器端证书
            KeyStore trustStore = KeyStore.getInstance(KEYSTORE_TYPE);//读取证书
            InputStream ksIn = context.getAssets().open(CLIENT_PRI_KEY);
            InputStream tsIn = context.getAssets().open(TRUSTSTORE_PUB_KEY);//加载证书
            keyStore.load(ksIn, CLIENT_BKS_PASSWORD.toCharArray());
            trustStore.load(tsIn, TRUSTSTORE_BKS_PASSWORD.toCharArray());
            ksIn.close();
            tsIn.close();
            //初始化SSLContext
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL_TYPE);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(CERTIFICATE_FORMAT);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(CERTIFICATE_FORMAT);
            trustManagerFactory.init(trustStore);
            keyManagerFactory.init(keyStore, CLIENT_BKS_PASSWORD.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }//省略各种异常处理，请自行添加
        catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }


    /*
     * p12证书
     *
     */

    public static SSLSocketFactory getP12SslSockerFactory(Context context) {
        InputStream trust_input = context.getResources().openRawResource(R.raw.trust);//服务器授信证书
        InputStream client_input = context.getResources().openRawResource(R.raw.client);//客户端证书
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            //服务器
            KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
            trustStore.load(trust_input, KEY_STORE_TRUST_PASSWORD.toCharArray());
            //客户端
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            keyStore.load(client_input, KEY_STORE_PASSWORD.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLSocketFactory factory = sslContext.getSocketFactory();
            return factory;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                trust_input.close();
                client_input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * pem证书
     *
     */
    public static final String tag = "CustomTrust";
    private static final String CLIENT_KET_PASSWORD = "214880157310437";
    static Context context;

    public static SSLSocketFactory getPemSslSocketfactory(Context ct) {
        context = ct;
        X509TrustManager trustManager;
        SSLSocketFactory sslSocketFactory = null;

        try {
            SSLContext sslContext = trustManagerForCertificates(trustedCertificatesInputStream()); //SSLContext.getInstance("TLS");
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }

    private static InputStream trustedCertificatesInputStream() {
        return context.getResources().openRawResource(R.raw.pem);
    }

    private static SSLContext trustManagerForCertificates(InputStream in) throws GeneralSecurityException, IOException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = CLIENT_KET_PASSWORD.toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        //  keyStore.load(in,CLIENT_KET_PASSWORD.toCharArray());
        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }

        SSLContext ssContext = SSLContext.getInstance("SSL");
        ssContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
        //return (X509TrustManager) trustManagers[0];
        return ssContext;
    }

    private static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }


    /*
     * 忽略验证 ,不安全的客户端验证
     *
     */
    public static SSLSocketFactory getUnSafeSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //获取TrustManager
    private static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }
        };
        return trustAllCerts;
    }
}

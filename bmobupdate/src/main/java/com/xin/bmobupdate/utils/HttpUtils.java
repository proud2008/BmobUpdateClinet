package com.xin.bmobupdate.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/12/4.
 */

public class HttpUtils {
    public static class HttpsUtils {
        public static class SSLParams {
            public SSLSocketFactory sSLSocketFactory;
            public X509TrustManager trustManager;
        }

        public static SSLParams getSslSocketFactory(InputStream[] certificates, InputStream bksFile, String password) {
            SSLParams sslParams = new SSLParams();
            try {
                TrustManager[] trustManagers = prepareTrustManager(certificates);
                KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                X509TrustManager trustManager = null;
                if (trustManagers != null) {
                    trustManager = new MyTrustManager(chooseTrustManager(trustManagers));
                } else {
                    trustManager = new UnSafeTrustManager();
                }
                sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
                sslParams.sSLSocketFactory = sslContext.getSocketFactory();
                sslParams.trustManager = trustManager;
                return sslParams;
            } catch (NoSuchAlgorithmException e) {
                throw new AssertionError(e);
            } catch (KeyManagementException e) {
                throw new AssertionError(e);
            } catch (KeyStoreException e) {
                throw new AssertionError(e);
            }
        }

        private class UnSafeHostnameVerifier implements HostnameVerifier {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }

        private static class UnSafeTrustManager implements X509TrustManager {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }

        private static TrustManager[] prepareTrustManager(InputStream... certificates) {
            if (certificates == null || certificates.length <= 0) return null;
            try {

                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null);
                int index = 0;
                for (InputStream certificate : certificates) {
                    String certificateAlias = Integer.toString(index++);
                    keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                    try {
                        if (certificate != null)
                            certificate.close();
                    } catch (IOException e)

                    {
                    }
                }
                TrustManagerFactory trustManagerFactory = null;

                trustManagerFactory = TrustManagerFactory.
                        getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

                return trustManagers;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
            try {
                if (bksFile == null || password == null) return null;

                KeyStore clientKeyStore = KeyStore.getInstance("BKS");
                clientKeyStore.load(bksFile, password.toCharArray());
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(clientKeyStore, password.toCharArray());
                return keyManagerFactory.getKeyManagers();

            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
            for (TrustManager trustManager : trustManagers) {
                if (trustManager instanceof X509TrustManager) {
                    return (X509TrustManager) trustManager;
                }
            }
            return null;
        }


        private static class MyTrustManager implements X509TrustManager {
            private X509TrustManager defaultTrustManager;
            private X509TrustManager localTrustManager;

            public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
                TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                var4.init((KeyStore) null);
                defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
                this.localTrustManager = localTrustManager;
            }


            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    defaultTrustManager.checkServerTrusted(chain, authType);
                } catch (CertificateException ce) {
                    localTrustManager.checkServerTrusted(chain, authType);
                }
            }


            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }
    }

    private static final String BmobCer =
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIFRTCCBC2gAwIBAgIQbr4W4FDZx/pWTyMv5kEiTjANBgkqhkiG9w0BAQsFADCB\n" +
                    "kDELMAkGA1UEBhMCR0IxGzAZBgNVBAgTEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4G\n" +
                    "A1UEBxMHU2FsZm9yZDEaMBgGA1UEChMRQ09NT0RPIENBIExpbWl0ZWQxNjA0BgNV\n" +
                    "BAMTLUNPTU9ETyBSU0EgRG9tYWluIFZhbGlkYXRpb24gU2VjdXJlIFNlcnZlciBD\n" +
                    "QTAeFw0xNTExMDgwMDAwMDBaFw0xODExMDcyMzU5NTlaME8xITAfBgNVBAsTGERv\n" +
                    "bWFpbiBDb250cm9sIFZhbGlkYXRlZDEUMBIGA1UECxMLUG9zaXRpdmVTU0wxFDAS\n" +
                    "BgNVBAMTC2FwaS5ibW9iLmNuMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC\n" +
                    "AQEAr/31ZmtjVid60Om8D3Az+p1GYV17LthV6EhWp4Rkk8KVzdct2wTiEaQJOjLX\n" +
                    "Siergra5YYjoUW91ympXdjQfR4/9/EMVg8/5N5GkdJMOTdCYBhieJeHdXCk4HkOq\n" +
                    "UIzXfchi1GjBO79bUproGB3ML+94rwCCuTaMKwKOFYkbjKbcBa6fkiOVdYu3ZzOE\n" +
                    "bWdGjUS1J9K2vsz+AVPq1XcXbo5o4xGakgNZWlYIkfd+987AtqzNC04pUF2GOya3\n" +
                    "6XHGLrvTq5ypfUPDcRv5AS1oXr2D4m4JKbwGHEdMIVyfgQGQgryAYiyE+S1+Xaze\n" +
                    "rjvfgfP1f3abTFPRDgV3cAtV/wIDAQABo4IB2TCCAdUwHwYDVR0jBBgwFoAUkK9q\n" +
                    "OpRaC9iQ6hJWc99DtDoo2ucwHQYDVR0OBBYEFLeOR6oa4hopr+dV9yzcb6ceD+bu\n" +
                    "MA4GA1UdDwEB/wQEAwIFoDAMBgNVHRMBAf8EAjAAMB0GA1UdJQQWMBQGCCsGAQUF\n" +
                    "BwMBBggrBgEFBQcDAjBPBgNVHSAESDBGMDoGCysGAQQBsjEBAgIHMCswKQYIKwYB\n" +
                    "BQUHAgEWHWh0dHBzOi8vc2VjdXJlLmNvbW9kby5jb20vQ1BTMAgGBmeBDAECATBU\n" +
                    "BgNVHR8ETTBLMEmgR6BFhkNodHRwOi8vY3JsLmNvbW9kb2NhLmNvbS9DT01PRE9S\n" +
                    "U0FEb21haW5WYWxpZGF0aW9uU2VjdXJlU2VydmVyQ0EuY3JsMIGFBggrBgEFBQcB\n" +
                    "AQR5MHcwTwYIKwYBBQUHMAKGQ2h0dHA6Ly9jcnQuY29tb2RvY2EuY29tL0NPTU9E\n" +
                    "T1JTQURvbWFpblZhbGlkYXRpb25TZWN1cmVTZXJ2ZXJDQS5jcnQwJAYIKwYBBQUH\n" +
                    "MAGGGGh0dHA6Ly9vY3NwLmNvbW9kb2NhLmNvbTAnBgNVHREEIDAeggthcGkuYm1v\n" +
                    "Yi5jboIPd3d3LmFwaS5ibW9iLmNuMA0GCSqGSIb3DQEBCwUAA4IBAQBtnrEPQthI\n" +
                    "RyNxtXC+P/WyuglJfXFC9FZHvx7YfC2urleMxgY3ZVKCTaxEWEmMNhGEyj3kqntc\n" +
                    "aff9cFnFjtkIqIUJLTt+pVnEdp87SiitiMtrFyjy1DnvwYSc+++zyJm+FS/0vCOB\n" +
                    "vZMBaXXXB+KrFtKqmnrAALMEG4KPtqzxo+4ePiJmNBgzWl7mO3srAOQsdWrqphEv\n" +
                    "R3zYVwntwzAg7BAUKVCOZgdq+80JFn1CGVg/dTKUnGqsvsEwoX2U8WcDiSXZKIyl\n" +
                    "HCN3i8F9TDjJl4dyecsYu40r80bGwOr9RNO9f5uON1HaTHikT9Qq8FWWhwB3Rp0O\n" +
                    "73fsJAHdsKhR\n" +
                    "-----END CERTIFICATE-----\n";

    private static OkHttpClient okHttpClient;

    /**
     * @return
     */

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient != null) {
            return okHttpClient;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            StringInputStream stringInputStream = new StringInputStream(BmobCer);
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(new InputStream[]{stringInputStream}, null, null);
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        } catch (Exception e) {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            builder.hostnameVerifier(hostnameVerifier);
        }

        okHttpClient = builder.readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)
                .connectTimeout(30, TimeUnit.MINUTES)
                .build();

        return okHttpClient;
    }

    private static class StringInputStream extends InputStream {
        private StringReader in;

        public StringInputStream(String source) {
            this.in = new StringReader(source);
        }

        public int read() throws IOException {
            return this.in.read();
        }

        public void close() throws IOException {
            this.in.close();
        }

        public synchronized void mark(int limit) {
            try {
                this.in.mark(limit);
            } catch (IOException var3) {
                throw new RuntimeException(var3.getMessage());
            }
        }

        public synchronized void reset() throws IOException {
            this.in.reset();
        }

        public boolean markSupported() {
            return this.in.markSupported();
        }
    }
}

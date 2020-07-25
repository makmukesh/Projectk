package com.vpipl.kalpamrit.Utils;

import android.annotation.TargetApi;
import android.net.SSLCertificateSocketFactory;
import android.os.Build;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


class MySSLSocketFactory extends SSLSocketFactory {

    KeyStore mkeyStore;
    String mkeyStorePassword;

    public MySSLSocketFactory(KeyStore keyStore) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException{
        super(null);
        mkeyStore=keyStore;
    }



    @Override
    public Socket connectSocket(Socket s, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException {
        return null;
    }

    @Override
    public Socket createSocket() throws IOException {
        return null;
    }

    @Override
    public boolean isSecure(Socket s) throws IllegalArgumentException {
        if (s instanceof SSLSocket) {
            return ((SSLSocket) s).isConnected();
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        SSLSocket sslSocket = null;


        if (autoClose) {
            socket.close();
        }


        SSLCertificateSocketFactory sslSocketFactory = (SSLCertificateSocketFactory) SSLCertificateSocketFactory.getDefault(0, null);


        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sslSocketFactory.setTrustManagers( new TrustManager[]{tm});

//            sslSocketFactory.setTrustManagers(new TrustManager[] { new SsX509TrustManager( mkeyStore, mkeyStorePassword) });


        sslSocket = (SSLSocket) sslSocketFactory.createSocket(InetAddress.getByName(host), port);

        sslSocket.setEnabledProtocols(sslSocket.getSupportedProtocols());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            sslSocketFactory.setHostname(sslSocket, host);
        } else {

            try {
                java.lang.reflect.Method setHostnameMethod = sslSocket.getClass().getMethod("setHostname", String.class);
                setHostnameMethod.invoke(sslSocket, host);
            } catch (Exception e) {

            }
        }


        return sslSocket;
    }

}
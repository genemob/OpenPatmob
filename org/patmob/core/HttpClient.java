/**
 * Compiles with Java 7
 */
package org.patmob.core;

import javax.swing.JOptionPane;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * One of PatMOb features is an extensive use of real-time data from EPO, 
 * USPTO and paid-for databases like PatBase. This is the class used for 
 * all HTTP connections. Configures 
 * <code>org.apache.http.impl.client.CloseableHttpClient</code>
 * for possible proxy issues, and returns it with 
 * <code>public static getInstance()</code>.
 * @author Piotr
 */
    public class HttpClient {
    static CloseableHttpClient httpClient = null;
    static RequestConfig requestConfig = null;
    static HttpClientContext context;
    static CredentialsProvider credsProvider;
    
    public static CloseableHttpClient getInstance() {
        if (httpClient==null) {
            setupHttpClient();
        }
        return httpClient;
    }
    
    private static void setupHttpClient() {
        // on first try requestConfig==null: connect without proxy
        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig);
        httpClient = clientBuilder.build();    
        HttpRequestBase httpRequest = new HttpGet("http://www.genemob.com/");
        HttpResponse httpResponse = null;
        
        try {
            httpResponse = httpClient.execute(httpRequest, context);
        } catch (HttpHostConnectException hhcx) {
            // there is a proxy
            //            patmobLogger.log(Level.SEVERE, null, hhcx);
            System.out.println("setupHttpClient1: " + hhcx);
            defineProxy();
        } catch (Exception x) {
            System.out.println("setupHttpClient2: " + x);
        }
        
        if (httpResponse!=null) analyzeResponse(httpResponse);
//            System.out.println("setupHttpClient3: " + httpResponse);
    }
    
    private static void defineProxy() {
        // Get proxy from properties file or from user - if needed
        String hostname = "globalproxy-amer.pharma.aventis.com";
        int port = 3129;
        
        RequestConfig.Builder rcBuilder = RequestConfig.custom();
        rcBuilder.setProxy(new HttpHost(hostname, port));
        requestConfig = rcBuilder.build();

        setupHttpClient();
    }
    
    private static void analyzeResponse(HttpResponse httpResponse) {
        if (httpResponse.getStatusLine().getStatusCode()==407) {
            // 407 Proxy Authentication Required
            defineProxyCredentials();
        } else {
            JOptionPane.showMessageDialog(null, "PatmobHttpClient: \n" + 
                    httpResponse.getStatusLine().toString());
        }
    }
    
    private static void defineProxyCredentials() {
        // Get creds from properties file or from user - if needed
        String user = "nm54935", 
                pass = "Genem0b78", 
                domain = "PHARMA";
        
        NTCredentials ntc = new NTCredentials(user, pass, "", domain);
        credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, ntc);
        context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);

        setupHttpClient();
    }
}
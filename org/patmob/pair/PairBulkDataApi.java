package org.patmob.pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.patmob.core.HttpClient;

/**
 *
 * @author Piotr
 */
public class PairBulkDataApi {
    static CloseableHttpClient httpClient = HttpClient.getInstance();
    
    private static HttpRequestBase getHttpRequest(JSONObject applicationQuery) {
        HttpPost httpPost = new HttpPost("https://pbd-prod-elb.uspto.gov/api/queries");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        
        StringEntity queryEntity = null;
        try {
            queryEntity = new StringEntity(applicationQuery.toString());
        } catch (UnsupportedEncodingException x) {
            System.out.println("StringEntity: " + x);
        }
        httpPost.setEntity(queryEntity);
        
        return httpPost;
    }
    
    private static JSONArray processResponse (HttpResponse httpResponse) {
        JSONArray jar = null;
        HttpEntity resultEntity = httpResponse.getEntity();
        if (resultEntity!=null) {
            try {
                InputStream is = resultEntity.getContent();
                BufferedReader br = 
                        new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line=br.readLine())!=null) {
                    sb.append(line);
                }
                JSONObject job2 = new JSONObject(sb.toString());
                JSONObject job3 = job2.getJSONObject("queryResults")
                        .getJSONObject("searchResponse")
                        .getJSONObject("response");
                jar = job3.optJSONArray("docs");
            } catch (IOException | UnsupportedOperationException | JSONException ex) {
                System.out.println("PairBulkDataApi.processResponse: " + ex);
            }
        }
        return jar;
    }
    
    private static JSONObject loadResults(JSONObject requestApplications, 
            JSONArray resultApplications, String usFullNumber) {
        for (int i=0; i<resultApplications.length(); i++) {
            if (resultApplications.getJSONObject(i)
                    .getString("appEarlyPubNumber")
                    .contains(usFullNumber)) {
                requestApplications.put(usFullNumber, 
                        resultApplications.getJSONObject(i));
            }
        }
        return requestApplications;
    }
    
//    private static String getQString(JSONObject requestApplications) {
//        StringBuilder sb = new StringBuilder();
//        Iterator<String> it = requestApplications.keys();
//        while (it.hasNext()) {
//            sb.append(it.next().substring(5, 12))
//                    .append(" ");
//        }
//System.out.println(sb.substring(0, sb.length()-1));
//        return sb.substring(0, sb.length()-1);
//    }
    
    private static JSONObject getApplications(JSONObject requestApplications) {
        JSONObject applicationQuery = new JSONObject()
                .put("qf", "appEarlyPubNumber")
                .put("fl", "applId, appEarlyPubNumber, patentTitle, appStatus, appStatusDate, patentNumberStr");
        
        Iterator<String> it = requestApplications.keys();
        while (it.hasNext()) {
            //"2016-0296602" usFullNumber
            String usFullNumber = it.next(),
                    usAppNumber = usFullNumber.substring(5);
            applicationQuery.put("searchText", usAppNumber);
            HttpRequestBase httpRequest = getHttpRequest(applicationQuery);
            HttpResponse httpResponse;
            try {
                httpResponse = httpClient.execute(httpRequest);
                if (httpResponse.getStatusLine().getStatusCode()==200) {
                    JSONArray resultApplications = processResponse(httpResponse);
                    loadResults(requestApplications, resultApplications, usFullNumber);
                }
            } catch (IOException x) {
                System.out.println("HttpResponse: " + x);
            }
        }
        
        return requestApplications;
    }
    
    /**
     * A batch query can be executed, but some requested applications were 
     * missing - found when searched individually.
     * @param requestApplications - JSONObject with publication numbers as keys:
     * {"2016-0296602": "not available", "2007-0299007": "not available"}
     * @return - requestApplications JSONObject, with values from PAIR, if found
     */
    public static JSONObject getApplicationData(JSONObject requestApplications) {    //{"2016-0296602": "not available"}
        JSONObject applicationData = getApplications(requestApplications);
        return applicationData;
    }
    
    public static JSONObject getGrantData() {
        return null;
    }
    
    
    
    
    
    
//    public static void usptoTest() {
//        System.out.println("*** usptoTest ***");
//        
//        HttpPost httpPost = new HttpPost("https://pbd-prod-elb.uspto.gov/api/queries");
//        httpPost.setHeader("Content-Type", "application/json");
//        httpPost.setHeader("Accept", "application/json");
//        JSONObject testJob = new JSONObject();
////        testJob.put("qf", "applId")
////                .put("searchText", "12050965 12471513 12828374 13172305 13498226"
////                        + " 14000942 14459440 14637415 14997148")
////                .put("fl", "applId, appEarlyPubNumber, patentTitle, appStatus, patentNumberStr");
//        testJob.put("qf", "appEarlyPubNumber")
//                .put("searchText", "0319012 0058840 0296602")
//                .put("fl", "applId, appEarlyPubNumber, patentTitle, appStatus, patentNumberStr");
//        
//        System.out.println("*** QUERY ***");
//        System.out.println(testJob.toString(2));
//        StringEntity queryEntity = null;
//        try {
//            queryEntity = new StringEntity(testJob.toString());
//        } catch (Exception x) {
//            System.out.println("Pair Bulk: " + x);
////            Logger.getLogger("patmobLogger").log(Level.SEVERE, null, x);
//        }
//        httpPost.setEntity(queryEntity);
//        
//        HttpResponse httpResponse = null;
//        try {
//            httpResponse = httpClient.execute(httpPost);                //httpGet
//            
//            if (httpResponse.getStatusLine().getStatusCode()==200) {
//                HttpEntity resultEntity = httpResponse.getEntity();
//                if (resultEntity!=null) {
//                    try {
//                        InputStream is = resultEntity.getContent();
//                        BufferedReader br = 
//                                new BufferedReader(new InputStreamReader(is));
//                        String line;
//                        StringBuilder sb = new StringBuilder();
//                        while ((line=br.readLine())!=null) {
//                            sb.append(line);
//                        }
//                        JSONObject job2 = new JSONObject(sb.toString());
//                        JSONObject job3 = job2.getJSONObject("queryResults")
//                                .getJSONObject("searchResponse")
//                                .getJSONObject("response");
//                        JSONArray jar = job3.optJSONArray("docs");
//                        
//                        System.out.println("*** RESULTS ***");
//                        System.out.println(jar.toString(2));
//                    } catch (Exception ex) {ex.printStackTrace();}
//                }
//            }
////            patmobLogger.info(" * ALL DONE * ");
//        } catch (IOException ex) {
//            System.out.println("Pair Bulk 2: " + ex);
////            Logger.getLogger("patmobLogger").log(Level.SEVERE, "CONN BOOBOO", ex);
//        }
//    }
    
    
    public static void main(String args[]) {
//        JSONObject requestApplications = new JSONObject()
//                .put("2010-0028372", "not available")
//                .put("2015-0071879", "not available")
//                .put("2012-0071402", "not available")
//                .put("2007-0299007", "not available")
//                .put("2008-0033147", "not available")
//                ;
        JSONObject requestApplications = new JSONObject()
                .put("2013-0252884", "not available")
                .put("2009-0305986", "not available")
                .put("2015-0210748", "not available")
                .put("2016-0115213", "not available")
                .put("2016-0319012", "not available")
                ;
        getApplicationData(requestApplications);
        System.out.println(getApplicationData(requestApplications).toString(1));
    }
}

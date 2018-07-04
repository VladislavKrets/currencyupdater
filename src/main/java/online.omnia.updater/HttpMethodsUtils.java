package online.omnia.updater;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lollipop on 12.07.2017.
 */
public class HttpMethodsUtils {


    public String anotherGetMethod(String url) {
        try {
            URL getUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder answerBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                answerBuilder.append(line);
            }
            reader.close();
            return answerBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getMethod(String url, Map<String, String> headers){
        if (!(url.startsWith("http://") || url.startsWith("https://"))) url = "https://" + url;
        HttpGet httpGet = null;

            httpGet = new HttpGet(url);

        try {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
            return getAnswer(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAnswer(HttpRequestBase http) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(http);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuilder answerBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            answerBuilder.append(line);
        }
        EntityUtils.consume(response.getEntity());
        reader.close();
        response.close();
        httpClient.close();
        return answerBuilder.toString();
    }

    public String postMethod(String url, List<NameValuePair> nameValuePairs, Map<String, String> headers) throws IOException {
        try {
            HttpPost httpPost = new HttpPost(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            return getAnswer(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

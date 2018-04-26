package davidlima.watsonpi.http;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonPost extends AsyncTask<String, String, String> {
    private JsonCallBack jsonCallBack;
    private String urlString;
    private String json;

    public JsonPost(JsonCallBack jsonCallBack, String url, String json) {
        this.jsonCallBack = jsonCallBack;
        this.urlString = url;
        this.json = json;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection conn;
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return e.toString();
        }
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Authorization", "Basic YmFiZmQzYmYtY2EzNC00MGIwLWI5Y2QtODY3NzFmYzM0ODMyOnNFN3RIaE9KMzVzbg==");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(json.getBytes().length));
            conn.setRequestProperty("Accept", "application/json");
            conn.setUseCaches(false);
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(json.getBytes());
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
        try {
            int response_code = conn.getResponseCode();
            if (response_code == HttpURLConnection.HTTP_OK) {
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return (result.toString());
            } else {
                InputStream input = conn.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return (result.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            conn.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("processed_language")) {
                jsonCallBack.success(result);
            } else {
                jsonCallBack.failed(jsonObject.getString("error"));
            }
        } catch (JSONException e) {
            jsonCallBack.failed("We couldn't connect to the Server, try again later");
        }
    }
}

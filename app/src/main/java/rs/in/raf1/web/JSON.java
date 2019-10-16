package rs.in.raf1.web;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class JSON {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String result = "";
	static HttpURLConnection urlConn = null;

    // constructor
    public JSON() {

    }

	public static JSONObject makeHttpRequest(String url, String method, HashMap<String, String> params) {

        Uri.Builder builder = new Uri.Builder();
        URL urlObject;

        // Making HTTP request
        try {
            urlObject = new URL(url);
            urlConn = (HttpURLConnection) urlObject.openConnection();
            urlConn.setRequestMethod(method);

            urlConn.connect();
            is = urlConn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");

                is.close();
                result = sb.toString();
            }


        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

}

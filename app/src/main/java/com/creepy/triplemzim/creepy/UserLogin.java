package com.creepy.triplemzim.creepy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static com.creepy.triplemzim.creepy.LoginActivity.CHECK_STRING;
import static com.creepy.triplemzim.creepy.LoginActivity.CHECK_STRING2;
import static com.creepy.triplemzim.creepy.LoginActivity.host;
import static com.creepy.triplemzim.creepy.LoginActivity.origin;
import static com.creepy.triplemzim.creepy.LoginActivity.referer;
import static com.creepy.triplemzim.creepy.LoginActivity.url;

/**
 * Created by HP on 11/30/2017.
 */

public class UserLogin extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "UserLogin";
    private final String mEmail;
    private final String mPassword;
    private final String USER_AGENT = "Mozilla/5.0";
    private HttpURLConnection connection;
    private List<String> cookies;
    public static boolean mAuthTask = false;
    public static boolean isSuccessful = false;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    int retryTime = 0;


    UserLogin(Context context, boolean firstTime) {
        mAuthTask = true;
        this.context = context;
        pref = context.getSharedPreferences("LoginActivity",Context.MODE_PRIVATE);
        editor = pref.edit();

        mEmail = pref.getString("username","null");
        mPassword = pref.getString("password","null");
//        pref = LoginActivity.pref;
//        editor = LoginActivity.editor;
        isSuccessful = false;
        if(firstTime == true){
            editor.putInt("retryNo", 1).apply();
        }

//            mEmail = "muhim";
//            mPassword = "";

    }

    @Override
    protected void onPreExecute() {
        int retryNo = pref.getInt("retryNo", 1);
        showNotification.onStart(context, "Attempting. Count: " + String.valueOf(retryNo));
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        try {
            CookieHandler.setDefault(new CookieManager());
            URL obj = new URL(url);

            String postParams = getPostParams();

            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Host", host);

            connection.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//                for (String cookie : this.cookies) {
//                    connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
//                }
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Referer", referer);
            connection.setRequestProperty("Origin", origin);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(postParams.length()));


            connection.setDoOutput(true);
            connection.setDoInput(true);

            Log.d(TAG, "doInBackground: \nSending 'POST' request to URL : " + url);


            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            Thread.sleep(2000);


            int responseCode = connection.getResponseCode();
//                Log.d(TAG, "doInBackground: \nSending 'POST' request to URL : " + url);
            Log.d(TAG, "doInBackground: Post parameters : " + postParams);
            System.out.println("Response Code : " + responseCode);


            BufferedReader in =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.d(TAG, "doInBackground: response Header: " + response);

            if(response.toString().contains(CHECK_STRING)  || response.toString().contains(CHECK_STRING2)){
                return true;
            }
            else return false;

            // Simulate network access.
        } catch (InterruptedException e) {
            return false;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private String getPostParams() {
        StringBuilder ret = new StringBuilder();

        try {
            ret.append(URLEncoder.encode("changePassword", "UTF-8"));
            ret.append('=');
            ret.append(URLEncoder.encode("0","UTF-8"));
            ret.append('&');

            ret.append(URLEncoder.encode("username", "UTF-8"));
            ret.append('=');
            ret.append(URLEncoder.encode(mEmail, "UTF-8"));
            ret.append('&');

            ret.append(URLEncoder.encode("password", "UTF-8"));
            ret.append('=');
            ret.append(URLEncoder.encode(mPassword,"UTF-8"));


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ret.toString();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        mAuthTask = false;

        if (success) {
            Log.d(TAG, "onPostExecute: Successful Login from userlogin");
//            nextJob();
            showNotification.onStart(context,"successful");
            updateData();
        } else {
            Log.d(TAG, "onPostExecute: Login Failed");


            int retryNo = pref.getInt("retryNo", 1);

            if(retryNo < 20){
//                showNotification.onStart(context, "failed! Trying again...Retry count: " + String.valueOf(retryNo));
                editor.putInt("retryNo", retryNo + 1).apply();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new UserLogin(context, false).execute((Void) null);
            }
            else{
                showNotification.onStart(context, "failed! Please use the app manually to login! ");
                editor.putInt("retryNo", 1).apply();
            }
        }
    }

    @Override
    protected void onCancelled() {
        mAuthTask = false;
//        showProgress(false);
        updateData();
    }

    private void updateData() {
        editor.putInt("retryNo", 1);
        isSuccessful = true;
        editor.putString("lastLogin", DateFormat.getDateTimeInstance().format(new Date()).toString());
        editor.putString("lastDate", DateFormat.getDateInstance().format(new Date()).toString());
        editor.commit();
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }
}

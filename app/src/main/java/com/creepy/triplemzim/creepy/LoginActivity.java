package com.creepy.triplemzim.creepy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private final static String TAG = "LoginActivity";
    public static String url = "http://hajirakhata.revesoft.com/Login.do";
    public static String host = "hajirakhata.revesoft.com";
    public static String referer = "http://hajirakhata.revesoft.com/";
    public static String origin = "http://hajirakhata.revesoft.com";
    public static final String CHECK_STRING = "View Leave Status";
    public static final String CHECK_STRING2 = "Server Time";

    // UI references.
    @BindView(R.id.email) EditText mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.email_sign_in_button) Button mEmailSignInButton;
    @BindView(R.id.login_progress) View mProgressView;
    @BindView(R.id.login_form) View mLoginFormView;
    @BindView(R.id.user) TextView textView;
    @BindView(R.id.submit) Button submitButton;
    @BindView(R.id.status) TextView stat;
    @BindView(R.id.msg) TextView message;
    @BindView(R.id.cDate) TextView cDate;
    @BindView(R.id.autoSwitch) Switch aSwitch;

    public SharedPreferences pref;
    public  SharedPreferences.Editor editor;

    public boolean autoCheck;
    private String usr, passwd;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private String ssid = "";
    public final Handler handler = new Handler();
    public Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // Set up the login form.
//        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
//        populateAutoComplete();

        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pref = this.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        editor = pref.edit();

        context = this;



//        dispatcher.mustSchedule(job);

//        pref.edit().putString("username","muhimmm").apply();
//        pref.edit().putString("password","none");
//        pref.edit().commit();

        updateAll();


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(aSwitch.isChecked()){
                    autoCheck = true;
                    editor.putString("autoCheck","true");
                }
                else{
                    autoCheck = false;
                    editor.putString("autoCheck","false");
//                    dispatcher.cancelAll();
                }
                editor.commit();
            }
        });


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        IntentFilter wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiReceiver, wifiIntentFilter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuinflater = getMenuInflater();
        menuinflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(LoginActivity.this, "Creepy খাতা v3.1\nDeveloper: TripleMZim\n" +
                "Software Engineer, REVE Systems\n" +
                "Email: triplemzim@gmail.com", Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }

    private void updateAll() {
        usr = pref.getString("username","None");
        passwd = pref.getString("password", "");

        Log.d(TAG, "onCreate: " + usr + passwd);

        if(pref.getString("autoCheck","false").equals("true")){
            autoCheck = true;
            aSwitch.setChecked(true);
        }
        else{
            autoCheck = false;
            aSwitch.setChecked(false);
        }

        cDate.setText("Current Date: " + DateFormat.getDateInstance().format(new Date()).toString());


        Log.d(TAG, "updateAll: " + usr);

        if(!usr.equals("None")){
            textView.setText("Previous user: " + usr);
            submitButton.setVisibility(View.VISIBLE);
        }
        else{
            textView.setText("No credential found!");
            submitButton.setVisibility(View.INVISIBLE);
        }

//        submitButton.setVisibility(View.VISIBLE);

        String last = pref.getString("lastLogin","null");

        message.setTextColor(getResources().getColor(R.color.colorPrimary));

        if(last.equals("null")){
            message.setText("No login information found!");
        }
        else{
            message.setText("Last login on " + pref.getString("lastLogin","no Data!"));
        }

        getWifiStatus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getWifiStatus();
        }
    };

    private void getWifiStatus() {
        wifiInfo = wifiManager.getConnectionInfo();
        ssid = "";
        ssid = wifiInfo.getSSID();

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = false;
        if(activeNetwork != null){
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }

        Log.d(TAG, "getWifiStatus: " + ssid);
        if(ssid.contains("ReveSystems") && wifiManager.isWifiEnabled() && isWiFi){
            stat.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            stat.setText("Good to go! Connected to ReveSystems!");
//            mEmailSignInButton.setEnabled(true);
            submitButton.setEnabled(true);
        }
        else{
            stat.setTextColor(getResources().getColor(R.color.colorRed));
            stat.setText("Warning! Please connect to ReveSystems network!");
//            mEmailSignInButton.setEnabled(false);
//            submitButton.setEnabled(false);

        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        editor.putString("username", email);
        editor.putString("password", password);

        mPasswordView.setText("");

        editor.commit();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }


    @OnClick(R.id.submit) void submitUser(){

        if (mAuthTask != null) {
            return;
        }

        showProgress(true);
        mAuthTask = new UserLoginTask(usr, passwd);
        mAuthTask.execute((Void) null);
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    public void nextJob(){
        //after successful login
        message.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        message.setText("Last login on " + DateFormat.getDateTimeInstance().format(new Date()).toString());

        editor.putString("lastLogin", DateFormat.getDateTimeInstance().format(new Date()).toString());
        editor.putString("lastDate", DateFormat.getDateInstance().format(new Date()).toString());

        editor.commit();
        updateAll();
    }



    public void showDialogmessage(boolean success){
        String msg;
        if(success){
            msg = "Login Successful! Yeee :)";
        }
        else{
            msg = "Login failed! Sorry :(";
        }
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setTitle("Hajirakhata")
                .setMessage(msg)
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        dialogBuilder.show();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String USER_AGENT = "Mozilla/5.0";
        private HttpURLConnection connection;
        private List<String> cookies;


        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
//            mEmail = "muhim";
//            mPassword = "";
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
                String inputLine = "";
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d(TAG, "doInBackground: response Header: " + response);

//                if(response.toString().contains(CHECK_STRING) || response.toString().contains(CHECK_STRING2)){
                if(response.toString().contains(CHECK_STRING2)){
                    return true;
                }
                return false;

                // Simulate network access.
            } catch (InterruptedException e) {
                return false;
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return false;
            }

//            return true;
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

//        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            showDialogmessage(success);
            if (success) {
                Log.d(TAG, "onPostExecute: Successful Login");
                nextJob();
                showNotification.onStart(getApplicationContext(), "successful!");
            } else {
                Log.d(TAG, "onPostExecute: Login Failed");
                mEmailView.setError(getString(R.string.error_incorrect_password));
//                mEmailView.requestFocus();

                updateAll();
                showNotification.onStart(getApplicationContext(), "failed!");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
            updateAll();
        }

        public void setCookies(List<String> cookies) {
            this.cookies = cookies;
        }
    }
}


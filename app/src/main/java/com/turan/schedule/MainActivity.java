package com.turan.schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "ScheduleSettings";
    ProgressBar loadingBar;
    SharedPreferences mSettings;
    EditText loginField, passwordField;
    TextView invalidLogin, invalidPassword;

    public Response globalResponse;
    public static final String HOST = "https://turan.evseenko.kz/api.php";
    public static final String AUTH_MASK = "action=auth&inputLogin=%s&inputPassword=%s";

    public static void close() {
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setLanguageForApp("ru");
        setContentView(R.layout.activity_main);
        loginField = findViewById(R.id.inputLogin);
        passwordField = findViewById(R.id.inputPassword);
        invalidLogin = findViewById(R.id.invalidLogin);
        invalidPassword = findViewById(R.id.invalidPassword);
        loadingBar = findViewById(R.id.loadingBar);
        mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (mSettings.getString("authToken", null) != null){
            loadingBar.setVisibility(View.VISIBLE);
            new PostExecute().execute("action=checkAuth&token=" + mSettings.getString("authToken", null), "CheckAuth");

        }
    }
    private void setLanguageForApp(String language){

        String languageToLoad  = language; //pass the language code as param
        Locale locale;
        if(languageToLoad.equals("not-set")){
            locale = Locale.getDefault();
        }
        else {
            locale = new Locale(languageToLoad);
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
    public String getAppVersion(){
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "none";
        }
    }

    public class PostExecute extends AsyncTask<String, Void, ResponseExecute> {

        protected ResponseExecute doInBackground(String... urls) {
            try {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    URL url = new URL("https://turan.evseenko.kz/api.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Length", "" + Integer.toString(urls[0].getBytes().length));
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    OutputStream os = connection.getOutputStream();
                    byte[] data = urls[0].getBytes("UTF-8");
                    os.write(data);
                    connection.connect();
                    InputStream inputStream = (connection.getResponseCode() == HttpURLConnection.HTTP_OK) ? connection.getInputStream(): connection.getErrorStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                    String content = rd.readLine();
                    return new ResponseExecute(urls[1], content);
                } else {
                    return new ResponseExecute(urls[1], "Check connection");
                }

            }catch (Exception ex){
                ex.printStackTrace();
                return new ResponseExecute(urls[1], "Error");
            }
        }

        protected void onPostExecute(ResponseExecute result) {
            switch (result.response){
                case "Check connection":{
                    Toast.makeText(getApplicationContext(),  R.string.check_connection, Toast.LENGTH_LONG).show();return;
                }
                case "Error":{
                    Toast.makeText(getApplicationContext(),  R.string.auth_error, Toast.LENGTH_LONG).show();return;
                }
            }
            switch (result.action){
                case "Auth":afterAuth(result.response);break;
                case "CheckAuth":afterCheckAuth(result.response);break;
            }

        }
    }
    public void afterCheckAuth(String result){
        loadingBar.setVisibility(View.GONE);
        System.out.println(result);
        try {
            Response response = new Gson().fromJson(result, Response.class);
            if (response.is_error){
                Toast.makeText(getApplicationContext(),  R.string.info_stop_session, Toast.LENGTH_LONG).show();
                mSettings.edit().remove("authToken").apply();
            }else {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivityForResult(intent, 1);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public void afterAuth(String result) {
        loadingBar.setVisibility(View.GONE);
        System.out.println(result);
        try {
            globalResponse = new Gson().fromJson(result, Response.class);
            if(globalResponse.is_error){
                globalResponse.printErrors();
                invalidPassword.setText("");
                invalidLogin.setText("");
                for (Error error : globalResponse.errors){
                    if(error.error_id == 3) {
                        invalidLogin.setText(error.text);
                        invalidPassword.setText(error.text);
                        passwordField.requestFocus();
                    }
                    if(error.error_id == 1) {
                        invalidLogin.setText(error.text);
                        loginField.requestFocus();
                    }
                    if(error.error_id == 2) {
                        invalidPassword.setText(error.text);
                        passwordField.requestFocus();
                    }
                }
            }else {
                System.out.println(globalResponse.token);
                mSettings.edit().putString("authToken", globalResponse.token).apply();
                Toast.makeText(getApplicationContext(),  R.string.success_auth, Toast.LENGTH_LONG).show();
                invalidPassword.setText("");
                invalidLogin.setText("");
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivityForResult(intent, 1);
            }
        }catch (Exception ex){
            System.err.println(ex.getMessage());
            loadingBar.setVisibility(View.GONE);
        }
    }
    public void clickLogin(View v){
        loadingBar.setVisibility(View.VISIBLE);
        new PostExecute().execute(String.format(AUTH_MASK, loginField.getText(), passwordField.getText()), "Auth");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }
}
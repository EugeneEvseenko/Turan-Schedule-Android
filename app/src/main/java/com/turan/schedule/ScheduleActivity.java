package com.turan.schedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.turan.schedule.MainActivity.APP_PREFERENCES;

public class ScheduleActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView headerName, headerEmail, headerPhone;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPreferences mSettings;
    RecyclerView recyclerView;
    CallsAdapter callsAdapter;
    ScheduleAdapter scheduleAdapter;
    ArrayList<String> basicList = new ArrayList<>();

    BasicAdapter basicAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        basicList.add(getString(R.string.schedule_not_exist));
        basicAdapter = new BasicAdapter(this, basicList);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.ScheduleList);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_500,R.color.purple_700,R.color.purple_200);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_schedule);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                toolbar.setSubtitle(R.string.info_updating);
                if (navigationView.getCheckedItem() == navigationView.getMenu().findItem(R.id.nav_calls)){
                    new PostExecute().execute("action=calls&token=" + mSettings.getString("authToken", null), "Calls");
                }else if (navigationView.getCheckedItem() == navigationView.getMenu().findItem(R.id.nav_schedule)){
                    new PostExecute().execute("action=schedule&token=" + mSettings.getString("authToken", null), "Schedule");
                }
            }
        });
        new PostExecute().execute("action=profile&token=" + mSettings.getString("authToken", null), "Profile");
        new PostExecute().execute("action=schedule&token=" + mSettings.getString("authToken", null), "Schedule");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        navigationView.setCheckedItem(id);
        switch (id){
            case R.id.nav_logout:{
                mSettings.edit().remove("authToken").apply();
                finish();
            }break;
            case R.id.nav_hphone:case R.id.nav_tphone:{
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ((id == R.id.nav_hphone) ? profile.group.headman.phone : profile.group.teacher.phone)));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }break;
            case R.id.nav_hemail:case R.id.nav_temail:{
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ (id == R.id.nav_hemail) ? profile.group.headman.email : profile.group.teacher.email});
                email.setType("message/rfc822");
                try {
                    startActivity(Intent.createChooser(email, getString(R.string.select_email)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }break;
            case R.id.nav_calls:{
                toolbar.setSubtitle(R.string.action_calls);
                new PostExecute().execute("action=calls&token=" + mSettings.getString("authToken", null), "Calls");
            }break;
            case R.id.nav_schedule:{
                toolbar.setSubtitle(R.string.action_schedule);
                new PostExecute().execute("action=schedule&token=" + mSettings.getString("authToken", null), "Schedule");
            }break;
            case R.id.nav_options:{
                Intent intent = new Intent(ScheduleActivity.this, SettingsActivity.class);
                startActivity(intent);
            }break;
            case R.id.nav_about:{
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
                //mDialogBuilder.setTitle(getString(R.string.about));
                LayoutInflater inflater = this.getLayoutInflater();
                View view = inflater.inflate(R.layout.about_layout, null);
                TextView title = (TextView) view.findViewById(R.id.about_title);
                title.setText(getString(R.string.app_name)  + " " + BuildConfig.VERSION_NAME);
                mDialogBuilder.setView(view);
                mDialogBuilder.setPositiveButton(R.string.dialog_close,
                        (dialog, did) -> {

                        });
                final AlertDialog mAlertDialog = mDialogBuilder.create();
                mAlertDialog.show();
            }break;
            case R.id.nav_web:{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://turan.evseenko.kz/"));
                startActivity(intent);
            }break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            System.out.println(result);
            switch (result.response){
                case "Check connection":{
                    Toast.makeText(getApplicationContext(),  R.string.check_connection, Toast.LENGTH_LONG).show();return;
                }
                case "Error":{
                    Toast.makeText(getApplicationContext(),  R.string.auth_error, Toast.LENGTH_LONG).show();return;
                }
            }
            switch (result.action){
                case "Schedule":afterSchedule(result.response);break;
                case "Calls":afterCalls(result.response);break;
                case "Profile":afterProfile(result.response);break;
            }
        }
    }
    ArrayList<Day> LessonsList = new ArrayList<Day>();
    public void afterSchedule(String result){
        System.out.println(result);
        Response response = new Gson().fromJson(result, Response.class);
        LessonsList = new ArrayList<Day>();
        if(response.schedule.monday.size() > 0 || response.schedule.tuesday.size() > 0 || response.schedule.wednesday.size() > 0 || response.schedule.thursday.size() > 0 || response.schedule.friday.size() > 0 || response.schedule.saturday.size() > 0 || response.schedule.sunday.size() > 0){
            if (response.schedule.monday.size() > 0) {
                LessonsList.add(
                        new Day(getString(R.string.monday), response.schedule.monday, 1)
                );
            }
            if (response.schedule.tuesday.size() > 0) {
                LessonsList.add(
                        new Day(getString(R.string.tuesday), response.schedule.tuesday, 2)
                );
            }
            if (response.schedule.wednesday.size() > 0) {
                LessonsList.add(
                        new Day(getString(R.string.wednesday), response.schedule.wednesday, 3)
                );
            }
            if (response.schedule.thursday.size() > 0) {
                LessonsList.add(
                        new Day(getString(R.string.thursday), response.schedule.thursday,4)
                );
            }
            if (response.schedule.friday.size() > 0) {
                LessonsList.add(
                        new Day(getString(R.string.friday), response.schedule.friday,5)
                );
            }
            if (response.schedule.saturday.size() > 0) {
                LessonsList.add(
                        new Day(getString(R.string.saturday), response.schedule.saturday,6)
                );
            }
            if (response.schedule.sunday.size() > 0) {
                LessonsList.add(
                        new Day(getString(R.string.sunday), response.schedule.sunday,7)
                );
            }
            mSettings.edit().putString("APP_CACHE_SCHEDULE",result).apply();
            scheduleAdapter = new ScheduleAdapter(this, LessonsList);
            recyclerView.setAdapter(scheduleAdapter);
        }else{
            recyclerView.setAdapter(basicAdapter);
        }
        toolbar.setSubtitle(R.string.action_schedule);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    ArrayList<Call> calls = new ArrayList<Call>();
    public void afterCalls(String result){
        System.out.println(result);
        Response response = new Gson().fromJson(result, Response.class);
        calls = response.calls;
        callsAdapter = new CallsAdapter(this, calls);
        recyclerView.setAdapter(callsAdapter);
        toolbar.setSubtitle(R.string.action_calls);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    Profile profile;
    public void afterProfile(String result){
        System.out.println(result);
        Response response = new Gson().fromJson(result, Response.class);
        profile = response.profile;
        if(response.profile.group != null) {
            toolbar.setTitle(response.profile.group.name);
            if (response.profile.group.teacher != null){
                navigationView.getMenu().findItem(R.id.nav_tphone).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_temail).setVisible(true);
                navigationView.getMenu().findItem(R.id.curator_item).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_tphone).setTitle(String.format(getString(R.string.nav_call_mask), response.profile.group.teacher.phone));
                navigationView.getMenu().findItem(R.id.nav_temail).setTitle(String.format(getString(R.string.nav_message_mask), response.profile.group.teacher.email));
                navigationView.getMenu().findItem(R.id.curator_item).setTitle(getString(R.string.nav_title_teacher) + " " + response.profile.group.teacher.name);
            }else {
                navigationView.getMenu().findItem(R.id.nav_tphone).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_temail).setVisible(false);
                navigationView.getMenu().findItem(R.id.curator_item).setVisible(false);
            }
            if (response.profile.group.headman != null && response.profile.group.headman.id != response.profile.id){
                navigationView.getMenu().findItem(R.id.nav_hphone).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_hemail).setVisible(true);
                navigationView.getMenu().findItem(R.id.headman_item).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_hphone).setTitle(String.format(getString(R.string.nav_call_mask), response.profile.group.headman.phone));
                navigationView.getMenu().findItem(R.id.nav_hemail).setTitle(String.format(getString(R.string.nav_message_mask), response.profile.group.headman.email));
                navigationView.getMenu().findItem(R.id.headman_item).setTitle(getString(R.string.nav_title_headman) + " " + response.profile.group.headman.name);
            }else {
                navigationView.getMenu().findItem(R.id.nav_hphone).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_hemail).setVisible(false);
                navigationView.getMenu().findItem(R.id.headman_item).setVisible(false);
            }
        }
        headerName = (TextView) findViewById(R.id.nav_title);
        headerEmail = (TextView) findViewById(R.id.nav_email);
        headerPhone = (TextView) findViewById(R.id.nav_phone);
        headerName.setText(response.profile.name);
        headerEmail.setText(response.profile.email);
        headerPhone.setText(response.profile.phone);
    }
    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                setResult(RESULT_OK, null);
                finish();
            }
            else
                Toast.makeText(getBaseContext(), R.string.info_exit, Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }
}
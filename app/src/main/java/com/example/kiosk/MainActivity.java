package com.example.kiosk;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.kiosk.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import java.util.Locale;


public class MainActivity extends AppCompatActivity  {

    public static final Integer RecordAudioRequestCode = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ConexionSQLiteHelper conn;

    public void initDB(){
        if(conn!=null){
            conn.resetTables();
            Product p1 = new Product("descr1", 2L, (short) 0, "codi", true, (short) 0, "tagComment");
            Product p2 = new Product("descr2", 2L, (short) 0, "codi", true, (short) 0, "tagComment");
            Product p3 = new Product("descr3", 2L, (short) 0, "codi", true, (short) 0, "tagComment");
            Product p4 = new Product("descr4", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p5 = new Product("descr5", 2L, (short) 0, "codi", true, (short) 0, "tagComment");
            Product p6 = new Product("descr6", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p7 = new Product("descr7", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p8 = new Product("descr8", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p9 = new Product("descr9", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p10 = new Product("descr10", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p11 = new Product("descr11", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p12 = new Product("descr12", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p13 = new Product("descr13", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p14 = new Product("descr14", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p15 = new Product("descr15", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p16 = new Product("descr16", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p17 = new Product("descr17", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p18 = new Product("descr18", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p19 = new Product("descr19", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p20 = new Product("descr20", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p21 = new Product("descr21", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p22 = new Product("descr22", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p23 = new Product("descr23", 2L, (short) 0, "codi", false, (short) 0, "tagComment");
            Product p24 = new Product("descr24", 2L, (short) 0, "codi", false, (short) 0, "tagComment");




            conn.agregarProductos(p1);
            conn.agregarProductos(p2);
            conn.agregarProductos(p3);
            conn.agregarProductos(p4);
            conn.agregarProductos(p5);
            conn.agregarProductos(p6);
            conn.agregarProductos(p7);
            conn.agregarProductos(p8);
            conn.agregarProductos(p9);
            conn.agregarProductos(p10);
            conn.agregarProductos(p11);
            conn.agregarProductos(p12);
            conn.agregarProductos(p13);
            conn.agregarProductos(p14);
            conn.agregarProductos(p15);
            conn.agregarProductos(p16);
            conn.agregarProductos(p17);
            conn.agregarProductos(p18);
            conn.agregarProductos(p19);
            conn.agregarProductos(p20);
            conn.agregarProductos(p21);
            conn.agregarProductos(p22);
            conn.agregarProductos(p23);
            conn.agregarProductos(p24);


            Log.println(Log.INFO,"DB","Data Base succesfully initialized");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager;
        activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public void onBackPressed() {
        // nothing to do here
        // … really
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //if(!hasFocus) {
            // Close every kind of system dialog
            //Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            //sendBroadcast(closeDialog);
        //}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set language
        Locale locale;
        locale = new Locale("es", "ES");
        Configuration config = new Configuration(getApplicationContext().getResources().getConfiguration());
        Locale.setDefault(locale);
        config.setLocale(locale);
        getApplicationContext().deleteDatabase("product_db");
        conn = new ConexionSQLiteHelper(this);
        initDB();
        showDB();

        getApplicationContext().getResources().updateConfiguration(config,
        getApplicationContext().getResources().getDisplayMetrics());

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        if(getIntent().getBooleanExtra("LOGOUT", false)){
            finish();
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    private void showDB() {
        Log.println(Log.INFO,"DB", "Showing db");
        Cursor cursor = conn.getAllProducts();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            System.out.println(cursor.getString(1) + " " +cursor.getString(2) + " " + cursor.getString(3) + " " + cursor.getString(4) + " " + cursor.getString(5) + " " + cursor.getString(6));
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permisos Otorgados",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
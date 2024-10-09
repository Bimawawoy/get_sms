package com.example.smsunittest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null) {
            // Ambil data SMS yang diterima
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "Message from " + msgs[i].getOriginatingAddress();
                str += ": " + msgs[i].getMessageBody() + "\n";
            }

            // Menampilkan SMS baru sebagai Toast atau bisa dikirimkan ke Activity
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            
            // Jika ingin menampilkan dialog di MainActivity, kirimkan broadcast atau intent ke MainActivity
            Intent smsIntent = new Intent("com.example.smsunittest.NEW_SMS");
            smsIntent.putExtra("sms_body", str);
            context.sendBroadcast(smsIntent);
        }
    }
}

package com.example.mobileproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class MySmsReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        SmsMessage[] messages = parseSmsMessage(bundle);

        //메세지 내용이 있을 경우 작동
        if(messages != null && messages.length > 0) {

            //내용
            String contents = messages[0].getMessageBody();
            ((MainActivity)MainActivity.mContext).SMSInsert(contents);
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle) {

        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for (int i = 0; i < objs.length; i++) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }
        return messages;
    }
}

package com.tribe.tribeandroid;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class SentMessageService extends Service {

    public static final String CONTENT_SMS = "content://sms/";
    static String messageId = "";
    private static final String address = "http://10.0.0.10:3020/";
    private static final String postSms = "postSms/";



    public SentMessageService() {
    }

    @Override
    public void onCreate() {
        MyContentObserver contentObserver = new MyContentObserver();
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse(CONTENT_SMS),true, contentObserver);
        Log.v("Caller History: Service Started", "OutgoingSMSReceiverService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        // start asynctask to get off this thread
            if(intent == null)
                return Service.START_FLAG_REDELIVERY;
            else
                return Service.START_STICKY;


    }

    private class MyContentObserver extends ContentObserver {


        public MyContentObserver() {
            super(null);
        }



        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Uri uriSMSURI = Uri.parse(CONTENT_SMS);
            Cursor cur = SentMessageService.this.getApplicationContext().getContentResolver().query(uriSMSURI, null, null, null, null);
            // this will make it point to the first record, which is the last SMS sent
            if(cur.moveToNext()) {
                String message_id = cur.getString(cur.getColumnIndex("_id"));
                String type = cur.getString(cur.getColumnIndex("type"));
                String number= cur.getString(cur.getColumnIndex("address")).trim();


                if (type.equals("2")) {

                    if (!message_id.equals(messageId)) {
                        number = android.telephony.PhoneNumberUtils.stripSeparators(number);
                        String content = cur.getString(cur.getColumnIndex("body"));
                        if(number.length() == 10){
                            number = "1"+number;
                        }

                        TelephonyManager telephoneManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        String myNumber = telephoneManager.getLine1Number();
                        myNumber = myNumber.substring(1);

                        Log.d("TAG",address + postSms + myNumber +"/"+number+"/"+content.length());
                        Ion.with(getApplicationContext()).load(address + postSms + myNumber +"/"+number+"/"+content.length()).setBodyParameter("dummy","dummy")
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        if (e != null) {
                                            e.printStackTrace();

                                        } else {

                                        }

                                    }
                                });
                        Log.d("TYPE", "Sent message to :" + number);
                    }
                    messageId = message_id;
                }
            }

        }
    }






}




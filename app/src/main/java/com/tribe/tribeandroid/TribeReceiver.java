package com.tribe.tribeandroid;

/**
 * Created by christianvazquez on 3/6/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;


public class TribeReceiver extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();
    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static String mLastState = "IDLE";
    private static boolean myCall = false;
    private static final String address = "http://10.0.0.10:3020/"; //Todo: Set up ddns server to stop using ip
    private static final String postSms = "postSms/";
    private static final String postCall = "postCall/";




    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals(ACTION_SMS_RECEIVED)) {
            handleSms(intent,context);
        } else if (action.equals("android.intent.action.PHONE_STATE")){
            handleCallEnd(intent,context);
        } else if (action.equals("android.intent.action.BOOT_COMPLETED")){
            Intent bluetoothServiceIntent = new Intent(context,SentMessageService.class);
            context.startService(bluetoothServiceIntent);
        } else if (action.equals("com.tribe.tribeandroid.SEND_SMS")){
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                String notificationText = json.getString("alert");
                String[] splitMessage = notificationText.split("_");

                String number = splitMessage[0];
                String message = splitMessage[1];
                Log.d("Test",number);
                Log.d("Test",message);

                SmsManager smsManager = SmsManager.getDefault(); //get the manager
                smsManager.sendTextMessage(number,null,message,null,null);  //send the message

            } catch (Exception e){
                Log.d("ERROR","Problem parsing push notification");
            }
        }

    }

    private void handleCallEnd(Intent intent,Context context) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if(mLastState != null && !mLastState.equals(state)){
            if(state.equals("RINGING")){
                Log.d("STATE","getting call user");
            } else if (state.equals("OFFHOOK")){
                if(mLastState.equals("IDLE")) {
                    Log.d("STATE", "Making Call");
                    myCall = true;

                }
                else if (mLastState.equals("RINGING")) {
                    Log.d("STATE", "Picking call up");
                    myCall = false;
                }

            } else if (state.equals("IDLE")){
                if (mLastState.equals("RINGING"))
                    Log.d("STATE","Rejected");
                if (mLastState.equals("OFFHOOK")) {

                    Uri contacts = CallLog.Calls.CONTENT_URI;
                    try {
                        Thread.sleep(200); //ensures latest data in call log is synced
                    } catch(Exception e){

                    }
                    Cursor managedCursor = context.getContentResolver().query(
                            contacts, null, null, null, null);
                    int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
                    int duration1 = managedCursor.getColumnIndex( CallLog.Calls.DURATION);

                    if( managedCursor.moveToLast() == true ) {
                        String phNumber = managedCursor.getString( number );
                        String callDuration = managedCursor.getString( duration1 );
                        if(phNumber.length() == 10){
                            phNumber = "1"+phNumber;
                        }

                        TelephonyManager telephoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        String myNumber = telephoneManager.getLine1Number();
                        myNumber = myNumber.substring(1);

                        if(myCall) {
                            Log.d("STATE", "I called - number: " + phNumber + " duration:" + callDuration);
                            postCall(context,myNumber,phNumber,callDuration);
                        }
                        else {
                            Log.d("STATE", "I was called  by - number: " + phNumber + " duration:" + callDuration);
                            postCall(context,phNumber,myNumber,callDuration);
                        }

                    }


                    managedCursor.close();
                }
            }
            mLastState = state;

        }
    }

    private void handleSms(Intent intent,Context context) {
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    senderNum = android.telephony.PhoneNumberUtils.stripSeparators(senderNum);
                    if(senderNum.contains("+"))
                        senderNum = senderNum.substring(1);
                    if(senderNum.length() == 10){
                        senderNum = "1"+senderNum;
                    }
                    TelephonyManager telephoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String myNumber = telephoneManager.getLine1Number();
                    myNumber = myNumber.substring(1);
                    String message = currentMessage.getDisplayMessageBody();

                    Log.d("TAG",address + postSms + senderNum +"/"+myNumber+"/"+message.length());
                    Ion.with(context).load(address + postSms + senderNum +"/"+myNumber+"/"+message.length()).setBodyParameter("dummy","dummy")
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

                    Log.d("STATE", "Got text from: "+ senderNum);
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
    }

    private void postCall(Context context,String caller, String receiver, String duration){

        Ion.with(context).load(address + postCall + caller +"/"+receiver+"/"+duration).setBodyParameter("dummy","dummy")
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

    }


}

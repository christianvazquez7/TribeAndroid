package com.tribe.tribeandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class TribeActivity extends Activity {

    private TextView prompt;
    private TextView actionButton;
    private ListView friendList;
    private boolean finished;
    private int friendsLeft = 5;
    private TribeFriendAdapter adapter;
    private static final int PICK_CONTACT = 1;
    private static final String address = "http://10.0.0.10:3020/"; //Todo: Your ip goes here
    private static final String register = "addFriends/";
    private String myNumber;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tribe);
        Intent bluetoothServiceIntent = new Intent(this,SentMessageService.class);
        this.startService(bluetoothServiceIntent);

        TelephonyManager telephoneManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        myNumber = telephoneManager.getLine1Number();
        if(myNumber.equals("+15126389698"))
            myNumber = "+17876131066";
        myNumber = myNumber.substring(1);


        prompt = (TextView) this.findViewById(R.id.prompt);
        actionButton = (TextView) this.findViewById(R.id.actionButton);
        friendList = (ListView) this.findViewById(R.id.list);

        adapter = new TribeFriendAdapter(this,R.layout.friend_view){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position,convertView,parent);


                View deleteButton =  view.findViewById(R.id.delete);
                deleteButton.setTag(position);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer index = (Integer) v.getTag();
                        remove(getItem(index));
                        notifyDataSetChanged();
                        friendsLeft ++;
                        prompt.setText("Add "+friendsLeft+" friends to your tribe");

                    }
                });

                return  view;


            }

        };
        friendList.setAdapter(adapter);


        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!finished) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                } else {
                    //send request to server
                    actionButton.setEnabled(false);
                    Log.d("NETWORK",address + register + myNumber);
                    Ion.with(TribeActivity.this).load(address + register + myNumber)
                            .setBodyParameter("f1Phone",adapter.getItem(0).getNumber())
                            .setBodyParameter("f2Phone",adapter.getItem(1).getNumber())
                            .setBodyParameter("f3Phone",adapter.getItem(2).getNumber())
                            .setBodyParameter("f4Phone",adapter.getItem(3).getNumber())
                            .setBodyParameter("f5Phone",adapter.getItem(4).getNumber())
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e != null) {
                                        e.printStackTrace();

                                    } else {
                                        //Todo: What happens after we send the numbers?
                                    }
                                    actionButton.setEnabled(true);

                                }
                            });
                }
            }
        });







    }

    private void addFriend(){

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tribe, menu);
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
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex("data1"));
                            cNumber = android.telephony.PhoneNumberUtils.stripSeparators(cNumber);
                            if(cNumber.length() != 11)
                                cNumber = "1" + cNumber;
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            adapter.add(new Friend(name,cNumber));
                            friendsLeft--;
                            prompt.setText("Add "+friendsLeft+" friends to your tribe");
                            adapter.notifyDataSetChanged();
                            if(friendsLeft <= 0){
                                prompt.setText("You're all set!");
                                actionButton.setText("Register");
                                finished = true;
                            }
                        }



                    }
                }
                break;
        }

    }



}

package com.tribe.tribeandroid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
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
import com.parse.ParsePush;

import java.util.ArrayList;


public class TribeActivity extends FragmentActivity {

    private TextView prompt;
    private View actionButton;
    private ListView friendList;
    private boolean finished;
    private int friendsLeft = 4;
    private TribeFriendAdapter adapter;
    private static final int PICK_CONTACT = 1;
    private static final String address = "http://10.0.0.10:3020/"; //Todo: Your ip goes here
    private static final String register = "addFriends/";
    private String myNumber;
    private View done;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tribe);
        Intent bluetoothServiceIntent = new Intent(this,SentMessageService.class);
        this.startService(bluetoothServiceIntent);
        actionButton = findViewById(R.id.actionButton);
        TelephonyManager telephoneManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        myNumber = telephoneManager.getLine1Number();
        if(myNumber == null)
            myNumber = "+17876131066";
        if(myNumber.equals("+15126389698"))
            myNumber = "+17876131066";

        myNumber = myNumber.substring(1);
        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send request to server
                if (finished) {
                    actionButton.setEnabled(false);
                    Log.d("NETWORK", address + register + myNumber);
                    //TODO: Must iterate through numbers and also send state info
                    Ion.with(TribeActivity.this).load(address + register + myNumber)
                            .setBodyParameter("f1Phone", adapter.getItem(0).getConcatNumbers())
                            .setBodyParameter("f2Phone", adapter.getItem(1).getConcatNumbers())
                            .setBodyParameter("f3Phone", adapter.getItem(2).getConcatNumbers())
                            .setBodyParameter("f4Phone", adapter.getItem(3).getConcatNumbers())
                            .setBodyParameter("f5Phone", adapter.getItem(4).getConcatNumbers())
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

        ParsePush.subscribeInBackground("participant_"+myNumber);

        prompt = (TextView) this.findViewById(R.id.prompt);
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
                        if(friendsLeft > 0){
                            finished = false;
                            done.setBackgroundColor(getResources().getColor(R.color.grey));
                        }


                    }
                });

                return  view;


            }

        };
        friendList.setAdapter(adapter);


        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);


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
                            String name="";
                            ArrayList<String> numbers = new ArrayList<String>();
                            while(phones.moveToNext()) {
                                //phones.moveToFirst();
                                String cNumber = phones.getString(phones.getColumnIndex("data1"));
                                cNumber = android.telephony.PhoneNumberUtils.stripSeparators(cNumber);
                                if (cNumber.length() != 11)
                                    cNumber = "1" + cNumber;
                                name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                numbers.add(cNumber);
                            }
                            Friend friend =new Friend(name, numbers);
                            DialogState f = DialogState.newInstance(1,1,1);
                            f.setFriend(friend,adapter);
                            f.show(getSupportFragmentManager(), "TAG");
                            adapter.add(friend);
                            friendsLeft--;
                            adapter.notifyDataSetChanged();
                            if(friendsLeft <= 0){
                                finished = true;
                                done.setBackgroundColor(getResources().getColor(R.color.green));

                            }
                        }



                    }
                }
                break;
        }

    }



}

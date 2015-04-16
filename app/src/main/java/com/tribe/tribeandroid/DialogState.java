package com.tribe.tribeandroid;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.tribe.tribeandroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christianvazquez on 1/30/15.
 */
public class DialogState extends DialogFragment {


    private Spinner spinner;
    private String state;
    private View done;
    private Friend friend;
    private TribeFriendAdapter adapter;


    static DialogState newInstance(int day, int hour,int minute) {
        DialogState f = new DialogState();
        return f;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void onResume()
    {
        super.onResume();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);

        List<String> states = new ArrayList<String>();
        states.add("Alabama");
        states.add("Alaska");
        states.add("Arizona");
        states.add("Arkansas");
        states.add("California");
        states.add("Colorado");
        states.add("Delaware");
        states.add("Florida");
        states.add("Georgia");
        states.add("Hawaii");
        states.add("Idaho");
        states.add("Illinois");
        states.add("Indiana");
        states.add("Iowa");
        states.add("Kansas");
        states.add("Kentucky");
        states.add("Lousiana");
        states.add("Maine");
        states.add("Maryland");
        states.add("Massachusetts");
        states.add("Michigan");
        states.add("Minnesota");
        states.add("Mississippi");
        states.add("Missouri");
        states.add("Montana");
        states.add("Nebraska");
        states.add("Nevada");
        states.add("New Hampshire");
        states.add("New Jersey");
        states.add("New Mexico");
        states.add("New York");
        states.add("North Carolina");
        states.add("North Dakota");
        states.add("Ohio");
        states.add("Oklahoma");
        states.add("Oregon");
        states.add("Pennsylvania");
        states.add("Puerto Rico");
        states.add("Rhode Island");
        states.add("South Carolina");
        states.add("South Dakota");
        states.add("Tennessee");
        states.add("Texas");
        states.add("Utah");
        states.add("Vermont");
        states.add("Virginia");
        states.add("Washington");
        states.add("West Virginia");
        states.add("Wisconsin");
        states.add("Wyoming");
        this.setCancelable(false);








        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,states);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.state_select_view, container, false);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        done = rootView.findViewById(R.id.submit);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = String.valueOf(spinner.getSelectedItem());
                DialogState.this.dismiss();
            }
        });

        return rootView;
    }

    public void setFriend(Friend f,TribeFriendAdapter a){
        friend = f;
        adapter = a;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        friend.setState(state);
        Log.d("HELLO","HERE");
        adapter.notifyDataSetChanged();
    }







}
package com.tribe.tribeandroid;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;




public class TribeFriendAdapter extends ArrayAdapter<Friend> {

    private Context mContext;
    private TextView friendName;
    private TextView friendPhone;



    public TribeFriendAdapter(Context context, int resource) {
        super(context, R.layout.friend_view);
        mContext = context;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            Activity ac = (Activity) mContext;
            LayoutInflater inflater = ac.getLayoutInflater();
            convertView = inflater.inflate(R.layout.friend_view,parent,false);
        }

        friendName = (TextView) convertView.findViewById(R.id.friendName);
        friendPhone = (TextView) convertView.findViewById(R.id.friendPhone);
        friendPhone.setText("");
        if(getItem(position).getState() != null){
            friendPhone.setText(getItem(position).getState());
        }
        friendName.setText(getItem(position).getName());



        return convertView;

    }





}
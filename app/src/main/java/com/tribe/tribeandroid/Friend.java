package com.tribe.tribeandroid;

import java.util.ArrayList;

/**
 * Created by christianvazquez on 3/8/15.
 */
public class Friend {
    private final String name;
    private final String number;
    private String state;
    private final ArrayList<String> phoneNumbers;

    public Friend(String name,String number){
        this.name = name;
        this.number = number;
        ArrayList<String> pn = new ArrayList<String>();
        pn.add(number);
        this.phoneNumbers = pn;
    }

    public Friend(String name,ArrayList<String> phoneNumbers){
        this.name = name;
        this.phoneNumbers = phoneNumbers;
        this.number = phoneNumbers.get(0);
    }

    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }

    public String getConcatNumbers(){
        String toSend="";
        for(int i=0; i<phoneNumbers.size();i++){
            toSend+=phoneNumbers.get(i);
            if(i!= phoneNumbers.size()-1){
                toSend+="_";
            }
        }
        return toSend;
    }

    public ArrayList<String> getPhoneNumbers(){
        return phoneNumbers;
    }

    public void setState(String state){
        this.state = state;
    }

    public String getState()
    {
        return state;
    }
}

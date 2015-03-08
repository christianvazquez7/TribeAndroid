package com.tribe.tribeandroid;

/**
 * Created by christianvazquez on 3/8/15.
 */
public class Friend {
    private final String name;
    private final String number;

    public Friend(String name,String number){
        this.name = name;
        this.number = number;
    }

    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }
}

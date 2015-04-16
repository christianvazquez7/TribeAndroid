package com.tribe.tribeandroid;

/**
 * Created by christianvazquez on 3/10/15.
 */
import android.app.Application;

import com.parse.Parse;

public class CustomApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Parse.initialize(this, "gPBIBX1zyoP66ncbyHSdqxf7FfMPit4SUoweT4Ag", "8N7y20kMVP3dwzO547Ia4ZfiRxtVShZvMNbuXZMA");
    }

}

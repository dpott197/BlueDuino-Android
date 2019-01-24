package com.aprilbrother.blueduino.globalvariables;

import com.aprilbrother.blueduino.bean.PinInfo;

import java.util.ArrayList;

public class GlobalVariables {

    //pin count
    public static int pinSize;

    //pins Info
    public static ArrayList<PinInfo> pinInfos = new ArrayList<PinInfo>();

    public static boolean isQueryPinAll = false;

}

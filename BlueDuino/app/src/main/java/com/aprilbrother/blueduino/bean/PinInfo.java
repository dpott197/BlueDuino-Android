package com.aprilbrother.blueduino.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PinInfo implements Parcelable {

    //引脚编号
    private int pin;
    //引脚可以执行什么操作
    private int capability;
    //当前模式
    private int mode;
    //当前模式下的值
    private int value;

    public PinInfo() {

    }

    public PinInfo(Parcel in) {
        pin = in.readInt();
        capability = in.readInt();
        mode = in.readInt();
        value = in.readInt();
    }

    public static final Creator<PinInfo> CREATOR = new Creator<PinInfo>() {
        @Override
        public PinInfo createFromParcel(Parcel in) {
            return new PinInfo(in);
        }

        @Override
        public PinInfo[] newArray(int size) {
            return new PinInfo[size];
        }
    };

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pin);
        dest.writeInt(capability);
        dest.writeInt(mode);
        dest.writeInt(value);
    }

}

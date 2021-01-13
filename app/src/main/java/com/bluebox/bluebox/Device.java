package com.bluebox.bluebox;

import java.io.Serializable;

public class Device implements Serializable {

    public String name;
    public String macAddress;
    public boolean isConnected;

    public Device(String name, String macAddress, boolean isConnected) {
        this.name = name;
        this.macAddress = macAddress;
        this.isConnected = isConnected;
    }
}
package info.androidhive.viewpager2;

public class Device {

    public String name;
    public String macAddress;
    public boolean isConnected;

    public Device(String name, String macAddress, boolean isConnected) {
        this.name = name;
        this.macAddress = macAddress;
        this.isConnected = isConnected;
    }
}
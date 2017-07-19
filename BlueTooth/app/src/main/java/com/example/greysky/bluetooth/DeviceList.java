package com.example.greysky.bluetooth;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by GreySky on 2017/6/17.
 */
public class DeviceList {
    public class Device{
        String name;
        String address;//设备的唯一标识符

        public Device(String n,String a){
            name = n;
            address = a;
        }
    }

    List<Device> data;

    public DeviceList(){
        data = new ArrayList<Device>();
    }
    public Device getDevice(int position){
        if (position>=data.size())
            return null;
        return data.get(position);
    }

    public int size(){
        return data.size();
    }

    public void removeAll(){
        data.clear();
    }

    public Device getDevice(String address){
        for (Device d:data){
            if (d.address.equals(address))
                return d;
        }
        return null;
    }

    public void delDevice(String address){
        for (Device d:data){
            if (d.address.equals(address))
                data.remove(d);
        }
    }

    public void delDevice(int position){
        if (position>=data.size())
            return;
        data.remove(position);
    }

    public void addDevice(String name,String address){
        delDevice(address);
        data.add(new Device(name,address));
    }
}

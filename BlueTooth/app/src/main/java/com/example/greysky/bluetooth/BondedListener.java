package com.example.greysky.bluetooth;

import android.view.View;
import android.widget.AdapterView;

import java.util.Map;

/**
 * Created by GreySky on 2017/6/16.
 */
public class BondedListener implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DeviceList.Device device = MainActivity.instance.bluetooth.bonded.getDevice(i);
        String address = device.address;//得到该设备的地址
        try{
            //停止搜索
            MainActivity.instance.bluetooth.stopSearch();
            MainActivity.instance.bluetooth.nowBonding = address;
            MainActivity.instance.bluetooth.chooseFile();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

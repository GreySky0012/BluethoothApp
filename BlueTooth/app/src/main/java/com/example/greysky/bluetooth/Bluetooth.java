package com.example.greysky.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

/**
 * Created by GreySky on 2017/6/17.
 */
public class Bluetooth {
    public MainActivity context;

    public boolean enable = false;
    public boolean bonding = false;
    public String nowBonding;

    public BluetoothAdapter adapter;//蓝牙适配器
;
    public DeviceList bonded;
    public DeviceList bondNone;

    //服务端的变量
    public Handler serverHandler;
    public Server server;

    public Handler clientHandler;
    public Client client;

    public final UUID uuid = UUID.fromString("abcd1234-ab12-ab12-ab12-abcdef123456");//随便定义一个UUID

    public Bluetooth(MainActivity Context){
        context = Context;
        init();
    }

    public void init(){
        //创建蓝牙适配器
        try{
            adapter = BluetoothAdapter.getDefaultAdapter();//得到蓝牙适配器，如果为null则表示设备不支持蓝牙

            if(adapter == null){
                throw new Exception("设备不支持蓝牙");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //启动蓝牙
        if (!adapter.isEnabled()){
            context.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),0);//调用系统对话框启动蓝牙
        }else {
            enable = true;
        }

        bonded = new DeviceList();
        bondNone = new DeviceList();

        serverHandler = new Handler() {
            public void handleMessage(Message msg) {
                Toast.makeText(context,String.valueOf(msg.obj),Toast.LENGTH_SHORT).show();
            }
        };

        clientHandler = new Handler(){
            public void handleMessage(Message msg){
                Toast.makeText(context,String.valueOf(msg.obj),Toast.LENGTH_SHORT).show();
            }
        };

        server = new Server(this);
        server.start();
    }

    public void setBondedList(){
        //得到已配对的蓝牙设备
        Set<BluetoothDevice> pairDevice = adapter.getBondedDevices();

        //根据配对情况更改ListView显示
        if(pairDevice.size() > 0){
            for (BluetoothDevice device:pairDevice){
                addBonded(device.getName(),device.getAddress());
            }
        }
    }

    public void search(){
        if (!checkEnable())
            return;
        stopSearch();
        adapter.startDiscovery();
        bondNone.removeAll();
        context.search.setText("正在搜索");
    }

    private boolean checkEnable(){
        if (!enable) {
            context.showMessage("蓝牙设备未开启,请开启后重试", true);
            return false;
        }
        return true;
    }

    public void stopSearch(){
        if(adapter.isDiscovering()){
            adapter.cancelDiscovery();
        }
        context.search.setText("开始搜索");
    }

    public void addBonded(String name,String address){
        bonded.addDevice(name,address);
        ((DevicesAdapter)context.device_bonded.getAdapter()).notifyDataSetChanged();
    }

    public void addBondNone(String name,String address){
        bondNone.addDevice(name,address);
        ((DevicesAdapter)context.device_bond_none.getAdapter()).notifyDataSetChanged();
    }

    public void delBonded(String address){
        bonded.delDevice(address);
        ((DevicesAdapter)context.device_bonded.getAdapter()).notifyDataSetChanged();
    }

    public void delBondNone(String address){
        bondNone.delDevice(address);
        ((DevicesAdapter)context.device_bond_none.getAdapter()).notifyDataSetChanged();
    }

    public void bondFinish(boolean success){
        if (success){
            addBonded((String)bondNone.getDevice(nowBonding).name,nowBonding);
            delBondNone(nowBonding);
            nowBonding = null;
        }
        bonding = false;
    }

    //配对
    public void bondWith(String id){
        stopSearch();
        BluetoothDevice d = adapter.getRemoteDevice(id);
        try{
            if (d.getBondState() == BluetoothDevice.BOND_NONE){
                Method createBond = BluetoothDevice.class.getMethod("createBond");
                createBond.invoke(d);
                nowBonding = id;
                bonding = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseFile(){
        FileChooser fc = new FileChooser(context);
    }

    public void send(String filePath){
        client = new Client(this,nowBonding,filePath);
        client.start();
    }
}

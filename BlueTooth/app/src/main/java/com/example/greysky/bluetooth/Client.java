package com.example.greysky.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by GreySky on 2017/6/17.
 */
public class Client extends Thread{
    public BluetoothDevice device;
    public BluetoothSocket socket;
    private OutputStream os;
    private final UUID uuid;
    private Handler handler;


    public Bluetooth bluetooth;
    public String address;
    public String filePath;

    public Client(Bluetooth b,String a,String f){
        bluetooth = b;
        address = a;
        filePath = f;
        uuid = b.uuid;
        handler = b.clientHandler;
    }

    @Override
    public void run() {

        try {
            device = bluetooth.adapter.getRemoteDevice(address);
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            os = socket.getOutputStream();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (os != null){
                String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
                Message msg = new Message();
                msg.obj = new String("开始发送文件"+fileName);
                handler.sendMessage(msg);
                os.write(fileName.getBytes());
                os.flush();

                File file = new File(filePath);
                byte[] buffer = new byte[(int)file.length()];
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(buffer,0,buffer.length);

                os.write(buffer,0,buffer.length);
                os.flush();
                os.close();

                msg = new Message();
                msg.obj = new String("成功发送"+buffer.length+"字节");
                handler.sendMessage(msg);
                bis.close();
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

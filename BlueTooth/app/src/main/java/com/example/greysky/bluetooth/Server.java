package com.example.greysky.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by GreySky on 2017/6/16.
 */
public class Server extends Thread {
    private Bluetooth bluetooth;

    private Handler handler;
    private String receivePath = Environment.getExternalStorageDirectory()+"/bluetooth/";
    private String fileName = null;

    private final UUID uuid;
    private final String name = "Bluetooth_Socket";
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private InputStream is;//输入流
    public Server(Bluetooth b) {
        this.bluetooth = b;
        uuid = bluetooth.uuid;
        handler = bluetooth.serverHandler;
        try {
            serverSocket = bluetooth.adapter.listenUsingRfcommWithServiceRecord(name, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true){
            try {
                socket = serverSocket.accept();
                if (!socket.isConnected()){
                    socket.connect();
                }
                is = socket.getInputStream();
                while(true) {
                    if (fileName == null){
                        byte[] buffer =new byte[1024];
                        int count = is.read(buffer);
                        fileName = new String(buffer,0,count,"utf-8");
                    }else {
                        Message msg = new Message();
                        msg.obj = new String("开始接收文件"+receivePath+fileName);
                        handler.sendMessage(msg);
                        File f = new File(receivePath);
                        if (!f.exists()){
                            f.mkdir();
                        }
                        f = new File(receivePath+fileName);
                        if (!f.exists()){
                            f.createNewFile();
                        }

                        StreamTool.streamSaveAsFile(is,f);

                        Log.e("111","success");
                        msg = new Message();
                        msg.obj = new String("接收文件成功"+receivePath+fileName);
                        handler.sendMessage(msg);
                        fileName = null;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}

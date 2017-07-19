package com.example.greysky.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    public Bluetooth bluetooth;

    public ListView device_bond_none;
    public ListView device_bonded;
    public Button search;

    public BroadcastReceiver broadcast;//广播

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        bluetooth = new Bluetooth(this);

        //取得所有控件的引用
        device_bonded = (ListView)findViewById(R.id.device_old);
        device_bond_none = (ListView)findViewById(R.id.device_new);
        search = (Button)findViewById(R.id.search);

        //初始化两个ListView
        initListView();

        //设置广播接收器
        broadcast = new MyBroadcast(this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.search();
            }
        });
    }

    //初始化ListView
    public void initListView(){
        device_bonded.setAdapter(new DevicesAdapter(this,bluetooth.bonded));
        device_bond_none.setAdapter(new DevicesAdapter(this,bluetooth.bondNone));

        device_bonded.setOnItemClickListener(new BondedListener());
        device_bond_none.setOnItemClickListener(new BondNoneListener());

        bluetooth.setBondedList();
    }

    public void showMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void showMessage(String message,boolean isLong){
        Toast.makeText(this,message,isLong?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                String path = data.getData().getPath();
                bluetooth.send(path);
            }else if (resultCode == Activity.RESULT_CANCELED){
                bluetooth.nowBonding = null;
            }
        }else if (requestCode == 0){
            if (resultCode == Activity.RESULT_OK){
                bluetooth.enable = true;
                bluetooth.setBondedList();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package com.example.greysky.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

/**
 * Created by GreySky on 2017/6/16.
 */
public class MyBroadcast extends BroadcastReceiver {

    private MainActivity context;

    public MyBroadcast(MainActivity context){
        this.context = context;
        init();
    }

    private void init(){
        //设置广播信息过滤
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);//每搜索到一个设备就会发送一个该广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//全部搜索完之后发送该广播
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//配对状态更改时发送该广播
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙开关时的广播
        filter.setPriority(Integer.MAX_VALUE);//设置优先级
        context.registerReceiver(this,filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //判断广播动作
        if (BluetoothDevice.ACTION_FOUND == action){//搜索到了一个设备
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                this.context.bluetooth.addBondNone(device.getName(),device.getAddress());
            }
            //搜索完成
        }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            MainActivity.instance.search.setText("开始搜索");
            this.context.showMessage("搜索完成");
            //配对状态改变
        }else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
            //只判断了配对成功
            if (((BluetoothDevice)(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))).getBondState() == BluetoothDevice.BOND_BONDED){
                this.context.bluetooth.bondFinish(true);
                this.context.showMessage("配对成功");
            }else if(((BluetoothDevice)(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))).getBondState() == BluetoothDevice.BOND_NONE){
                this.context.bluetooth.bondFinish(false);
                this.context.showMessage("配对失败");
            }
            //蓝牙开关状态改变
        }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1);
            if (state == BluetoothAdapter.STATE_TURNING_OFF||state == BluetoothAdapter.STATE_OFF){
                this.context.bluetooth.enable = false;
            }else {
                this.context.bluetooth.enable = true;
            }
        }
    }
}

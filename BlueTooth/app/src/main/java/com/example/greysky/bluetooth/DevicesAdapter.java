package com.example.greysky.bluetooth;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by GreySky on 2017/6/16.
 */
public class DevicesAdapter extends BaseAdapter{

    private DeviceList devices;
    private LayoutInflater layoutInflater;
    private Context context;
    public DevicesAdapter(Context context, DeviceList data){
        this.context = context;
        this.devices = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public final class Components{
        public TextView name;
        public TextView id;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int i) {
        return devices.getDevice(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Components components = null;
        if (view == null){
            components = new Components();
            //获得组件
            view = layoutInflater.inflate(R.layout.list_item,null);
            components.id = (TextView)view.findViewById(R.id.id);
            components.name = (TextView)view.findViewById(R.id.name);
            view.setTag(components);
        }else {
            components = (Components)view.getTag();
        }

        //绑定数据
        components.name.setText((String)devices.getDevice(i).name);
        components.id.setText((String)devices.getDevice(i).address);
        return view;
    }
}

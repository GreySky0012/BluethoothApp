package com.example.greysky.bluetooth;

import android.content.Intent;
import android.widget.Toast;

/**
 * Created by GreySky on 2017/6/17.
 */
public class FileChooser {
    MainActivity context;
    public FileChooser(MainActivity c){
        context = c;
        chooseFile();
    }

    public void chooseFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try{
            context.startActivityForResult(Intent.createChooser(intent,"文件选择"),1);
        }catch (android.content.ActivityNotFoundException ex){
            context.showMessage("请安装文件管理器");
        }
    }
}

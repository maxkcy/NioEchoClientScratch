package com.max.javaniochatclient;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class JavaNioClientMain {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String [] args){
        NioClient nioClient = new NioClient();
        nioClient.init();
    }
}

package com.max.javaniochatclient;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class NioClient extends Thread{
    public final String host = "localhost";
    public final int port = 7033;
    public static final int TIMEOUT = 1000;
    public static int BUFSIZE = 1024;
    ClientHandler clientHandler = new ClientHandler();
    Selector selector;
    boolean run = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void init() {
        SocketChannel socketChannel;
        {
            try {
                selector = Selector.open();
                socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
                socketChannel.configureBlocking(false);
                int OPS = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
                socketChannel.register(selector, OPS, ByteBuffer.allocate(BUFSIZE));
                while (!socketChannel.finishConnect()){
                    System.out.print(".");
                }
                System.out.println("Connected to Server: " + socketChannel.getRemoteAddress());
                int waiting = 0;
                while(run){
                    if(selector.select(TIMEOUT) == 0 ){
                        System.out.print(".");
                        waiting++;
                        if(waiting >= 1000){
                            System.out.println("\n");
                            waiting = 0;
                        }
                        continue;
                    }
                    Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
                    while(keyIter.hasNext()){
                        SelectionKey key = keyIter.next();
                        selector.selectedKeys().remove(key);
                        if(key.isAcceptable()){
                            clientHandler.handleAccept(key);
                        }

                        if(key.isReadable()){
                            clientHandler.handleRead(key);
                        }

                        if(key.isValid() && key.isWritable()){
                            clientHandler.handleWrite(key);
                        } else {
                            key.interestOps(0);
                            key.channel().close();
                            key.cancel();
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

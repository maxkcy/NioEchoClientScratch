package com.max.javaniochatclient;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientHandler implements Handler{
    Scanner scanner = new Scanner(System.in);


    @Override
    public void handleAccept(SelectionKey key) throws IOException {
        //not needed for client to create a socket channel since it is the socketchannel
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.clear(); // added this l8r
        long bytesRead = socketChannel.read(buf);
        if (bytesRead == -1) {
            socketChannel.close();
            System.out.println("Connection to a client closed because nothing was read.");
        } else if (bytesRead > 0) {
            
            //String message = new String(buf.array(), "UTF-8");
//            String message = buf.toString();
//            while(buf.hasRemaining()){
                socketChannel.read(buf);
//            } //eh is this really needed cus buf is being put until it is filled
            String message = StandardCharsets.UTF_8.decode(buf).toString();
            System.out.println("Message from server: " + message);
            /*while (buf.hasRemaining()) {
                System.out.print((char) buf.get());
            }*/
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buf = (ByteBuffer) key.attachment();
//        buf.limit(buf.capacity());
        buf.flip(); //where do i put this???
        System.out.println("Enter the message to be sent: ");
        String message = scanner.nextLine();
        if (message.getBytes().length > buf.capacity()){
           buf = ByteBuffer.allocate(message.getBytes().length);
        }
        buf.clear();
        for (int i = 0; i < message.getBytes().length; i++) {
            buf.put(message.getBytes()[i]);
        }
        //        buf.put(message.getBytes(), 0, message.length()); //eh this here?

        SocketChannel socketChannel = (SocketChannel) key.channel();
        buf.flip();
        if (!buf.hasRemaining()) {
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }else{
            buf.compact(); //this good enough?
        }
        socketChannel.write(buf);
        System.out.println("Message I know I sent to server: " + message);
    }
}

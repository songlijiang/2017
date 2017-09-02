package com.slj.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * Created by slj on 17/3/21.
 */
public class AIOServer {


    public static void main(String[] args) {
        String local ="127.0.0.1";

        SocketAddress address = new InetSocketAddress(local,8080);
        try {
            AsynchronousServerSocketChannel asynchronousServerSocketChannel =AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(address);
            Reader reader = new Reader(asynchronousServerSocketChannel);

            asynchronousServerSocketChannel.accept(new Context() ,reader);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("start to sleep");
            Thread.sleep(1000*60*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}

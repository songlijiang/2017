package com.slj.crawler.panda;

import com.slj.crawler.douyu.MessageHandler;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by slj on 17/9/3.
 */
public class TestSource {

    public static void main(String[] args) throws Exception{
        Socket socket = new Socket("58.220.72.140",8080);

        MessageHandler messageHandler = new MessageHandler(socket);
        messageHandler.send("hello");
        System.out.println(read(socket.getInputStream()));
    }




    private static String read(InputStream inputStream)throws Exception{
        byte[] bytes = new byte[4028];
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        while ((inputStream.read(bytes)) != -1) {
            byteArray.write(bytes);
        }

        return new String(Arrays.copyOfRange(byteArray.toByteArray(), 0, bytes.length));
    }
}

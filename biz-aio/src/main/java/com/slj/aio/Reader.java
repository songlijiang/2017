package com.slj.aio;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by slj on 17/3/21.
 */
@AllArgsConstructor
public class Reader implements CompletionHandler<AsynchronousSocketChannel,Context>{


    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    @Override
    public void completed(AsynchronousSocketChannel result, Context attachment) {
        final ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            result.read(buffer);
            buffer.flip();
            System.out.println("request = "+new String(buffer.array()));
            result.write(buffer);
            buffer.flip();
            result.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
    }

    @Override
    public void failed(Throwable exc, Context attachment) {
        System.out.println(exc);

    }
}

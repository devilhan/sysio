package com.devilhan.io.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author Hanyanjiao
 * @date 2020/10/21
 */
public class ThreadHandlerChannel extends Thread {

    public ThreadHandlerChannel(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(512);
        sc.read(buffer);
        buffer.flip();
        System.out.println(Charset.defaultCharset().decode(buffer));
        sc.register(key.selector(),SelectionKey.OP_READ);
    }
}

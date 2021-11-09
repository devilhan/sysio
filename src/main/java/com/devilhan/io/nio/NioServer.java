package com.devilhan.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;


/**
 * @author Hanyanjiao
 * @date 2020/10/20
 */

public class NioServer {
    public static void main(String[] args) throws IOException {
        //创建服务端channel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("127.0.0.1",8888));
        ssc.configureBlocking(false);

        System.out.println("server started ,listening on :"+ssc.getLocalAddress());
        //轮询器，不同的操作系统有不同的实现类
        Selector selector = Selector.open();
        //将服务端channel注册到轮询器上，并告诉轮询器，自己感兴趣的事件是ACCEPT事件
        ssc.register(selector,SelectionKey.OP_ACCEPT);

        while (true) {
            //调用轮询器的select()方法，让轮询器从操作系统上获取所有的事件
            selector.select(); //阻塞

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                it.remove();
                handle(key);
            }
        }
    }

    private static void handle(SelectionKey key) {
        if (key.isAcceptable()){   //新连接接入
            try {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);

                //向selector 注册该channel -> 有新的客户端接入后，将客户端对应的channel感兴趣事件是可读
                sc.register(key.selector(),SelectionKey.OP_READ);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (key.isReadable()){  //可读事件
            try {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(512);
                sc.read(buffer);
                buffer.flip();
                System.out.println(Charset.defaultCharset().decode(buffer));
                sc.register(key.selector(),SelectionKey.OP_READ);
                /*ByteBuffer bufferToWrite = ByteBuffer.wrap("Hello Client".getBytes());
                sc.write(bufferToWrite);*/
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

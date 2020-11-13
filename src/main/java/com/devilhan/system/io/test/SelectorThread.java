package com.devilhan.system.io.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Hanyanjiao
 * @date 2020/11/13
 */
public class SelectorThread implements Runnable {
    //每一个线程对应一个selector
    //多线程的情况下，该主机，该程序的并发客户端被分配到多个selector上
    //注意：每个客户端，只绑定到其中一个selector //不存在交互问题

    Selector selector = null;
    SelectThreadGroup stg;

    LinkedBlockingQueue<Channel> lbq = new LinkedBlockingQueue<>();
    public SelectorThread(SelectThreadGroup stg) {
        try {
            this.stg = stg;
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //loop
        while (true){
            try {
                //1,select()
                System.out.println(Thread.currentThread().getName()+": before select..."+selector.keys().size());
                int nums = selector.select();  //阻塞
//                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName()+": after select..."+selector.keys().size());
                //2,处理selectKeys
                if (nums>0){
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()){  //线程处理
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()){  //接受客户端的过程
                            acceptHandler(key);
                        }else if (key.isReadable()){
                            readHander(key);
                        }else if (key.isWritable()){

                        }
                    }
                }
                //3,处理一些tasks
                if (!lbq.isEmpty()){
                    Channel channel = lbq.take();
                    if (channel instanceof ServerSocketChannel){
                        ServerSocketChannel server = (ServerSocketChannel) channel;
                        server.register(selector,SelectionKey.OP_ACCEPT);
                        System.out.println(Thread.currentThread().getName()+": register listen");
                    }else if (channel instanceof SocketChannel){
                        SocketChannel client = (SocketChannel) channel;
                        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                        client.register(selector,SelectionKey.OP_READ,buffer);
                        System.out.println(Thread.currentThread().getName()+": register client:"+client.getRemoteAddress());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void readHander(SelectionKey key) {
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        SocketChannel client = (SocketChannel) key.channel();
        buffer.clear();
        while (true){
            try {
                int num = client.read(buffer);
                if (num>0){
                    buffer.flip(); //将读到的内容翻转，然后直接写出
                    while (buffer.hasRemaining()){
                        client.write(buffer);
                    }
                    buffer.clear();
                }else if (num == 0){
                    break;
                }else if (num<0){
                    //客户端断开了
                    System.out.println("client: "+client.getRemoteAddress()+" closed...");
                    key.cancel();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void acceptHandler(SelectionKey key) {
        System.out.println("acceptHandler....");
        ServerSocketChannel server = (ServerSocketChannel) key.channel();

        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);

            //choose a selector and register!
            stg.nextSelector2(client);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

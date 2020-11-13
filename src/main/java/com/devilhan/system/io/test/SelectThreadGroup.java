package com.devilhan.system.io.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hanyanjiao
 * @date 2020/11/13
 */
public class SelectThreadGroup {

    SelectorThread[] sts;
    ServerSocketChannel server = null;
    AtomicInteger xid = new AtomicInteger(0);

    public SelectThreadGroup(int num) {
        //线程数
        sts = new SelectorThread[num];
        for (int i=0;i<num;i++){
            sts[i] = new SelectorThread(this);
            new Thread(sts[i]).start();
        }
    }

    public void bind(int port) {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));

            //注册到哪一个selector
            nextSelector2(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void nextSelector2(Channel channel) {
        if (channel instanceof  ServerSocketChannel){
            try {
                if (channel instanceof ServerSocketChannel){
                    sts[0].lbq.put(channel);
                    sts[0].selector.wakeup();
                }else {
                    SelectorThread st = next2();

                    //通过队列传递数据 消息
                    st.lbq.add(channel);
                    //通过打断阻塞，让对应的线程去自己在打断后完成自己的注册
                    st.selector.wakeup();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void nextSelector(Channel channel) {
        SelectorThread st = next();

        //通过队列传递数据 消息
        st.lbq.add(channel);
        //通过打断阻塞，让对应的线程去自己在打断后完成自己的注册
        st.selector.wakeup();


        //channel 有可能是server 有可能是client
//        ServerSocketChannel s = (ServerSocketChannel) channel;
        /*try {
            s.register(st.selector,SelectionKey.OP_ACCEPT);
            st.selector.wakeup();//功能是让selector 的 select 不阻塞，立刻返回

        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }*/

    }

    //无论serverSocket or Socket都复用这个方法
    public SelectorThread next() {

        int index = xid.incrementAndGet() % sts.length;
        return sts[index ];
    }
    public SelectorThread next2() {

        int index = xid.incrementAndGet() % (sts.length -1);
        return sts[index +1];
    }
}

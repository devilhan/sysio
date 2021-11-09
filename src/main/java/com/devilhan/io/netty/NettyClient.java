package com.devilhan.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Hanyanjiao
 * @date 2020/10/28
 */
public class NettyClient {

    public static void main(String[] args){
        new NettyClient().clientStart();
    }

    private void clientStart(){
        EventLoopGroup workers = new NioEventLoopGroup();  //相当于线程池
        Bootstrap client = new Bootstrap(); //辅助启动的类
        client.group(workers).channel(NioSocketChannel.class)
//                .handler(new ClientChannelInitializer());
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        try {
            ChannelFuture future = client.connect("localhost",8888).sync();
           /* ChannelFuture f = b.connect("localhost", 8888);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()){
                        System.out.println("not connect!");
                    }else{
                        System.out.println("connected!");
                    }
                }
            });
            f.sync();
            System.out.println("--------");
*/
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workers.shutdownGracefully();
        }
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter{
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        executorService.scheduleAtFixedRate(()->{
            String msg = Thread.currentThread().getName()+ "Hello World";
            ctx.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
        },0,10, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //读需要手动释放
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(buf.toString());
    }
}
class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        System.out.println(channel);
    }
}


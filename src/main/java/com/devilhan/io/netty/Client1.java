package com.devilhan.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @author Hanyanjiao
 * @date 2020/10/28
 */
public class Client1 {
    public static void main(String[] args){
        new Client1().clientStart();
    }

    private void clientStart(){
        EventLoopGroup workers = new NioEventLoopGroup();  //相当于线程池
        Bootstrap b = new Bootstrap(); //辅助启动的类
        b.group(workers).channel(NioSocketChannel.class)
//                .handler(new ClientChannelInitializer());
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("channel initialized");
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        try {
            System.out.println("start to connect ...");
            ChannelFuture f = b.connect("localhost",8888).sync();
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
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workers.shutdownGracefully();
        }
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel is activated.");

        final ChannelFuture f = ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Netty Server".getBytes()));
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("msg send!");
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            //读需要手动释放
            buf = (ByteBuf) msg;
            System.out.println(buf.toString());

        }finally {
            if (buf != null){
                ReferenceCountUtil.release(msg);
            }
        }
    }
}
class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        System.out.println(channel);
    }
}


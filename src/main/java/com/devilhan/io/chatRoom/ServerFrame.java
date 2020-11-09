package com.devilhan.io.chatRoom;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Hanyanjiao
 * @date 2020/10/28
 */
public class ServerFrame extends Frame {

    Channel channel;
    TextArea ta = new TextArea();
    TextField tf = new TextField();

    public ServerFrame(){
        this.setSize(1600,600);
        this.setLocation(300,30);
        this.add(ta);
        this.add(tf);
        this.setTitle("小妖聊天室");

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setVisible(true);

    }

    public static void main(String[] args){
        ServerFrame serverFrame = new ServerFrame();
        serverFrame.connect(8888);
    }

    public void send(String msg){
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
        channel.writeAndFlush(buf);
    }

    public void connect(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new ServerHandler());
                    }
                });

        try {
            ChannelFuture f = b.bind(port);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess() ){
                        System.out.println("not connected");
                    }else{
                        System.out.println("connected");
                        channel = f.channel();
                        System.out.println("server is "+channel.toString());
                    }
                }
            });
            f.sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    class ServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
            ByteBuf buf = null;
            try{
                buf = (ByteBuf) msg;
                /*byte[] bytes = new byte[buf.readableBytes()];
                String receive = new String(buf.getBytes(buf.readerIndex(),bytes).array());*/
                String receive = buf.toString(CharsetUtil.UTF_8);
                System.out.println("server receive msg is "+receive);
                ta.setText(ta.getText()+"\n"+ receive);
//                ctx.writeAndFlush(Unpooled.copiedBuffer("机器人为您指导！".getBytes()));
//                ctx.close();
            }finally {
                if (buf != null){
                    ReferenceCountUtil.release(msg);
                }
            }
        }
    }

    public TextArea getTa() {
        return ta;
    }

    public void setTa(TextArea ta) {
        this.ta = ta;
    }

    public TextField getTf() {
        return tf;
    }

    public void setTf(TextField tf) {
        this.tf = tf;
    }
}



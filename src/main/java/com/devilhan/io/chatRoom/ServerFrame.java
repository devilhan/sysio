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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Hanyanjiao
 * @date 2020/10/28
 */
public class ServerFrame extends Frame {

    private static final String TITLE_NAME = "Robot";

    Channel channel;
    TextArea ta = new TextArea();
    TextField tf = new TextField();

    public ServerFrame() {
        this.setSize(600, 400);
        this.setLocation(300, 30);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);
        this.setTitle(TITLE_NAME);
        ta.setEditable(false);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tf.getText() != null && tf.getText() != "") {
                    //将字符串发送到服务器
                    String msg = TITLE_NAME + ":\n    " + tf.getText();
                    if (ta.getText() != null && ta.getText() != "") {
                        ta.setText(ta.getText() + "\n" + msg);
                    } else {
                        ta.setText(msg);
                    }
                    send(msg);
                    tf.setText("");
                }
            }
        });
        this.setVisible(true);

    }

    public static void main(String[] args) {
        ServerFrame serverFrame = new ServerFrame();
        serverFrame.connect(8888);
    }

    public void send(String msg) {
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
        System.out.println("already send");
        this.channel.writeAndFlush(buf);
    }

    public void connect(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
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
                    if (!channelFuture.isSuccess()) {
                        System.out.println("not connected");
                    } else {
                        channel = f.channel();
                    }
                }
            });
            f.sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    class ServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
            ByteBuf buf = null;
            try {
                buf = (ByteBuf) msg;
                /*byte[] bytes = new byte[buf.readableBytes()];
                String receive = new String(buf.getBytes(buf.readerIndex(),bytes).array());*/
                String receive = buf.toString(CharsetUtil.UTF_8);
                System.out.println("receive is " + receive);
                ta.setText(ta.getText() + "\n" + receive);
//                ctx.writeAndFlush(Unpooled.copiedBuffer( ": \n    机器人为您指导！".getBytes()));
//                ctx.close();
            } finally {
                if (buf != null) {
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



package com.devilhan.io.chatRoom;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Hanyanjiao
 * @date 2020/10/28
 */
public class ClientFrame extends Frame {
    private static final String TITLE_NAME = "devilHan";
    TextArea ta = new TextArea();
    TextField tf = new TextField();

    private Channel channel = null;

    public ClientFrame() {
        this.setSize(600, 400);
        this.setLocation(300, 30);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);
        ta.setEditable(false);
        this.setTitle(TITLE_NAME);

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
        ClientFrame clientFrame = new ClientFrame();
        clientFrame.connect();
    }

    public void send(String msg) {
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
        channel.writeAndFlush(buf);
    }

    public void connect() {
        EventLoopGroup client = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(client).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new ClientHandler());
                    }
                });
        try {
            ChannelFuture f = b.connect("localhost", 8888);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        System.out.println("not connected");
                    } else {
                        channel = f.channel();  //初始化channel
                    }
                }
            });
            f.sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            client.shutdownGracefully();
        }

    }

    class ClientChannelInitializer extends ChannelInitializer {
        @Override
        protected void initChannel(Channel channel) {
            channel.pipeline().addLast(new ClientFrame.ClientHandler());
        }
    }

    class ClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
            ByteBuf buf = null;
            try {
                buf = (ByteBuf) msg;
               /* byte[] bytes = new byte[buf.readableBytes()];
                String receive = new String(buf.getBytes(buf.readerIndex(),bytes).array());*/
                String receive = buf.toString(CharsetUtil.UTF_8);
                System.out.println("connn");
                System.out.println("receive is " + receive);
                ta.setText(ta.getText() + "\n" + receive);
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


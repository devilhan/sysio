package com.devilhan.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Hanyanjiao
 * @date 2020/10/20
 */
public class Server2 {

    public static void main(String[] args) throws IOException {
        //在服务器端建立一个连接
        ServerSocket ss = new ServerSocket();

        ss.bind(new InetSocketAddress("127.0.0.1", 8888));
        while (true) {
            Socket s = ss.accept();
            new Thread(() -> {
                try {
                    handle(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    static void handle(Socket s) throws IOException {
        InputStream inputStream = s.getInputStream();
        byte[] bytes = new byte[1024];
        try {
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                System.out.println(Thread.currentThread().getName() + "receive : " + new String(bytes, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

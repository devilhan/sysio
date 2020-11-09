package com.devilhan.io.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Hanyanjiao
 * @date 2020/10/20
 */
public class Server {

    public static void main(String[] args) throws IOException {
        //在服务器端建立一个连接
        ServerSocket ss = new ServerSocket();

        ss.bind(new InetSocketAddress("127.0.0.1",8888));
        while (true){
            Socket s = ss.accept();
            new Thread(()->{
                handle(s);
            }).start();
        }
    }

    static void handle(Socket s){
        byte[] bytes = new byte[1024];
        try {
            int len = s.getInputStream().read(bytes);
            System.out.println("receive : "+new String(bytes,0,len));

            s.getOutputStream().write(bytes,0,len);
            s.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}

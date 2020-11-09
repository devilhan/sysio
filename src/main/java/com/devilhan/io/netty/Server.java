package com.devilhan.io.netty;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Hanyanjiao
 * @date 2020/10/27
 * BIO 的server
 */
public class Server {

   public static void main(String[] args) throws IOException {
       ServerSocket socket = new ServerSocket();
       socket.bind(new InetSocketAddress(8888));
       Socket s = socket.accept();

       System.out.println("a client connect");
       byte[] bytes = new byte[1024];
       int len = s.getInputStream().read(bytes);
       System.out.println("receive is "+new String(bytes,0,len));

   }
}

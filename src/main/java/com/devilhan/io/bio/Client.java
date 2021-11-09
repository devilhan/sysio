package com.devilhan.io.bio;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Hanyanjiao
 * @date 2020/10/20
 */
public class Client {

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws IOException {
        Socket s = new Socket("127.0.0.1",8888);
        executorService.scheduleAtFixedRate(()->{
            try {
                s.getOutputStream().write("Hello Server2".getBytes());
                s.getOutputStream().flush();
                System.out.println("send success!");
            }catch (IOException e){
                e.printStackTrace();
            }
        },0,10, TimeUnit.SECONDS);
    }
}

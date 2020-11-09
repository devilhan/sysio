package com.devilhan.basic;

/**
 * @author Hanyanjiao
 * @date 2020/11/5
 */
public class CacheLinePadding {

    public static volatile long[] arr = new long[2];

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            for (long i=0;i<10000_0000L;i++){
                arr[0] = i;
            }
        });

        Thread t2 = new Thread(()->{
            for (long i=0;i<10000_0000L;i++){
                arr[1] = i;
            }
        });
        final long start = System.nanoTime();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println((System.nanoTime()-start)/100_0000);
    }
}

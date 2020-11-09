package com.devilhan.basic;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;

import java.util.concurrent.ExecutionException;


/**
 * @author Hanyanjiao
 * @date 2020/11/9
 */
public class HelloFiber2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int size = 10000;
        Fiber<Void>[] fibers = new Fiber[size];
        for (int i=0;i<fibers.length;i++){
            fibers[i] = new Fiber<>(new SuspendableRunnable(){

                @Override
                public void run() throws SuspendExecution, InterruptedException {
                    calc();
                }
            });
        }

        for (int i=0;i<fibers.length;i++){
            fibers[i].start();
        }

        for (int i=0;i<fibers.length;i++){
            fibers[i].join();
        }

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    static void calc() {
        int result = 0;
        for (int m = 0; m < 10000; m++) {
            for (int i = 0; i < 200; i++){
                result += i;
            }
        }
    }
}

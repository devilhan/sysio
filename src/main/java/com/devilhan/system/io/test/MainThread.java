package com.devilhan.system.io.test;

/**
 * @author Hanyanjiao
 * @date 2020/11/13
 */
public class MainThread {
    //这里不做关于IO和业务的事情
    public static void main(String[] args){

        //创建IO Thread
        SelectThreadGroup group = new SelectThreadGroup(3);


        //监听的server注册到某一个selector
        group.bind(9999);
    }
}

BIO->NIO

select/poll   synchronous I/O multiplexing

    select：使用的是定长数组，限制在1024

    > man 2 select
      int select(int nfds, fd_set *readfds, fd_set *writefds,
                  fd_set *exceptfds, struct timeval *timeout);
                  
    poll：使用的是链表实现，没有长度限制。
    
    缺点：每次传输重复的fd
    
epoll  I/O event notification facility

    epoll：在最开始把对应的文件描述符传输给内核
        内核自己开辟一个空间将此文件描述符存下
        当状态改变时，内核将此文件描述符复制到就续表中

    > man 7 epoll
        epoll_create(2) creates an epoll instance and returns a file descriptor referring to that instance.

        Interest in particular file descriptors is then registered via epoll_ctl(2).

        epoll_wait(2) waits for I/O events, blocking the calling thread if no events are currently available.



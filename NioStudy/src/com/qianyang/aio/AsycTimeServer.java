package com.qianyang.aio;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-06
 */
public class AsycTimeServer implements Runnable{

    private Integer port;
    CountDownLatch latch;
    AsynchronousServerSocketChannel asyncServerSocketChannel;

    AsycTimeServer(Integer port){
        this.port = port;

        try  {
            asyncServerSocketChannel = AsynchronousServerSocketChannel.open();
            asyncServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("async time server start on port:" + port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);

        doAccept();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept(){
        this.asyncServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }

    public static void main(String[] args){
        new Thread(new AsycTimeServer(8080)).start();
    }
}

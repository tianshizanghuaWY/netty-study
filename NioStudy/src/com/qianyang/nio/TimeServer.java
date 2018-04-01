package com.qianyang.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-01
 */
public class TimeServer implements Runnable{
    private Selector selector;
    private ServerSocketChannel server;
    private volatile boolean stop;

    public TimeServer(int port){

        try{
            selector = Selector.open();
            server = ServerSocketChannel.open();

            //1. 设置异步非阻塞模式
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port), 1024);
            server.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("time server is started in port:" + port);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop){
            try{
                selector.select(1000);

                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> keys = keySet.iterator();

                SelectionKey selectionKey = null;
                while (keys.hasNext()){
                   selectionKey = keys.next();
                   keys.remove();

                   handleInput(selectionKey);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(selector != null){
            try {
                //多路复用器关闭后， 注册在其上的channel，pipe等资源都会自动关闭，不需要额外释放资源
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException{
        try{

            if(key.isValid()){
                if(key.isAcceptable()){
                    //accept new connection
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    //相当于完成TCP三次握手, TCP 物理链路正式建立
                    SocketChannel channel = ssc.accept();
                    channel.configureBlocking(false);

                    //add the new connection to the selector
                    channel.register(selector, SelectionKey.OP_READ);
                }

                if(key.isReadable()){
                    //read the data
                    SocketChannel channel = (SocketChannel) key.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    //read 是非阻塞的，要对读到的内容进行判断
                    // size > 0： 读到数据，对字节进行编码
                    // size = 0 : 正常情景，没有读到内容
                    // size < 0：链路已经关闭，需要关闭 SocketChannel， 释放资源
                    int readBytes = channel.read(buffer);
                    if(readBytes > 0){
                        //flip 将缓冲区当前的limit设置成position，同时将position设置为0，为后续读取做准备
                        buffer.flip();

                        //remaining = limit - position
                        byte[] bytes = new byte[buffer.remaining()];
                        //获取可读字节，并放入数组中
                        buffer.get(bytes);

                        String body = new String(bytes, "UTF-8");
                        System.out.println("time server receive order: " + body);

                        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                                new Date().toString() : "BAD ORDER";

                        byte[] answerBytes = currentTime.getBytes();
                        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                        writeBuffer.put(answerBytes);
                        writeBuffer.flip();// !!!!!!!!!!
                        channel.write(writeBuffer);
                    }else if(readBytes < 0){
                        //对链路关闭
                        key.cancel();
                        channel.close();
                    }
                }
            }

        }finally {
            if(key != null){
                key.cancel();
                if(key.channel() != null){
                    try {
                        key.channel().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args){
        new Thread(new TimeServer(8080), "TimeServer-NIO-001").start();
    }
}

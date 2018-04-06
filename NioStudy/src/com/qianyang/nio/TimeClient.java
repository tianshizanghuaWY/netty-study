package com.qianyang.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-06
 */
public class TimeClient implements Runnable{
    private String serverHost;
    private Integer port;
    private Selector selector;
    private SocketChannel channel;
    private volatile boolean stop;

    TimeClient(String serverHost, Integer port){
        this.serverHost = serverHost;
        this.port = port;
        try {
            selector = Selector.open();
            channel = SocketChannel.open();

            channel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!stop){
            try{
                selector.select(1000);

                Set<SelectionKey> alreadyKeys = selector.selectedKeys();
                Iterator<SelectionKey> its = alreadyKeys.iterator();
                SelectionKey key = null;

                while (its.hasNext()){
                    key = its.next();
                    its.remove();

                    handleInput(key);
                }
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        if(selector != null){
            try {
                //多路复用器关闭后,所有注册在上面的 Channel 和 Pipe 等资源都会被自动释放资源
                selector.close();
                System.out.println("selector closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws Exception{
        if(key.isValid()){
            if(key.isConnectable()){
                if(channel.finishConnect()){
                    System.out.println("channel connect finish, channel:" + channel);
                    channel.register(selector, SelectionKey.OP_READ);
                    doWrite();
                }else {
                    System.out.println("连接失败");
                    System.exit(1);
                }
            }

            if(key.isReadable()){
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int size = channel.read(readBuffer);

                if(size > 0){
                    System.out.println("channel is on read channel:" + channel);
                    readBuffer.flip();
                    byte[] answer = new byte[readBuffer.remaining()];
                    readBuffer.get(answer);

                    String body = new String(answer, "UTF-8");
                    System.out.println("now is:" + body);

                    //TODO  测试下不关闭的情形
                    //this.stop = true;
                }else if(size < 0){
                    //TODO  测试下不关闭的情形
                    System.out.println("close channel:" + channel);
                    key.cancel();
                    channel.close();
                }
            }
        }

    }

    /**
     * SocketChannel.connect() 返回false，表示执行异步连接，说明客户端已经发送sync包，服务端没有返回ack包,物理链路还没有建立
     * @throws IOException
     */
    private void doConnect() throws IOException{
        if(channel.connect(new InetSocketAddress(serverHost, port))){
            System.out.println("channel.connect success, register OP-READ");
            channel.register(selector, SelectionKey.OP_READ);
            doWrite();
        } else {
            System.out.println("channel.connect, send sync, register OP-CONNECT");
            channel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    //改为每10秒发送一次请求
    private void doWrite() throws IOException {
        new Thread(){
            @Override
            public void run(){
                while (!stop){
                    byte[] order = "QUERY TIME ORDER".getBytes();
                    ByteBuffer data = ByteBuffer.allocate(1024);
                    data.put(order);

                    //在对buffer的读写模式转换时, 千万不要忘记这一步
                    data.flip();

                    try {
                        channel.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(!data.hasRemaining()){
                        System.out.println("send order to server successful");
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public static void main(String[] args){
        new Thread(new TimeClient("127.0.0.1", 8080)).start();
    }
}

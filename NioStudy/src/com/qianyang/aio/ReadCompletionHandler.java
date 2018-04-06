package com.qianyang.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-06
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer>{

    private AsynchronousSocketChannel asyncSocketChannel;
    public ReadCompletionHandler(AsynchronousSocketChannel channel){
        this.asyncSocketChannel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("read from one connection finished.");

        attachment.flip();

        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);

        try{
            String order = new String(body,"UTF-8");

            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(order) ?
                    new Date().toString() : "BAD ORDER";

            byte[] response = currentTime.getBytes();

            doWrite(response);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //让这个 channel 继续处理后续的读操作
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            this.asyncSocketChannel.read(buffer, buffer, this);

            System.out.println("----------------> 继续读~");
        }

    }

    private void doWrite(byte[] data){
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();

        //this.asyncSocketChannel.write(buffer);

        this.asyncSocketChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                //如果没发送完，继续发送
                if(attachment.hasRemaining()){
                    asyncSocketChannel.write(attachment, attachment, this);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

                try {
                    asyncSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("send answer success!");
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

        try {
            this.asyncSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

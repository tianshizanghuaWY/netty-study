package com.qianyang.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-06
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsycTimeServer>{
    @Override
    public void completed(AsynchronousSocketChannel channel, AsycTimeServer attachment) {
        System.out.println("accept one connection - finished.");

        //让TimeServer 继续接收其它连接 -> 接受完一个请求，继续去接收另一个 -> 形成一个循环
        //假如这个 completed() 有着比较复杂的逻辑，其它连接会岂不是要阻塞了吗
        attachment.asyncServerSocketChannel.accept(attachment, this);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        channel.read(buffer, buffer, new ReadCompletionHandler(channel));
    }

    @Override
    public void failed(Throwable exc, AsycTimeServer attachment) {

        exc.printStackTrace();

        attachment.latch.countDown();
    }
}

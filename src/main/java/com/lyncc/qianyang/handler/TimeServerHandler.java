package com.lyncc.qianyang.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * <br>
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter{

    /*
     * 当建立一个链接,并准备传输数据时调用 channelActive()
     *
     *
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        //super.channelActive(ctx);
        ByteBuf timeBuf = ctx.alloc().buffer(4); //用于发送一个32位的 int
        timeBuf.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        //timeBuf.writeByte(4);

        final ChannelFuture future = ctx.writeAndFlush(timeBuf);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                assert future == channelFuture;

                ctx.close();//注意， 这个也是返回一个 ChannelFuture
            }
        });

        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    /*
     * ByteBuf has two pointers; one for read operations and the other for write operations.
     * The writer index increases when you write something to a ByteBuf while the reader index does not change.
     * The reader index and the writer index represents where the message starts and ends respectively.
     *
     * 在 NIO 操作中，在发送message之前，需要先调用 flip() 方法
     * 但是 ByteBuf 不需要, 因为他有2个指针， 分别为读操作和写操作使用,
     * 当写入数据到 ByteBuf 时，writer index 会增长，而 reader index 不会变
     * reader index 表示一个message 的开始
     * writer index 表示一个message 的结束
     *
     * ctx.writeAndFlush() 会返回一个 ChannelFuture
     * A ChannelFuture represents an I/O operation which has not yet occurred.
     * It means, any requested operation might not have been performed yet because all operations are asynchronous in Netty.
     * ChannelFuture 表示一个 I/O 操作还没有执行(因为在 Netty 里所有操作都是异步的)
     * 因此，需要为 Future 添加listener，监听 I/O 操作啥时候执行完，可以在执行完成后，做一个操作， 比如 关闭连接
     */
}

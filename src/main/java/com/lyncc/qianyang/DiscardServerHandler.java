package com.lyncc.qianyang;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, java.lang.Object msg) throws Exception {
        //super.channelRead(ctx, msg);

        //直接忽略数据
        ((ByteBuf) msg).release(); // (3)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, java.lang.Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);

        //Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();

        //处理事件时抛出的异常在这里处理, 通常在连接关闭前, 发送一个 response message with error code
    }
}

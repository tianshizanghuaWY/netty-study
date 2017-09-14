package com.lyncc.qianyang.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * ByteToMessageDecoder extends ChannelInboundHandlerAdapter
 * ByteToMessageDecoder 是 ChannelInboundHandlerAdapter 的一个实现，设计它用来处理碎片化的传输数据
 * 它内部维持一个用于累计的 buf, 当有数据来时，就会调用 decode 方法， 在decode 方法里，可以决定是否添加数据到out
 */
public class TimeDecoder extends ByteToMessageDecoder{

    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf, List<Object> out) throws Exception {

        if(byteBuf.readableBytes() < 4)
            return;

        out.add(byteBuf.readBytes(4));
    }
    /*
     * If decode() adds an object to out, it means the decoder decoded a message successfully.
     * ByteToMessageDecoder will discard the read part of the cumulative buffer.
     * Please remember that you don't need to decode multiple messages.
     * ByteToMessageDecoder will keep calling the decode() method until it adds nothing to out
     */
}

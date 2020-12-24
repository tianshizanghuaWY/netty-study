package com.lyncc.qianyang.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

/**
 * <br>
 *
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //super.channelRead(ctx, msg);

        ByteBuf in = (ByteBuf) msg;

        try{
            long time = (in.readUnsignedInt() - 2208988800L) * 1000;
            System.out.println(new Date(time));

            ctx.close();
        }finally {
            ReferenceCountUtil.release(in);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);

        cause.printStackTrace();
        ctx.close();
    }
}

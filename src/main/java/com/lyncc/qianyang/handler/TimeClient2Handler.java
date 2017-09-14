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
public class TimeClient2Handler extends ChannelInboundHandlerAdapter{
    private ByteBuf buf;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //super.channelRead(ctx, msg);

        ByteBuf byteReceive = (ByteBuf)msg;

        try{
            //先将读取的数据写入到 Buffer中
            buf.writeBytes(byteReceive);

            //当buf中的可读字节数大于 4 时才去处理
            if(buf.readableBytes() >= 4){
                long timeMills = (buf.readUnsignedInt() - 2208988800L) * 1000;
                System.out.println(new Date(timeMills));

                //关闭连接 通常的做法是每次读取数据都关闭吗?
                ctx.close();
            }
        }finally {

            //别忘了释放 ByteBuf
            ReferenceCountUtil.release(byteReceive);

            //Please keep in mind that it is the handler's responsibility to release any reference-counted object passed to the handler
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }


    /*
     * 每个ChannelHandler 都有2个声明周期监听方法
     * 可以在方法里做一些初始化的操作(不要太耗时的操作)
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //super.handlerAdded(ctx);
        buf = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //super.handlerRemoved(ctx);
        buf.release();
        buf = null;
    }
}

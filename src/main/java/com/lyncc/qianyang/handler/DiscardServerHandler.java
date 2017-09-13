package com.lyncc.qianyang.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, java.lang.Object msg) throws Exception {
        //super.channelRead(ctx, msg);

        //justPrint(ctx, msg);

        retuenTheMsg(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, java.lang.Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);

        //Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();

        //处理事件时抛出的异常在这里处理, 通常在连接关闭前, 发送一个 response message with error code
    }

    //只是打印接受过来的数据
    private void justPrint(ChannelHandlerContext ctx, java.lang.Object msg){
        //System.out.println("just discard the msg.");
        //直接忽略数据
        //((ByteBuf) msg).release();

        //打印输出
        ByteBuf in = (ByteBuf) msg;
        /*try{
            while (in.isReadable()){
                System.out.print((char)in.readByte());
                System.out.flush();
            }
        }finally {
            ReferenceCountUtil.release(in);
        }*/

        //优雅的方式
        System.out.println(in.toString(CharsetUtil.UTF_8));
    }

    //返回接收的数据
    private void retuenTheMsg(ChannelHandlerContext ctx, java.lang.Object msg){
        //不需要手动 release msg， 因为当 ctx 写回 msg 时， 会自动 release 它
        ctx.write(msg);

        ctx.flush(); //触发输出
    }
}

package com.lyncc.qianyang.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 在IDEA测试： client 控制端直接输入命令, 触发 channelRead0() -> 触发 channelReadComplete()
 *
 */
public class TelnetServerHandler extends SimpleChannelInboundHandler<String>{

    //【坑】COMMIT_CHAR, 刚开始测试的时候一直没有加上这个换行符， client 端一直没有接受到数据
    private static final String COMMIT_CHAR = "\r\n";

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("channelRead0 - TelnetServerHandler:" + s);

        String response;
        boolean close = false;

        if("bye".equals(s)){
            response = "see you later...." + COMMIT_CHAR;
            close = true;
        }else{
            response = "Did you say " + s + "?" + COMMIT_CHAR;
        }

        // We do not need to write a ChannelBuffer here.
        // We know the encoder inserted at TelnetPipelineFactory will do the conversion.
        ChannelFuture writeFuture = channelHandlerContext.write(response);

        // Close the connection after sending response
        // if the client has sent 'bye'.
        if(close){
            writeFuture.addListener(ChannelFutureListener.CLOSE);

            /*
             * ChannelFutureListener.CLOSE 的 operationComplete() 实现:
             * future.channel().close();
             */

        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive - TelnetServerHandler.");

        //super.channelActive(ctx);
        ctx.write("welcome!");
        ctx.write("my name is wangyuan!" + COMMIT_CHAR);
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //super.channelReadComplete(ctx);

        System.out.println("channelReadComplete - TelnetServerHandler.");
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive - TelnetServerHandler.");
        //super.channelInactive(ctx);

        ctx.close();
    }

    /*
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded - TelnetServerHandler.");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded - TelnetServerHandler.");
        super.handlerRemoved(ctx);
    }*/
}

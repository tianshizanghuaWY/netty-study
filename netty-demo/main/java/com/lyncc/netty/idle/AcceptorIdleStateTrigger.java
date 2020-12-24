package com.lyncc.netty.idle;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


/*
 * 一个处理连接空闲状态事件的  ChannelHandler
 * 在心跳机制中，客户端是 write ， 服务端是 read
 * 一旦 读空闲 事件触发， 则抛出异常
 */
@ChannelHandler.Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                System.out.println("server - READER_IDLE: " + TimeUtil.getCurrentFormatDateStr());
                throw new Exception("idle exception");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}

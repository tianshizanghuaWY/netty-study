package com.lyncc.customProtocal;

import com.lyncc.customProtocal.meesage.Message;
import com.lyncc.customProtocal.meesage.resolver.MessageResolverFactory;
import com.lyncc.customProtocal.meesage.resolver.PingMessageResolver;
import com.lyncc.customProtocal.meesage.resolver.PongMessageResolver;
import com.lyncc.customProtocal.meesage.resolver.RequestMessageResolver;
import com.lyncc.customProtocal.meesage.resolver.Resolver;
import com.lyncc.customProtocal.meesage.resolver.ResponseMessageResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务端消息处理器
 *
 * @author 千阳
 * @date 2020-12-24
 */
public class ServerMessageHandler extends SimpleChannelInboundHandler<Message> {
    /** 个消息处理器工**/
    private MessageResolverFactory resolverFactory = MessageResolverFactory.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message)
            throws Exception {
        // 接受消息并处理
        Resolver resolver = resolverFactory.getMessageResolver(message);
        Message result = resolver.resolve(message);

        // 将响应数据写入到处理器中
        ctx.writeAndFlush(result);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        resolverFactory.registerResolver(new RequestMessageResolver());	// 注册request消息处理器
        resolverFactory.registerResolver(new ResponseMessageResolver());// 注册response消息处理器
        resolverFactory.registerResolver(new PingMessageResolver());	// 注册ping消息处理器
        resolverFactory.registerResolver(new PongMessageResolver());	// 注册pong消息处理器
    }
}

package com.lyncc.qianyang.factorial;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;


public class FactorialServerInitializer extends ChannelInitializer<SocketChannel>{
    private SslContext sslContext;
    public FactorialServerInitializer(SslContext sslContext){
        this.sslContext = sslContext;
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();
        if(sslContext != null){
            pipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        }

        // Enable stream compression (you can remove these two if unnecessary)
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        // Add the number codec first,
        pipeline.addLast(new BigIntegerDecoder());
        pipeline.addLast(new NumberEncoder());

        // and then business logic.
        // Please note we create a handler for every new channel
        // because it has stateful properties.
        pipeline.addLast(new FactorialServerHandler());
    }
}

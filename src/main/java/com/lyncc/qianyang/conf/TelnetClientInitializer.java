package com.lyncc.qianyang.conf;

import com.lyncc.qianyang.client.TelnetClient;
import com.lyncc.qianyang.handler.TelnetClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;



/**
 * <br>
 */
public class TelnetClientInitializer extends ChannelInitializer<SocketChannel>{

    private static final StringDecoder STRING_DECODER = new StringDecoder();
    private static final StringEncoder STRING_ENCODER = new StringEncoder();
    private static final TelnetClientHandler CLIENT_HANDLER = new TelnetClientHandler();
    private final SslContext sslCtx;

    public TelnetClientInitializer(SslContext sslContext){
        this.sslCtx = sslContext;
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(socketChannel.alloc(), TelnetClient.HOST, TelnetClient.PORT));
        }

        // Add the text line codec combination first,
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(STRING_DECODER);
        pipeline.addLast(STRING_ENCODER);

        // and then business logic.
        pipeline.addLast(CLIENT_HANDLER);
    }
}

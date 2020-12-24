package com.lyncc.qianyang.client;

import com.lyncc.qianyang.conf.TelnetClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TelnetClient {
    public static final boolean SSL = System.getProperty("ssl") != null;
    public static final String HOST = System.getProperty("host", "127.0.0.1");
    public static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8992" : "8023"));

    public static void main(String[] args) throws Exception{

        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            Bootstrap client = new Bootstrap();
            client.group(workerGroup).channel(NioSocketChannel.class)
                    .handler(new TelnetClientInitializer(sslCtx));

            //start the connection attempt
            Channel channel = client.connect(HOST,PORT).sync().channel();

            //read commands from the stdin
            ChannelFuture lastWriteFuture = null;
            BufferedReader inputBuff = new BufferedReader(new InputStreamReader(System.in));
            for(;;){
                String line = inputBuff.readLine();
                if(line == null){
                    break;
                }

                //sent command to server
                lastWriteFuture = channel.writeAndFlush(line + "\r\n");

                if("bye".equalsIgnoreCase(line)){
                    channel.closeFuture().sync();
                    break;
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if(lastWriteFuture != null){
                lastWriteFuture.sync();
            }

        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}

package com.lyncc.qianyang;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 http://netty.io/wiki/user-guide-for-4.x.html#wiki-h2-4
 */
public class DiscardServer {
    private int port;
    public DiscardServer(int port){
        this.port = port;
    }

    /*
     * bossGroup 用来接收连接请求
     * workerGroup: 一旦bossGroup将接受请求注册到 workerGroup中， workerGroup 就会去处理连接传递的数据
     * 线程创建的个数，以及线程如何映射到创建的 Channel 取决于EventLoopGroup的实现
     * ChannelInitializer 用来配置一个Channel, 比如通常用来配置 ChannelPipeline of Channel(比如添加一些 handler 来实现你的web应用)
     * 当多个handlers 被添加，你的应用会越来越复杂，最终这个 ChannelInitializer 匿名类会被提取到顶级类中
     */
    public void run() throws Exception{
        //accepts an incoming connection
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        //handles the traffic of the accepted connection once the boss accepts the connection and registers the accepted connection to the worker.
        //How many Threads are used and how they are mapped to the created Channels depends on the EventLoopGroup
        //implementation and may be even configurable via a constructor.
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();


            //The handler specified here will always be evaluated by a newly accepted Channel.
            //The ChannelInitializer is a special handler that is purposed to help a user configure a new Channel.
            //It is most likely that you want to configure the ChannelPipeline of the new Channel
            //by adding some handlers such as DiscardServerHandler to implement your network application.
            //As the application gets complicated, it is likely that you will add more handlers to the pipeline
            //and extract this anonymous class into a top level class eventually
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception{
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }

                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //bind and start to accept incoming connections
            ChannelFuture future = bootstrap.bind(port).sync();

            //wait until the server socket is closed
            future.channel().closeFuture().sync();

        }finally {

        }
    }

    public static void main(String[] args) throws Exception{
        new DiscardServer(8080).run();
    }

    //1. NioEventLoopGroup is a multithreaded event loop that handles I/O operation
    //2. ServerBootstrap is a helper class that sets up a server
    //3. NioServerSocketChannel is used to instantiate a new Channel to accept incoming connections.
}

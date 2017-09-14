package com.lyncc.qianyang.client;

import com.lyncc.qianyang.handler.TimeClient2Handler;
import com.lyncc.qianyang.handler.TimeClientHandler;
import com.lyncc.qianyang.handler.TimeDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * <br>
 *
 */
public class TimeClient {
    public static void main(String[] args) throws Exception{
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            Bootstrap client = new Bootstrap();
            client.group(workerGroup);
            client.channel(NioSocketChannel.class);
            client.option(ChannelOption.SO_KEEPALIVE, true);

            //第一种方式，TimeClientHandler 没有对接受的数据长度进行判断，可能会出错，因为传输过来的数据时碎片化的
            /*client.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new TimeClientHandler());
                }
            });*/

            //TimeClient2Handler 对数据的长度进行了判断
            /*client.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new TimeClient2Handler());
                }
            });*/

            //第三种方式，使用 TimeClientHandler 进行实质上的业务处理(转义输出时间)
            //并且新增一个ChannelHandler 给 ChannelPipeLine,这个ChannelHandler 专门处理数据碎片化传输的问题
            client.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new TimeDecoder(),new TimeClientHandler());
                }
            });

            // Start the client.
            ChannelFuture f = client.connect("localhost", 8080).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }

    /*
     * ChannelPipeLine 可以接受多个 ChannelHandler, 这样可以把复杂的任务进行查分, 每个ChannelHandler处理各自的 issue
     */
}

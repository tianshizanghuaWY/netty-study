package com.qianyang.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-16
 */
public class TimeClient {
    TimeClient(){}

    public void connect(){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            Bootstrap client = new Bootstrap();
            client.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeClientHandler());
                        }
                    });

            //发起异步连接操作
            ChannelFuture f = client.connect("127.0.0.1", 8080).sync();

            //等待客户端链路关闭
            f.channel().closeFuture().sync();

            System.out.println("time client connection close.");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }

    }
    public static void main(String[] args){
        new TimeClient().connect();
    }

    class TimeClientHandler extends ChannelInboundHandlerAdapter{
        private final ByteBuf order;
        public TimeClientHandler(){
            String orderStr = "QUERY TIME ORDER";

            order = Unpooled.buffer(orderStr.getBytes().length);
            order.writeBytes(orderStr.getBytes());
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("time client channel active");

            ctx.channel().writeAndFlush(order);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("time client channel inactive");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("time client channel read");

            ByteBuf byteBuf = (ByteBuf) msg;

            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);

            System.out.println("answer: " + new String(bytes, "UTF-8"));
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}

package com.qianyang.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Date;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-16
 */
public class TimeServer {
    private Integer port;
    TimeServer(Integer port){
        this.port = port;
    }

    public void bind(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeServerHandler());
                        }
                    });

            ChannelFuture f = server.bind(port).sync();

            System.out.println("Time server started");

            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();

            System.out.println("Time server shutdown");
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
    public static void main(String[] args){
        new TimeServer(8080).bind();
    }

    class TimeServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
            System.out.println("TimerServer channelRead ...");
            ByteBuf buf = (ByteBuf) msg;
            byte[] order = new byte[buf.readableBytes()];
            buf.readBytes(order);

            String orderStr = new String(order, "UTF-8");
            System.out.println("Time server receive order:" + orderStr);
            String answer = "QUERY TIME ORDER".equals(orderStr) ? new Date().toString() : "BAD ORDER";

            ByteBuf resp = Unpooled.copiedBuffer(answer.getBytes());

            //ctx.channel().write(resp);

            //这一句等同 write + flush
            ctx.channel().writeAndFlush(resp);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Time server chanel read complete");
            //ctx.channel().flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}

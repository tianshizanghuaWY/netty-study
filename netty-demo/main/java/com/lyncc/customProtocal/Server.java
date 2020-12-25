package com.lyncc.customProtocal;

import com.lyncc.customProtocal.meesage.MessageDecoder;
import com.lyncc.customProtocal.meesage.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * https://my.oschina.net/zhangxufeng/blog/3043768
 *
 * 名称	        字段	    字节数	        描述
 * ------------------------------------------------
 * 魔数	      magicNumber	4	      一个固定的数字，一般用于指定当前字节序列是当前类型的协议，比如Java生成的class文件起始就使用0xCAFEBABE作为其标识符，对于本服务，这里将其定义为0x1314
 * 主版本号	  mainVersion	1	      当前服务器版本代码的主版本号
 * 次版本号	  subVersion	1	      当前服务器版本的次版本号
 * 修订版本号  modifyVersion	1	      当前服务器版本的修订版本号
 * 会话id	  sessionId	    8	      当前请求的会话id，用于将请求和响应串联到一起
 * 消息类型	  messageType	1	      请求：1，表示当前是一个请求消息；响应：2，表示当前是一个响应消息；Ping：3，表示当前是一个Ping消息；Pong：4，表示当前是一个Pong消息；Empty：5，表示当前是一个空消息，该消息不会写入数据管道中；
 * 附加数据	  attachments   不定	  附加消息是字符串类型的键值对来表示的，这里首先使用2个字节记录键值对的个数，然后对于每个键和值，都首先使用4个字节记录其长度，然后是具体的数据，其形式如：键值对个数+键长度+键数据+值长度+值数据...
 * 消息体长度  length	    4字节	  记录了消息体的长度
 * 消息体      body	        不定	  消息体，服务之间交互所发送或接收的数据，其长度有前面的length指定
 */
public class Server {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加用于处理粘包和拆包问题的处理器
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));

                            // 添加自定义协议消息的编码和解码处理器
                            pipeline.addLast(new MessageEncoder());
                            pipeline.addLast(new MessageDecoder());

                            // 添加具体的消息处理器
                            pipeline.addLast(new ServerMessageHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(8585).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

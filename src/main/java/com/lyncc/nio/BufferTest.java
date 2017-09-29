package com.lyncc.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 */
public class BufferTest {

    public static void main(String[] args){
        byteBufferTest();
    }

    public static void byteBufferTest(){
        byte[] byte1 = "he     ".getBytes();
        byte[] byte2 = "llo     ".getBytes();

        ByteBuffer buffer1 = ByteBuffer.allocate(10);
        ByteBuffer buffer2 = ByteBuffer.allocate(10);
        buffer1.put(byte1);
        buffer2.put(byte2);

        ByteBuffer buffer3 = ByteBuffer.allocate(20);
        ByteBuffer[] b4 = {buffer1, buffer2};
        buffer3.put(buffer1.array());
        buffer3.put(buffer2.array());

        //读取内容
        System.out.println(new String(buffer3.array()));
        System.out.println("buffer1 addr:" + buffer1.array());
        System.out.println("buffer2 addr:" + buffer2.array());
        System.out.println("buffer3  addr:" + buffer3.array());

        ByteBuf nb1 = Unpooled.buffer(10);
        ByteBuf nb2 = Unpooled.buffer(10);
        nb1.writeBytes(byte1);
        nb2.writeBytes(byte2);
        System.out.println("nb1 - array:addr:" + nb1.array());

        ByteBuf nb3 = Unpooled.wrappedBuffer(nb1, nb2);
        // System.out.println("nb3 - array:addr:" + nb3.array()); //UnsupportedOperationException

        byte[] data = new byte[nb3.readableBytes()];
        for(int i = 0; i < data.length; i++){
            data[i] = nb3.getByte(i);
        }

        System.out.println("data of nb3:" + new String(data));
    }
}

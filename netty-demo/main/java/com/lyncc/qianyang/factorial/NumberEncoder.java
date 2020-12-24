package com.lyncc.qianyang.factorial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;

/**
 * Encodes a {@link Number} into the binary representation prepended with
 * a magic number ('F' or 0x46) and a 32-bit length prefix.  For example, 42
 * will be encoded to { 'F', 0, 0, 0, 1, 42 }.
 * */
public class NumberEncoder extends MessageToByteEncoder<Number>{
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          Number o, ByteBuf byteBuf) throws Exception {
        System.out.println("---------------> encode:" + o);
        BigInteger value;
        if(o instanceof  BigInteger){
            value = (BigInteger)o;
        }else{
            value = new BigInteger(String.valueOf(0));
        }

        byte[] byteData = value.toByteArray();
        int dataLength = byteData.length;

        //write a message
        byteBuf.writeByte('F');      //magic number
        byteBuf.writeInt(dataLength);//data length
        byteBuf.writeBytes(byteData); //data
    }

    public static void main(String[] args){
        BigInteger test = new BigInteger("42");
        byte[] byteData = test.toByteArray();
        int dataLength = byteData.length;

    }
}

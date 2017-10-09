package com.qianyang.charset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/*
 * 我们要将一个GBK编码的文件(gbk.txt)转存到一个UTF编码的文件(utf.txt)中
 *
 * http://www.cnblogs.com/skynet/archive/2011/05/03/2035105.html
 * http://www.jianshu.com/p/94f70818da0d
 */
public class CharEncodeDecode {

    private static Charset gbk = Charset.forName("GBK");
    private static Charset utf = Charset.forName("UTF-8");

    public static void main(String[] args){

        try{

            String filePath = System.getProperty("user.dir") + File.separator
                            + "NioStudy" + File.separator + "src/com/qianyang/charset/gbk.txt";

            String outPutFilePath = System.getProperty("user.dir") + File.separator
                    + "NioStudy" + File.separator + "src/com/qianyang/charset/utf.txt";

            FileInputStream inputStream = new FileInputStream(filePath);
            FileChannel fileChannel = inputStream.getChannel();

            ByteBuffer dataBuffer = ByteBuffer.allocate(1024);
            int readSize = fileChannel.read(dataBuffer);

            dataBuffer.flip(); //非常重要的一步，容易疏忽

            System.out.println("Read result : " + readSize); //长度29

            //System.out.println(new String(dataBuffer.array(), "UTF-8")); //乱码
            //System.out.println(new String(dataBuffer.array(), "GBK")); //正常

            CharsetDecoder gbkDecoder = gbk.newDecoder();
            CharBuffer charBuffer = gbkDecoder.decode(dataBuffer);//charBuffer 是 GBK解码后的中文字符集，并且长度是29

            //System.out.println(charBuffer.array()); //正常打印中文

            CharsetEncoder utfEncoder = utf.newEncoder();
            ByteBuffer outputByte = utfEncoder.encode(charBuffer); //长度39

            FileOutputStream outputStream = new FileOutputStream(outPutFilePath);
            FileChannel writeChannel = outputStream.getChannel();
            int writeSize = writeChannel.write(outputByte); //长度39
            System.out.println("write result : " + writeSize);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

/*
 * 1) byteBuffer.flip()
 * 2) 使用CharsetDecoder 对 ByteBuffer 进行解码, 结果存于 CharBuffer
 * 3) 使用CharsetEncoder 对 CharBuffer 进行编码, 结果存于 ByteBuffer
 * 4) 对于文件的读写都是通过FileChannel 进行的, 数据的载体都是 ByteBuffer
 */

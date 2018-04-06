package com.lyncc.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-06
 */
public class BufferAPITest {
    public static void main(String[] args) throws Exception{
        System.out.println(System.getProperty("user.dir"));

        readAndWrite();
    }

    private static void readAndWrite() throws Exception{
        String filePath = System.getProperty("user.dir") + File.separator
                + "src\\main\\java\\com\\lyncc\\nio\\Buffer.txt";
        FileInputStream fileInputStream = new FileInputStream(filePath);
        FileChannel channel = fileInputStream.getChannel();

        String writeFilePath = System.getProperty("user.dir") + File.separator
                + "src\\main\\java\\com\\lyncc\\nio\\BufferTo.txt";
        FileOutputStream writeStream = new FileOutputStream(writeFilePath);


        try{
            ByteBuffer buffer = ByteBuffer.allocate(10);

            //读：channel -> buffer(往 buffer 里写)
            int readSize = channel.read(buffer);

            while (readSize > -1){

                //转换模式(准备从 buffer 里读)
                buffer.flip();

                byte[] writeBytes = buffer.array();
                writeStream.write(writeBytes);
                writeStream.flush();

                while (buffer.hasRemaining()){
                    //一次读取一"位"
                    char b = (char)buffer.get();
                    System.out.println(b);
                }

                //为下一次往 buffer 里写做准备
                buffer.clear();
                readSize = channel.read(buffer);
            }
        }finally {
            fileInputStream.close();
            writeStream.close();
        }

    }
}

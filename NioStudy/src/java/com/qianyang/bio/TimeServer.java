package com.qianyang.bio;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-01
 */
public class TimeServer {
    public static void main(String[] args) throws Exception{
        ServerSocket server = null;

        try{
            server = new ServerSocket(8080);
            Socket socket = null;
            System.out.println("server is started in port: 8080");
            while (true){
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }
        }finally {

            if(server != null){
                System.out.println("time server closed!");
                server.close();;
                server = null;
            }
        }
    }
}

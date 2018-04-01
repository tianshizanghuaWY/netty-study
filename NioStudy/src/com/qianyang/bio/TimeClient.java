package com.qianyang.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-01
 */
public class TimeClient {
    public static void main(String[] args) throws Exception{

        Socket client = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try{
            client = new Socket("127.0.0.1", 8080);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            out.println("QUERY TIME ORDER");
            System.out.println("send query time order!");
            String answer = in.readLine();

            System.out.println("now is:" + answer);
        }finally {

            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(client != null){
                client.close();
            }
        }
    }
}

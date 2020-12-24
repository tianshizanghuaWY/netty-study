package com.qianyang.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-04-01
 */
public class TimeServerHandler implements Runnable{
    private Socket socket;

    public TimeServerHandler (Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {

        BufferedReader in = null;
        PrintWriter out = null;

        try{
            in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);

            String currentTime = null;
            String body = null;

            while (true){
                body = in.readLine();

                if(body == null){
                    break;
                }

                System.out.println("receive order:" + body);
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER!";

                out.println(currentTime);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(in != null){
                    in.close();
                }
                if(out != null){
                    out.close();
                }
                if(socket != null){
                    socket.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

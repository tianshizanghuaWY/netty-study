package com.lyncc.qianyang.model;

import java.util.Date;

/**
 * <br>
 */
public class UnixTime {
    private long value;

    public UnixTime(){
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }
    public UnixTime(long value){
        this.value = value;
    }

    public long value(){
        return this.value;
    }

    public String toString(){
        return new Date((value() - 2208988800L) * 1000L).toString();
    }
}

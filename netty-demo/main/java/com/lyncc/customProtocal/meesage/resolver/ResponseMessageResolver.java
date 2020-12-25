package com.lyncc.customProtocal.meesage.resolver;

import com.alibaba.fastjson.JSON;
import com.lyncc.customProtocal.meesage.Message;
import com.lyncc.customProtocal.meesage.MessageTypeEnum;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <br>
 *
 * @author 千阳
 * @date 2020-12-24
 */
public class ResponseMessageResolver implements Resolver{
    private static final AtomicInteger counter = new AtomicInteger();

    public boolean support(Message message) {
        return message.getMessageType().equals(MessageTypeEnum.RESPONSE);
    }

    /**
     * 收到一个 response 消息，返回一个 empty 消息
     * @param message
     * @return
     */
    public Message resolve(Message message) {
        int count = counter.getAndIncrement();

        System.out.println("rec response msg:[" + count + "]"
                + JSON.toJSONString(message));

        Message messageRsp = new Message();
        messageRsp.setMessageType(MessageTypeEnum.EMPTY);

        return messageRsp;
    }
}

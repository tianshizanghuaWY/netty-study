package com.lyncc.customProtocal.meesage.resolver;

import com.lyncc.customProtocal.meesage.Message;
import com.lyncc.customProtocal.meesage.MessageTypeEnum;

/**
 * <br>
 *
 * @author 千阳
 * @date 2020-12-24
 */
public class PingMessageResolver implements Resolver{
    public boolean support(Message message) {
        return message.getMessageType().equals(MessageTypeEnum.PING);
    }

    /**
     * 收到一个 ping 消息，返回一个 pong 消息
     * @param message
     * @return
     */
    public Message resolve(Message message) {
        System.out.println("rec ping msg: " + System.currentTimeMillis());

        Message messageRsp = new Message();
        messageRsp.setMessageType(MessageTypeEnum.PONG);

        return messageRsp;
    }
}

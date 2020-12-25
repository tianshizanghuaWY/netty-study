package com.lyncc.customProtocal.meesage;

import com.lyncc.customProtocal.common.Constants;
import com.lyncc.customProtocal.common.SessionIdGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * message to byte
 * @author 千阳
 * @date 2020-12-24
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message,
                          ByteBuf out) throws Exception {
        if(MessageTypeEnum.EMPTY.equals(message.getMessageType())){
            // EMPTY 消息表示该消息不需要响应（即不需要写进管道里）
            return;
        }

        out.writeInt(message.getMagicNumber());
        out.writeByte(message.getMainVersion());
        out.writeByte(message.getSubVersion());
        out.writeByte(message.getModifyVersion());

        if(StringUtils.isEmpty(message.getSessionId())){
            message.setSessionId(SessionIdGenerator.generate());
        }
        out.writeCharSequence(message.getSessionId(), Constants.UTF_8);

        out.writeByte(message.getMessageType().getType());

        //扩展字段处理,扩展内容为 Map<String, String>
        //序列化格式: 扩展字段个数 + key长度 + key + value长度 + value + key长度...
        int length = message.getAttachments().size();
        out.writeShort(length);
        if(length > 0){
            for(String key : message.getAttachments().keySet()){
                out.writeInt(key.length());
                out.writeCharSequence(key, Constants.UTF_8);
                out.writeInt(message.getAttachments().get(key).length());
                out.writeCharSequence(message.getAttachments().get(key), Constants.UTF_8);
            }
        }

        int bodyLength = StringUtils.isEmpty(message.getBody()) ? 0 : message.getBody().length();
        out.writeInt(bodyLength);
        if(bodyLength > 0){
            out.writeCharSequence(message.getBody(), Constants.UTF_8);
        }
    }
}

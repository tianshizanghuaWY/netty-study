package com.lyncc.customProtocal.meesage;

import com.lyncc.customProtocal.common.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <br>
 *
 * @author 千阳
 * @date 2020-12-24
 */
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf in, List<Object> list) throws Exception {

        Message message = new Message();
        message.setMagicNumber(in.readInt());
        message.setMainVersion(in.readByte());
        message.setSubVersion(in.readByte());
        message.setModifyVersion(in.readByte());

        message.setSessionId((String)in.readCharSequence(Constants.SESSION_ID_LENGTH, Constants.UTF_8));

        message.setMessageType(MessageTypeEnum.get(in.readByte()));

        //扩展字段处理
        short extendSize = in.readShort();
        for(int i = 0; i < extendSize; i++){
            int keyLength = in.readInt();
            String key = (String)in.readCharSequence(keyLength, Constants.UTF_8);
            int valLength = in.readInt();
            String val = (String)in.readCharSequence(valLength, Constants.UTF_8);

            message.addAttachment(key, val);
        }

        int bodyLength = in.readInt();
        if(bodyLength > 0){
            message.setBody((String)in.readCharSequence(bodyLength, Constants.UTF_8));
        }

        list.add(message);
    }
}

package com.lyncc.customProtocal.meesage;

import com.lyncc.customProtocal.common.Constants;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息
 */
@Data
public class Message {
    private int magicNumber = Constants.MAGIC_NUMBER;
    private byte mainVersion = Constants.MAIN_VERSION;
    private byte subVersion = Constants.SUB_VERSION;
    private byte modifyVersion = Constants.MODIFY_VERSION;

    private String sessionId;

    private MessageTypeEnum messageType;

    /**自定义协议-扩展字段**/
    private Map<String, String> attachments = new HashMap();
    private String body;

    public Map<String, String> getAttachments() {
        return Collections.unmodifiableMap(attachments);
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments.clear();
        if (null != attachments) {
            this.attachments.putAll(attachments);
        }
    }

    public void addAttachment(String key, String value) {
        attachments.put(key, value);
    }
}

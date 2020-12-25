package com.lyncc.customProtocal.common;

import java.util.UUID;

/**
 * sessionId 生成器
 *
 * @author wangyuan
 * @date 2020-12-24
 */
public class SessionIdGenerator {
    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}

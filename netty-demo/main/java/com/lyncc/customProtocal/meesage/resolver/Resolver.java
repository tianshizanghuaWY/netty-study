package com.lyncc.customProtocal.meesage.resolver;

import com.lyncc.customProtocal.meesage.Message;

/**
 * @author wangyuan
 * @date 2020-12-24
 */
public interface Resolver {
    boolean support(Message message);
    Message resolve(Message message);
}

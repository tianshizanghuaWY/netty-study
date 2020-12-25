package com.lyncc.customProtocal.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author 千阳
 * @date 2020-12-24
 */
public class Constants {
    public static final int MAGIC_NUMBER = 0x1314;
    public static final byte MAIN_VERSION = 1;
    public static final byte SUB_VERSION = 1;
    public static final byte MODIFY_VERSION = 1;
    public static final int SESSION_ID_LENGTH = 8;

    public static final Charset UTF_8 = StandardCharsets.UTF_8;
}

package com.kk.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: Aliyun OSS
 * @author: Spike Wong
 * @Created: 2022/5/15
 */
@Component
public class OssUtil implements InitializingBean {
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;
    @Value("${spring.cloud.alicloud.access-key}")
    private String accessKeyID;
    @Value("${spring.cloud.alicloud.secret-key}")
    private String accesskeySecret;
    @Value("${spring.cloud.alicloud.oss.bucketname}")
    private String bucketName;

    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endpoint;
        ACCESS_KEY_ID = accessKeyID;
        ACCESS_KEY_SECRET = accesskeySecret;
        BUCKET_NAME = bucketName;
    }

}

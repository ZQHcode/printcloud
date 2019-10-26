package com.print.printcloud.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by 郑钦泓
 * 2019-10-26 17:25
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatAccountConfig {


    /**
     * 公众平台id
     */
    private String mpAppId;

    /**
     * 公众平台密钥
     */
    private String mpAppSecret;
}

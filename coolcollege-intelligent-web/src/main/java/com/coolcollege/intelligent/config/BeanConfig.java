package com.coolcollege.intelligent.config;

import org.apache.shenyu.springboot.starter.client.common.config.ShenyuClientCommonBeanConfiguration;
import org.apache.shenyu.springboot.starter.client.sofa.ShenyuSofaClientConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * @author zhangchenbiao
 * @FileName: BeanConfig
 * @Description:
 * @date 2022-07-21 10:41
 */
@Component
@ConditionalOnProperty(name = "shenyu.register.registerType")
@Import({ShenyuSofaClientConfiguration.class, ShenyuClientCommonBeanConfiguration.class})
public class BeanConfig {

}

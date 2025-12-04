package com.coolcollege.intelligent.util;

import com.coolcollege.intelligent.model.region.dto.LimitDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 钉钉限流工具类
 * @Author suzhuhong
 * @Date 2022/8/3 16:03
 * @Version 1.0
 */
@Slf4j
public class DingTalkLimitingUtil{

    private static  final  Integer MINUTE_MAX_LIMITING = 500;


    /**
     * 分钟限流
     * @param limitTime 次数
     * @param startTime 开始时间
     * @return
     */
    public  static LimitDTO minuteLimit(Integer limitTime, Long startTime) {
        //如果超过500 且时间少于1分钟 防止发生钉钉限流
        log.info("maxTime:{}", limitTime);
        if (limitTime > MINUTE_MAX_LIMITING) {
            //计算时间间隔
            Long endTime = System.currentTimeMillis();
            log.info("start:{},endTime:{},time:{}", startTime, endTime, endTime - startTime);
            //1分钟
            if (endTime - startTime < 60000) {
                LocalDateTime time = LocalDateTime.now();
                try {
                    //钉钉当前时间下一秒结束限流 +1更保险
                    Thread.sleep((60 - time.getSecond() + 1) * 1000);
                    log.error("minuteLimit_sleep：{}秒", (60 - time.getSecond() + 1));
                } catch (InterruptedException e) {
                    log.error("minuteLimit_sleep_e:{}",e.getMessage());
                }
            }
            log.info("param_reset");
            //重新计数
            limitTime = 0;
            startTime = System.currentTimeMillis();
            return new LimitDTO(startTime,limitTime);
        }
        return new LimitDTO(startTime,limitTime);
    }

}

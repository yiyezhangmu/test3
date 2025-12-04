package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.sync.vo.EnterpriseOpenMsg;
import com.coolcollege.intelligent.facade.enterprise.init.EnterpriseInitService;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 设备状态刷新
 * @author ：zhangchenbiao
 * @date ：2023/12/21 15:32
 */
@Service
@Slf4j
public class DeviceListener implements MessageListener {

    @Resource
    private DeviceService deviceService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        String text = new String(message.getBody());
        log.info("DeviceStatusUpdateListener messageId:{}, msg:{}", message.getMsgID(), text);
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        switch (RocketMqTagEnum.getByTag(message.getTag())){
            case DEVICE_STATUS_REFRESH:
                log.info("设备状态刷新");
                JSONObject msg = JSON.parseObject(text);
                if(Objects.isNull(msg)){
                    return Action.CommitMessage;
                }
                String enterpriseId = msg.getString("enterpriseId");
                if(StringUtils.isBlank(enterpriseId)){
                    return Action.CommitMessage;
                }
                String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
                DataSourceHelper.changeToSpecificDataSource(dbName);
                deviceService.refreshDeviceStatus(enterpriseId, dbName);
                break;
            default:
                break;
        }
        return Action.CommitMessage;
    }
}

package com.coolcollege.intelligent.service.aliyun;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/19
 */
public interface AliyunVdsMonitorService {

    String addCdrsMonitor(String corpId);

    Object updateCdrsMonitor(String enterpriseId,String corpId, String customerId,String taskId, String picOperateType,List<String> picList);
    Object stopCdrsMonitor(String taskId,String corpId);
}

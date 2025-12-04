package com.coolcollege.intelligent.service.aliyun;

import com.coolcollege.intelligent.common.util.ListPageInfo;
import com.coolcollege.intelligent.model.aliyun.request.WebHookRequest;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunPersonTraceVO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsPersonHistoryVO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsPersonVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/14
 */
public interface AliyunVdsService {

    /**
     * 全部历史
     * @param eid
     * @param storeId
     * @param startTime
     * @param endTime
     * @param pageSize
     * @param pageNum
     * @return
     */
    ListPageInfo<AliyunVdsPersonVO> listPerson(String eid,String storeId,Long startTime,Long endTime,Integer pageSize,Integer pageNum);

    void callBackWebhook(String eid, String customerId, WebHookRequest request);

    /**
     * 到访记录
     * @param eid
     * @param storeId
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<AliyunVdsPersonHistoryVO> listPersonHistory(String eid,String storeId,Long startTime,Long endTime,Integer pageNum,Integer pageSize);


    Integer personHistoryCount(String eid,String storeId,Long startTime,Long endTime);

    /**
     * 人员轨迹
     * @param eid
     * @param customerId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<AliyunPersonTraceVO> listPersonTrace(String eid,String customerId,Integer pageNum,Integer pageSize);

}

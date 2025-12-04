package com.coolcollege.intelligent.service.aliyun;

import com.aliyuncs.vcs.model.v20200515.*;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunEventDTO;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunMetricsDTO;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolcollege.intelligent.model.aliyun.response.*;

import java.util.List;

public interface AliyunService {

    /**
     * 根据设备号查看实时视频
     *
     * @param videoDTO
     * @return
     */
    String getLiveUrl(VideoDTO videoDTO);

    /**
     * 根据设备号和时间查看时间段视频
     *
     * @param videoDTO
     * @return
     */
    String getPastVideoUrl(VideoDTO videoDTO);

    /**
     * 阿里云事件接口
     * @param corpId
     * @param aliyunEventDTO
     * @return
     */
    Object listEventAlgorithm(String corpId, AliyunEventDTO aliyunEventDTO);


    /**
     * 创建vds CoroId
     * @param projectName
     * @return corpId
     */
    String createVdsProject(String projectName);

    /**
     * 绑定vds
     * @param vdsCorpId
     * @param deviceList
     * @return
     */
    List<VdsBindDeviceResponse>  bindDeviceToVds(String eid,String vcsCorpId,String vdsCorpId, List<String>deviceList);

    /**
     * 解绑vds
     * @param vdsCorpId
     * @param deviceList
     * @return
     */
    List<VdsBindDeviceResponse>  unbindDeviceToVds(String vdsCorpId,List<String>deviceList);

    /**
     * 获取全部 corp基本信息
     * PageNumber [1, )
     * PageSize [5, 100]
     * CountTotalNum 是否需要统计总数 默认false; 当PageNumber=1时，会默认为true，其它页码默认不统计
     * Type 项目类型：NATIVE（原生CDRS项目，即从CDRS#CreateProject创建的项目），INHERITED（即从VCS创建的项目）
     * NameLike 项目名称：模糊搜索
     * @param name
     * @param pageNumber
     * @param pageSize
     * @param type
     * @return
     */
    VdsPageResponse<VdsProjectInfo> paginateProject(String name , String type, Boolean countTotalNum, Integer pageNumber , Integer pageSize);

    /**
     * 获取设备在哪些corpId下
     * @param deviceId
     * @return
     */
   List<VdsCorpResponse> listDeviceRelation(String deviceId);

    /**
     * 获取corpId有哪些设备
     * @param corpId
     * @param pageNumber
     * @param pageSize
     * @param countTotalNum
     * @return
     */
    VdsPageResponse<VdsDeviceResponse> paginateDevice(String corpId, Integer pageNumber , Integer pageSize, Boolean countTotalNum);

    /**
     * 获取人员动态列表
     * @param corpId
     * @param startTime
     * @param endTime
     * @param pageNumber
     * @param pageSize
     * @return
     */
    VdsPersonResultResponse<VdsPersonInfo> listPersonResult(String corpId, Long startTime, Long endTime, Integer pageNumber, Integer pageSize);

    String createDataSource(String eid);

    /**
     * 获取vcsCoprId
     * @param eid
     * @return
     */
    String getVcsCorpId(String eid);
}

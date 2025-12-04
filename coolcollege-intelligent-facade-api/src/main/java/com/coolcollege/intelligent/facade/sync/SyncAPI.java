package com.coolcollege.intelligent.facade.sync;

import com.coolcollege.intelligent.facade.dto.*;

public interface SyncAPI {
    /**
     * 百丽ehr，单个用户同步权限
     * @author chenyupeng
     * @date 2021/8/18
     * @param request
     * @return com.coolcollege.intelligent.facade.dto.ResultDTO
     */
    BaseResultDTO syncSingleUser(SyncRequest request);


    /**
     * 百丽ehr
     * @author byd
     * @date 2021/8/23
     * @param request
     * @return com.coolcollege.intelligent.facade.dto.ResultDTO
     */
    BaseResultDTO syncAll(SyncAllRequest request);


    BaseResultDTO syncUserAuth(SyncAllRequest request);



    /**
     * 森宇OA，单个用户同步权限
     * @author wxp
     * @date 2021/9/8
     * @param request
     * @return com.coolcollege.intelligent.facade.dto.ResultDTO
     */
    BaseResultDTO syncSenYuSingleUser(SyncSenYuOARequest request);

    /**
     * 记录开始同步日志
     * @author byd
     * @date 2021/9/10
     * @param request
     * @return com.coolcollege.intelligent.facade.dto.ResultDTO
     */
    BaseResultDTO<Long> beginSyncLog(SyncLogRequest request);

    /**
     * 记录失败同步日志
     * @author byd
     * @date 2021/9/10
     * @param request
     * @return com.coolcollege.intelligent.facade.dto.ResultDTO
     */
    BaseResultDTO failSyncLog(SyncLogRequest request);

    /**
     * 百丽用户处理用户和区域的关系
     * @param request
     * @return
     */
    BaseResultDTO syncBailiUserRegion(SyncRequest request);

    /**
     * 同步美宜佳
     * @param request
     * @return
     */
    BaseResultDTO syncMyjAll(SyncAllRequest request);

    /**
     * 同步美宜佳
     * @param request
     * @return
     */
    BaseResultDTO syncMyjSingleUserAuth(SyncMyjUserRequest request);

    /**
     * 同步鲜丰水果用户区域权限
     * @param request
     * @return
     */
    BaseResultDTO syncXfsgSingleUser(SyncXfsgOARequest request);
}

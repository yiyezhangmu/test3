package com.coolcollege.intelligent.facade.open.api.video;


import com.coolcollege.intelligent.facade.dto.openApi.video.*;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @author byd
 * @date 2023-07-31 10:37
 */
public interface VideoLiveApi {

    /**
     * 创建直播
     * @param videoCreateDTO startUniformLive参数
     * @return
     */
    OpenApiResponseVO<String> startUniformLive(VideoCreateApiDTO videoCreateDTO);

    /**
     * 获取直播信息
     * @param videoApiDTO videoApiDTO
     * @return
     */
    OpenApiResponseVO<VideLiveInfoApiDTO> getUniformLiveInfo(VideoApiDTO videoApiDTO);

    /**
     * 创建直播
     * @param videoCreateDTO startUniformLive参数
     * @return
     */
    OpenApiResponseVO<Boolean> updateUniformLive(VideoCreateApiDTO videoCreateDTO);



    /**
     * 查询直播的观看数据
     * @param videoApiDTO videoApiDTO
     * @return
     */
    OpenApiResponseVO<LiveWatchDetailApiDTO> watchDetails(VideoApiDTO videoApiDTO);

    /**
     * 查询直播观看人员信息
     * @param videoApiDTO videoApiDTO
     * @return
     */
    OpenApiResponseVO<LiveWatchUserListApiDTO> watchUsers(VideoApiDTO videoApiDTO);

    /**
     * 删除直播
     * @param videoApiDTO videoApiDTO
     * @return
     */
    OpenApiResponseVO<Boolean> deleteLives(VideoApiDTO videoApiDTO);

}

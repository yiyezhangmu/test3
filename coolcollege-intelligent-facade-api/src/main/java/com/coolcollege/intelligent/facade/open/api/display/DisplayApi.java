package com.coolcollege.intelligent.facade.open.api.display;

import com.coolcollege.intelligent.facade.dto.openApi.DisplayDTO;
import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayTableDTO;
import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayTaskDTO;
import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayTaskProgressDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.*;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @Author suzhuhong
 * @Date 2022/7/11 15:38
 * @Version 1.0
 */
public interface DisplayApi {

    /**
     * 陈列列表
     * @param displayDTO
     * @return
     */
    OpenApiResponseVO displayList(DisplayDTO displayDTO);


    /**
     * 陈列记录详情
     * @param displayDTO
     * @return
     */
    OpenApiResponseVO displayDetail(DisplayDTO displayDTO);

    /**
     * 查询陈列检查表
     * @param reqDTO 请求DTO对象
     * @return 陈列检查表VO
     */
    OpenApiResponseVO<DisplayUnifyVO<DisplayTableVO>> getDisplayTableList(DisplayTableDTO reqDTO);

    /**
     * 查询陈列SOP文档
     * @param reqDTO 请求DTO对象
     * @return 陈列SOP文档VO
     */
    OpenApiResponseVO<DisplayUnifyVO<DisplaySopVO>> getDisplaySopList(DisplayTableDTO reqDTO);

    /**
     * 查询陈列任务
     * @param reqDTO 请求DTO对象
     * @return 陈列任务VO
     */
    OpenApiResponseVO<DisplayUnifyVO<DisplayTaskVO>> getDisplayTaskList(DisplayTaskDTO reqDTO);
}

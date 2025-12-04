package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.PatrolMetaDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;

import java.util.List;

/**
 * @author byd
 * @date 2023-01-05 14:13
 */
public interface UserPersonInfoService {

    /**
     * 获取选中的userId
     * @param eid
     * @param usePersonInfo
     * @param useRange
     * @return
     */
    String getUserIds(String eid, String usePersonInfo, String useRange, String userId);



    /**
     * 获取选中的userId列表
     * @param eid
     * @param usePersonInfo
     * @param useRange
     * @return
     */
    List<String> getUserIdList(String eid, String usePersonInfo, String useRange, String userId);



    /**
     * 获取选中的user用户名称列表
     * @param eid
     * @param usePersonInfo
     * @param useRange
     * @return
     */
    List<String> getUserNameList(String eid, String usePersonInfo, String useRange, String userId);

    /**
     * 获取选中的userId列表
     * @param eid
     * @param taskProcessDTOList
     * @return
     */
    List<String> getUserIdListByTaskProcess(String eid, List<TaskProcessDTO> taskProcessDTOList);

    List<String> getUserIdListByCommonDTO(String eid, List<StoreWorkCommonDTO> commonDTOList);

    /**
     * 处理检查表人员相关信息
     * @param eid
     * @param table
     * @return
     */
    PatrolMetaDTO dealMetaTableUserInfo(String eid, TbMetaTableDO table, Boolean resultViewUserWithUserRang);

    /**
     * 过滤人员信息
     * @param enterprise
     * @param personInfo
     * @return
     */
    String filterPersonInfo(String enterprise, String personInfo);
}

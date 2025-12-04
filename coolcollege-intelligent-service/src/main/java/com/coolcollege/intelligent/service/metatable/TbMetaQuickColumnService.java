package com.coolcollege.intelligent.service.metatable;

import com.coolcollege.intelligent.common.enums.meta.MetaColumnStatusEnum;
import com.coolcollege.intelligent.model.achievement.request.AchievementExportRequest;
import com.coolcollege.intelligent.model.ai.vo.AIModelVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.request.TbMetaQuickColumnExportRequest;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaQuickColumnVO;
import com.coolcollege.intelligent.model.patrolstore.request.QuickTableColumnRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.QuickTableColumnVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: TbMetaQuickColumnService
 * @Description: 快速检查项service
 * @date 2022-04-06 14:57
 */
public interface TbMetaQuickColumnService {

    /**
     * 更新检查项状态
     * @param enterpriseId
     * @param id
     * @param statusEnum
     * @return
     */
    Boolean updateStatus(String enterpriseId, Long id, MetaColumnStatusEnum statusEnum);



    /**
     * 创建快捷检查表
     * @param enterpriseId
     * @param userId
     * @param quickTableColumnRequest
     */
    TbMetaQuickColumnVO createQuickTableColumn(String enterpriseId, String userId, QuickTableColumnRequest quickTableColumnRequest);


    /**
     * 更新快捷检查项
     * @param enterpriseId
     * @param userId
     * @param quickTableColumnRequest
     * @return
     */
    Boolean updateQuickTableColumn(String enterpriseId, String userId, QuickTableColumnRequest quickTableColumnRequest);


    /**
     * 获取检查项详情
     * @param enterpriseId
     * @param id
     * @return
     */
    QuickTableColumnVO getQuickTableColumnDetail(String enterpriseId, Long id, String userId);


    /**
     * 获取检查项详情
     * @param enterpriseId
     * @param id
     * @return
     */
    Long copyQuickTableColumn(String enterpriseId, Long id, CurrentUser user);


    /**
     * 检查项导出
     * @author chenyupeng
     * @date 2022/4/13
     * @param eid
     * @param request
     * @param user
     * @return com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO
     */
    ImportTaskDO exportQuickColumn(String eid, TbMetaQuickColumnExportRequest request, CurrentUser user);

    /**
     * 批量更新检查项状态
     * @param enterpriseId
     * @param ids
     * @param statusEnum
     * @return
     */
    Boolean batchUpdateStatus(String enterpriseId, List<Long> ids, MetaColumnStatusEnum statusEnum);


    /**
     * 快速检查项 配置权限
     * @param enterpriseId
     * @param quickTableColumnRequest
     * @param user
     * @return
     */
    Boolean configQuickColumnAuth(String enterpriseId,QuickTableColumnRequest quickTableColumnRequest, CurrentUser user);

    /**
     * 更新快速检查项的使用人
     * @param enterpriseId
     */
    void updateQuickColumnUseUser(String enterpriseId);

    /**
     * 获取企业AI模型列表
     * @param enterpriseId 企业id
     * @return AI模型VO列表
     */
    List<AIModelVO> getEnterpriseAIModelList(String enterpriseId);
}

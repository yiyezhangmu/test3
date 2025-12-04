package com.coolcollege.intelligent.model.ai;

import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.model.ai.dto.InspectionInfoDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import lombok.Data;

import java.util.List;

@Data
public class AIResolveRequestDTO {

    /**
     * 巡店项
     */
    private TbDataStaTableColumnDO dataColumn;

    /**
     * 标准项
     */
    private TbMetaStaTableColumnDO metaStaTableColumnDO;

    /**
     * 结果项
     */
    private List<TbMetaColumnResultDO> resultDOList;


    /**
     * 店务项
     */
    private SwStoreWorkDataTableColumnDO storeWorkDataColumn;

    /**
     * 风格
     */
    private String style;

    /**
     * 检查图片项
     */
    private InspectionInfoDTO inspectionInfoDTO;

    public String getModelCode(AiResolveBusinessTypeEnum businessType) {
        if(AiResolveBusinessTypeEnum.AI_INSPECTION.equals(businessType)){
            return inspectionInfoDTO.getModelCode();
        }
        return metaStaTableColumnDO.getAiModel();
    }

    public String getStandardPic(AiResolveBusinessTypeEnum businessType){
        if(AiResolveBusinessTypeEnum.AI_INSPECTION.equals(businessType)){
            return inspectionInfoDTO.getStandardPic();
        }
        return metaStaTableColumnDO.getStandardPic();
    }
}

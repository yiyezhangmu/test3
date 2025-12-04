package com.coolcollege.intelligent.service.ai;

import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.common.util.ImageUtil;
import com.coolcollege.intelligent.model.ai.AICommonPromptDTO;

import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.AIResolveRequestDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * <p>
 * 各平台AI处理 服务类
 * </p>
 *
 * @author wangff
 * @since 2025/6/5
 */
public interface AIOpenService {

    /**
     * AI调用
     * @param enterpriseId 企业id
     * @param aiCommonPromptDTO AI调用通用提示词DTO
     * @param imageList 图片url列表
     * @param aiModel AI算法模型
     * @return AI处理结果
     */
    String aiResolve(String enterpriseId, AICommonPromptDTO aiCommonPromptDTO, List<String> imageList, AiModelLibraryDO aiModel);


    /**
     * 批量处理店务ai
     * @param enterpriseId
     * @param processColumnList
     * @return
     */
    default void batchDealStoreWorkAiResolve(String enterpriseId, String dbName, List<SwStoreWorkDataTableColumnDO> processColumnList){
    }



    /**
     * 异步AI处理
     * @param enterpriseId 企业id
     * @param request AI处理请求DTO
     * @return AI处理DTO
     */
    default AIResolveDTO asyncAiResolve(String enterpriseId, AiResolveBusinessTypeEnum businessType, AIResolveRequestDTO request){
        return null;
    }


    /**
     * 同步AI处理
     * @param enterpriseId
     * @param request
     * @return
     */
    default AIResolveDTO syncAiResolve(String enterpriseId, AIResolveRequestDTO request){
        return null;
    }

    static Pair<String, List<String>> getAiImageOrBusinessId(AiResolveBusinessTypeEnum businessType, AIResolveRequestDTO request){
        switch (businessType){
            case PATROL:
                TbDataStaTableColumnDO dataColumn = request.getDataColumn();
                List<String> imageUrls = ImageUtil.getImageList(dataColumn.getCheckPics());
                return Pair.of(String.valueOf(dataColumn.getId()), imageUrls);
            case STORE_WORK:
                SwStoreWorkDataTableColumnDO storeWorkDataColumn = request.getStoreWorkDataColumn();
                List<String> storeWorkImageUrl = ImageUtil.getStoreWorkImageUrl(storeWorkDataColumn.getCheckPics());
                return Pair.of(String.valueOf(storeWorkDataColumn.getId()), storeWorkImageUrl);
            case AI_INSPECTION:
                return Pair.of(String.valueOf(request.getInspectionInfoDTO().getBusinessId()), request.getInspectionInfoDTO().getImageList());
            default:
                return null;
        }
    }

}

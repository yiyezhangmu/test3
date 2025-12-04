package com.coolcollege.intelligent.service.newbelle;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.model.newbelle.dto.BaseGoodsDetailDTO;
import com.coolcollege.intelligent.model.newbelle.dto.InventoryStoreDataDTO;
import com.coolcollege.intelligent.model.newbelle.request.BaseGoodsDetailRequest;
import com.coolcollege.intelligent.model.newbelle.request.InventoryStoreDataRequest;
import com.coolcollege.intelligent.model.newbelle.request.ProductNoBySubTaskIdRequest;
import com.coolcollege.intelligent.model.newbelle.request.RegionAndStoreRequest;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentStoreVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComptRegionStoreVO;

import java.util.List;

public interface ProductFeedbackService {
    /**
     * 货品反馈_半年内有库存店铺数据
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    List<InventoryStoreDataDTO> getInventoryStoreData(String enterpriseId, InventoryStoreDataRequest request);

    /**
     * 公共基础类_商品信息
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    BaseGoodsDetailDTO getBaseGoodsDetail(String enterpriseId, BaseGoodsDetailRequest request);

    /**
     * 选店组件（过滤未铺货的区域和门店）
     *
     * @param eid
     * @param parentId
     * @param userId
     * @param storeNewNo
     * @return
     */
    SelectComptRegionStoreVO getRegionAndStore(String eid, Long parentId, String userId, List<String> storeNewNo);


    PageVO<SelectComponentStoreVO> getStoresByKeyword(String enterpriseId,
                                                      String keyword,
                                                      Integer pageNum,
                                                      Integer pageSize,
                                                      String userId,
                                                      List<String> storeNewNo);

    String getProductNoBySubTaskId(String enterpriseId,
                                   ProductNoBySubTaskIdRequest request);
}

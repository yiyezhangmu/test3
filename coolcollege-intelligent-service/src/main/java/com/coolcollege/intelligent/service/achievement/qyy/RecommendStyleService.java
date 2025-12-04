package com.coolcollege.intelligent.service.achievement.qyy;

import com.coolcollege.intelligent.common.enums.ConversationTypeEnum;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.model.achievement.qyy.dto.AddRecommendStyleDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.UpdateRecommendStyleDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.*;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: RecommendStyleService
 * @Description: 主推款
 * @date 2023-04-11 15:29
 */
public interface RecommendStyleService {

    /**
     * 移动端获取主推款列表
     * @param enterpriseId
     * @param conversationId
     * @param conversationType
     * @return
     */
    List<H5RecommendStyleListVO> getH5RecommendStyleList(String enterpriseId, String conversationId, ConversationTypeEnum conversationType);

    /**
     * 主推款详情
     * @param enterpriseId
     * @param id
     * @return
     */
    H5RecommendStyleDetailVO getRecommendStyleDetail(String enterpriseId, Long id);

    /**
     * pc端获取主推款列表
     * @param enterpriseId
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<PCRecommendStyleListVO> getPCRecommendStylePage(String enterpriseId, String name, Integer pageNum, Integer pageSize);

    /**
     * pc端主推款详情
     * @param enterpriseId
     * @param id
     * @return
     */
    PCRecommendStyleDetailVO getPCRecommendStyleDetail(String enterpriseId, Long id);

    /**
     * 新增主推款
     * @param enterpriseId
     * @param param
     * @return
     */
    Boolean addRecommendStyle(String enterpriseId, String createUserId, String createUsername, AddRecommendStyleDTO param);

    /**
     * 更新主推款
     * @param enterpriseId
     * @param param
     * @return
     */
    Boolean updateRecommendStyle(String enterpriseId, String updateUserId, String updateUsername, UpdateRecommendStyleDTO param);

    /**
     * 删除主推款
     * @param enterpriseId
     * @param id
     * @return
     */
    Boolean deleteRecommendStyle(String enterpriseId, Long id);

    /**
     * 商品搜索
     * @param enterpriseId
     * @param goodsIds
     * @return
     */
    List<RecommendStyleGoodsVO> searchGoods(String enterpriseId, String goodsIds);
}

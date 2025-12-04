package com.coolcollege.intelligent.mapper.achieve.qyy;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ConversationTypeEnum;
import com.coolcollege.intelligent.dao.qyy.QyyRecommendStyleMapper;
import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: QyyRecommendStyleDAO
 * @Description: 主推款
 * @date 2023-04-11 16:18
 */
@Service
@Slf4j
public class QyyRecommendStyleDAO {

    @Resource
    private QyyRecommendStyleMapper qyyRecommendStyleMapper;

    /**
     * 新增主推款
     * @param enterpriseId
     * @param param
     * @return
     */
    public Boolean addRecommendStyle(String enterpriseId, QyyRecommendStyleDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param)){
            return null;
        }
        qyyRecommendStyleMapper.insertSelective(enterpriseId, param);
        return true;
    }

    /**
     * 更新主推款
     * @param enterpriseId
     * @param param
     * @return
     */
    public Boolean updateRecommendStyle(String enterpriseId, QyyRecommendStyleDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param) || Objects.isNull(param.getId())){
            return null;
        }
        qyyRecommendStyleMapper.updateByPrimaryKey(enterpriseId, param);
        return true;
    }


    /**
     * 通过群 获取主推款
     * @param enterpriseId
     * @param conversationId
     * @return
     */
    public List<QyyRecommendStyleDO> getRecommendStyleByConversationId(String enterpriseId, String conversationId, ConversationTypeEnum conversationType){
        if(StringUtils.isAnyBlank(enterpriseId, conversationId) || Objects.isNull(conversationType)){
            return Lists.newArrayList();
        }
        return qyyRecommendStyleMapper.getRecommendStyleByConversationId(enterpriseId, conversationId, conversationType.getCode());
    }

    /**
     * 获取主推款详情
     * @param enterpriseId
     * @param id
     * @return
     */
    public QyyRecommendStyleDO getRecommendStyleDetail(String enterpriseId, Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)){
            return null;
        }
        return qyyRecommendStyleMapper.getRecommendStyleDetail(enterpriseId, id);
    }

    public Page<QyyRecommendStyleDO> getPCRecommendStylePage(String enterpriseId, String name){
        return qyyRecommendStyleMapper.getPCRecommendStylePage(enterpriseId, name);
    }

    public Boolean deleteRecommendStyle(String enterpriseId, Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)){
            return Boolean.FALSE;
        }
        QyyRecommendStyleDO update = new QyyRecommendStyleDO();
        update.setId(id);
        update.setDeleted(true);
        return qyyRecommendStyleMapper.updateByPrimaryKeySelective(enterpriseId, update) > Constants.ZERO;
    }

    /**
     * 获取定时任务主推款
     * @param enterpriseId
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<QyyRecommendStyleDO> getTimerRecommendStylePage(String enterpriseId, String beginTime, String endTime){
        if(StringUtils.isAnyBlank(enterpriseId, beginTime, endTime)){
            return Lists.newArrayList();
        }
        return qyyRecommendStyleMapper.getTimerRecommendStylePage(enterpriseId, beginTime, endTime);
    }

    /**
     * 更新发送状态
     * @param enterpriseId
     * @param ids
     * @return
     */
    public Integer updateRecommendStyleSendStatus(String enterpriseId, List<Long> ids){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(ids)){
            return Constants.ZERO;
        }
        return qyyRecommendStyleMapper.updateRecommendStyleSendStatus(enterpriseId, ids);
    }

}

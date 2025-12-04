package com.coolcollege.intelligent.mapper.messageboard;

import com.coolcollege.intelligent.dao.messageboard.MessageBoardMapper;
import com.coolcollege.intelligent.model.messageboard.entity.MessageBoardDO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author wxp
 * @FileName: MessageBoardDAO
 * @Description:
 * @date 2024-07-29 16:24
 */
@Repository
public class MessageBoardDAO {

    @Resource
    private MessageBoardMapper messageBoardMapper;

    public Long addMessageBoard(String enterpriseId, MessageBoardDO param){
        if(StringUtils.isBlank(enterpriseId)){
            return null;
        }
        messageBoardMapper.insertSelective(param, enterpriseId);
        return param.getId();
    }


    public Integer updateMessageBoard(String enterpriseId, MessageBoardDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param.getId())){
            return null;
        }
        return messageBoardMapper.updateByPrimaryKeySelective(param, enterpriseId);
    }

    /**
     * 分页获取留言列表
     * @param enterpriseId
     * @param businessId
     * @param businessType
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<MessageBoardDO> getMessagePage(String enterpriseId, String businessId, String businessType, Integer pageNum, Integer pageSize){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(businessId) || StringUtils.isBlank(businessType)){
            return new Page<>();
        }
        PageHelper.startPage(pageNum, pageSize);
        return messageBoardMapper.getMessagePage(enterpriseId, businessId, businessType);
    }

    /**
     * 获取点赞记录
     * @param enterpriseId
     * @param businessId
     * @param businessType
     * @param createUserId
     * @return
     */
    public MessageBoardDO getLikeRecord(String enterpriseId, String businessId, String businessType, String createUserId){
        if(StringUtils.isAnyBlank(enterpriseId, businessType, createUserId) || Objects.isNull(businessId)){
            return null;
        }
        return messageBoardMapper.getLikeRecord(enterpriseId, businessId, businessType, createUserId);
    }

}

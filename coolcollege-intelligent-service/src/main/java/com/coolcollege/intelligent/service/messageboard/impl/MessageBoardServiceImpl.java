package com.coolcollege.intelligent.service.messageboard.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.messageboard.MessageOperateTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.mapper.messageboard.MessageBoardDAO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.messageboard.dto.MessageBoardDTO;
import com.coolcollege.intelligent.model.messageboard.entity.MessageBoardDO;
import com.coolcollege.intelligent.model.messageboard.vo.MessageBoardVO;
import com.coolcollege.intelligent.service.messageboard.MessageBoardService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wxp
 * @FileName: MessageBoardServiceImpl
 * @Description:
 * @date 2024-07-29 16:24
 */
@Slf4j
@Service
public class MessageBoardServiceImpl implements MessageBoardService {

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private MessageBoardDAO messageBoardDAO;

    @Override
    public PageInfo<MessageBoardVO> getMessagePage(String enterpriseId, String businessId, String businessType, Integer pageNum, Integer pageSize) {
        Page<MessageBoardDO> messageBoardPage = messageBoardDAO.getMessagePage(enterpriseId, businessId, businessType, pageNum, pageSize);
        List<MessageBoardVO> resultList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(messageBoardPage)){
            List<String> userIds = messageBoardPage.stream().map(MessageBoardDO::getCreateUserId).collect(Collectors.toList());
            Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
            resultList = MessageBoardVO.convertVO(messageBoardPage, userMap);
        }
        PageInfo resultPage = new PageInfo(messageBoardPage);
        resultPage.setList(resultList);
        return resultPage;
    }


    @Override
    public MessageBoardVO leaveMessageOrLike(String enterpriseId, String userId, MessageBoardDTO param) {
        if(StringUtils.isAnyBlank(enterpriseId, userId, param.getBusinessType(), param.getOperateType()) ||
                Objects.isNull(param.getBusinessId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        MessageBoardVO messageBoardVO = new MessageBoardVO();
        if(MessageOperateTypeEnum.LIKE.getCode().equals(param.getOperateType())){
            MessageBoardDO messageBoardDO = messageBoardDAO.getLikeRecord(enterpriseId, param.getBusinessId(), param.getBusinessType(), userId);
            if(messageBoardDO != null){
                messageBoardDO.setLikeCount(messageBoardDO.getLikeCount() + Constants.INDEX_ONE);
                messageBoardDO.setUpdateTime(new Date());
                messageBoardDAO.updateMessageBoard(enterpriseId, messageBoardDO);
                BeanUtils.copyProperties(messageBoardDO, messageBoardVO);
                return messageBoardVO;
            }
        }
        MessageBoardDO messageBoardDO = new MessageBoardDO();
        messageBoardDO.setBusinessId(param.getBusinessId());
        messageBoardDO.setBusinessType(param.getBusinessType());
        messageBoardDO.setOperateType(param.getOperateType());
        if(MessageOperateTypeEnum.LIKE.getCode().equals(param.getOperateType())){
            messageBoardDO.setLikeCount(Constants.INDEX_ONE);
        }else {
            messageBoardDO.setMessageContent(param.getMessageContent());
        }
        messageBoardDO.setCreateUserId(userId);
        messageBoardDO.setUpdateUserId(userId);
        messageBoardDO.setCreateTime(new Date());
        messageBoardDO.setUpdateTime(new Date());
        messageBoardDO.setDeleted(false);
        messageBoardDAO.addMessageBoard(enterpriseId, messageBoardDO);
        BeanUtils.copyProperties(messageBoardDO, messageBoardVO);
        return messageBoardVO;
    }

}

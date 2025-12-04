package com.coolcollege.intelligent.service.messageboard;

import com.coolcollege.intelligent.model.messageboard.dto.MessageBoardDTO;
import com.coolcollege.intelligent.model.messageboard.vo.MessageBoardVO;
import com.github.pagehelper.PageInfo;

/**
 * @author wxp
 * @FileName: MessageBoardService
 * @Description:
 * @date 2024-07-29 16:24
 */
public interface MessageBoardService {

    /**
     * 获取留言列表
     * @param enterpriseId
     * @param businessId
     * @param businessType
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<MessageBoardVO> getMessagePage(String enterpriseId, String businessId, String businessType, Integer pageNum, Integer pageSize);


    /**
     * 留言/点赞
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    MessageBoardVO leaveMessageOrLike(String enterpriseId, String userId, MessageBoardDTO param);

}

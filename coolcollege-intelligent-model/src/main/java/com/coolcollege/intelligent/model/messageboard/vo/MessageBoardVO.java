package com.coolcollege.intelligent.model.messageboard.vo;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.messageboard.entity.MessageBoardDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author   wxp
 * @date   2024-07-29 16:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBoardVO implements Serializable {

    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("业务类型 店务storework 其它other")
    private String businessType;

    @ApiModelProperty("操作类型 留言message 点赞like")
    private String operateType;

    @ApiModelProperty("留言内容")
    private String messageContent;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("发起人")
    private String createUserId;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @ApiModelProperty("头像")
    private String avatar;

    public static List<MessageBoardVO> convertVO(List<MessageBoardDO> messageBoardDOList, Map<String, EnterpriseUserDO> userMap){
        if(CollectionUtils.isEmpty(messageBoardDOList)){
            return Lists.newArrayList();
        }
        List<MessageBoardVO> resultList = new ArrayList<>();
        for (MessageBoardDO messageBoardDO : messageBoardDOList) {
            MessageBoardVO messageBoardVO = new MessageBoardVO();
            BeanUtils.copyProperties(messageBoardDO, messageBoardVO);
            EnterpriseUserDO enterpriseUser = userMap.get(messageBoardDO.getCreateUserId());
            String username = Optional.ofNullable(enterpriseUser).map(EnterpriseUserDO::getName).orElse("");
            String userAvatar = Optional.ofNullable(enterpriseUser).map(EnterpriseUserDO::getAvatar).orElse("");
            messageBoardVO.setCreateUserName(username);
            messageBoardVO.setAvatar(userAvatar);
            resultList.add(messageBoardVO);
        }
        return resultList;
    }
}
package com.coolcollege.intelligent.model.activity.vo;

import com.coolcollege.intelligent.model.activity.entity.ActivityLikeDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-07-03 08:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLikePageVO implements Serializable {

    @ApiModelProperty("点赞人")
    private String likeUserId;

    @ApiModelProperty("点赞人姓名")
    private String likeUsername;

    @ApiModelProperty("点赞人头像")
    private String likeUserAvatar;

    @ApiModelProperty("创建时间")
    private Date createTime;

    public static List<ActivityLikePageVO> convertVO(List<ActivityLikeDO> likeList, Map<String, EnterpriseUserDO> userMap){
        if(CollectionUtils.isEmpty(likeList)){
            return Lists.newArrayList();
        }
        List<ActivityLikePageVO> resultList = new ArrayList<>();
        for (ActivityLikeDO activityLike : likeList) {
            ActivityLikePageVO like = new ActivityLikePageVO();
            like.setLikeUserId(activityLike.getLikeUserId());
            EnterpriseUserDO enterpriseUser = userMap.get(activityLike.getLikeUserId());
            String username = Optional.ofNullable(enterpriseUser).map(EnterpriseUserDO::getName).orElse("");
            String userAvatar = Optional.ofNullable(enterpriseUser).map(EnterpriseUserDO::getAvatar).orElse("");
            like.setLikeUsername(username);
            like.setLikeUserAvatar(userAvatar);
            like.setCreateTime(activityLike.getCreateTime());
            resultList.add(like);
        }
        return resultList;
    }
}
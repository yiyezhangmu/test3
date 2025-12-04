package com.coolcollege.intelligent.model.activity.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.activity.entity.ActivityCommentDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-07-03 08:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCommentDTO implements Serializable {

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评论附件照片json字段[{},{}]")
    private String contentPics;

    @ApiModelProperty("评论附件视频json字段[{},{}]")
    private String contentVideo;

    public static ActivityCommentDO convertDO(ActivityCommentDTO param, String userId){
        ActivityCommentDO insert = new ActivityCommentDO();
        insert.setActivityId(param.getActivityId());
        insert.setCommentUserId(userId);
        insert.setContent(param.getContent());
        insert.setContentPics(param.getContentPics());
        insert.setContentVideo(param.getContentVideo());
        insert.setLikeCount(Constants.ZERO);
        insert.setCommentUserId(userId);
        insert.setUpdateUserId(userId);
        insert.setCreateTime(new Date());
        insert.setUpdateTime(new Date());
        insert.setIsContainsPic(Boolean.FALSE);
        insert.setIsContainsVideo(Boolean.FALSE);
        JSONArray containsPicArray = JSONObject.parseArray(param.getContentPics());
        if(Objects.nonNull(containsPicArray) && !containsPicArray.isEmpty()){
            insert.setIsContainsPic(Boolean.TRUE);
        }
        JSONArray containsVideoArray = JSONObject.parseArray(param.getContentVideo());
        if(Objects.nonNull(containsVideoArray) && !containsVideoArray.isEmpty()){
            insert.setIsContainsVideo(Boolean.TRUE);
        }
        return insert;
    }
}
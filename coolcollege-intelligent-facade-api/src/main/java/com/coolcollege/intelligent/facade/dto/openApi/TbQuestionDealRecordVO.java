package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 工单操作历史
 * @author   zhangchenbiao
 * @date   2021-12-20 07:18
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbQuestionDealRecordVO implements Serializable {

    /**
     * 操作类型create(创建) handle(处理)  approve(审批)  turn(转交) reallocate(重新分配)
     */
    private String operateType;

    /**
     * 处理时间
     */
    private Date dealTime;

    /**
     * 操作人id
     */
    private String operateUserId;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 用户头像
     */
    private String operateUserAvatar;

    /**
     * 审核行为,pass通过 reject拒绝 rectified已整改 unneeded无需整改
     */
    private String actionKey;

    /**
     * 备注
     */
    private String remark;

    /**
     * 图片
     */
    private List<String> photoList;

    /**
     * 视频
     */
    private List<VideoDTO> videoList;


    @Data
    public static class VideoDTO{
        private String videoId;
        private String videoUrl;
    }
}
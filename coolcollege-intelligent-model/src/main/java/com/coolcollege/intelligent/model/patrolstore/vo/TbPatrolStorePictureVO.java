package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStorePictureVO {
    /**
     * 抓拍状态 1 抓拍中 2 结束抓拍 或 没有调用抓拍
     */
    private Integer captureStatus;
    /**
     * 抓拍图片列表
     */
    private List<TbPatrolStorePictureInfoVO> pictureList;
}

package com.coolcollege.intelligent.model.picture.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureCenterColumnAIResultVO {

    /**
     * 图片id
     */
    private Long pictureId;

    /**
     * 算法结果
     */
    private String aiResult;

    /**
     * 算法类型
     */
    private String aiType;

}

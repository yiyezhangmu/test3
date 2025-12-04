package com.coolcollege.intelligent.model.pictureInspection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2021/8/26 17:39
 * @Version 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreSceneDo {


    /**
     * id
     */
    private Long id;

    /**
     * 巡检场景名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记
     */
    private Integer deleted;

    /**
     * 场景类型
     */
    private String sceneType;

    /**
     * 设备数
     */
    private Integer  sceneNum;


}

package com.coolcollege.intelligent.model.picture.query;

import com.coolcollege.intelligent.model.operationboard.query.BaseQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 图片中心
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@ApiModel("图片中心请求体")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureCenterQuery extends BaseQuery {
    /**
     * 区域id
     */
    private List<String> regionIdList;

    /**
     * 区域id
     */
    @ApiModelProperty("区域id")
    private String regionId;

    /**
     * 门店id集合
     */
    private List<String> storeIdList;

    /**
     * 检查表id
     */
    @ApiModelProperty("检查表id")
    private Long metaTableId;

    @ApiModelProperty("分类ID")
    private Long categoryId;


    /**
     * 区域路径
     */
    @ApiModelProperty("区域路径")
    private String regionPath;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店id
     */
    @ApiModelProperty("任务名称")
    private String taskName;

    /**
     * 检查项id
     */
    @ApiModelProperty("检查项id")
    private List<Long> metaColumnIdList;

    /**
     * 工单来源
     */
    @ApiModelProperty("工单来源")
    private String questionType;

    /**
     * 抓拍类型 timing:定时抓拍,AI AI抓拍
     */
    @ApiModelProperty("抓拍类型")
    private String capturePictureType;

    @ApiModelProperty("任务类型")
    private String taskType;

    private List<String> regionPathList;

    private CurrentUser currentUser;

    /**
     * 审批节点
     */
    @ApiModelProperty("审批节点_工单3.0")
    private Integer nodeNo;

    /**
     * 工单名称
     */
    @ApiModelProperty("工单名称_工单3.0（支持父工单名称与子工单名称）")
    private String questionName;

    @ApiModelProperty("任务状态 handle:未完成 complete:已完成")
    private String status;


    @ApiModelProperty("发起人id列表")
    private List<String> createUserIdList;
}

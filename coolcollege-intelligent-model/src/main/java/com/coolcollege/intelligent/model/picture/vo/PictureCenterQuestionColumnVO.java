package com.coolcollege.intelligent.model.picture.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.ldap.PagedResultsControl;
import java.util.List;

/**
 * @Description:
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@ApiModel(value = "图片中心-图片VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureCenterQuestionColumnVO {
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 业务id
     */
    @ApiModelProperty("业务id")
    private Long businessId;

    /**
     * 图片路径
     */
    @ApiModelProperty("图片路径")
    private String pictureUrl;

    /**
     * 视频路径
     */
    @ApiModelProperty("视频路径")
    private String videoUrl;



    /**
     * 处理节点 0:创建人 1:处理日 2:审批人 3:复审人
      */
    @ApiModelProperty("处理节点 0:创建人 1:处理日 2:审批人 3:复审人")
    private String nodeNo;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String userName;

}

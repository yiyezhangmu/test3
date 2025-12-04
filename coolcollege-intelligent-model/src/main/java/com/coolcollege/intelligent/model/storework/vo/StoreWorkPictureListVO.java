package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.picture.vo.PictureCenterQuestionColumnVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/10/20 14:45
 * @Version 1.0
 */
@Data
public class StoreWorkPictureListVO {

    @ApiModelProperty("店务日期 周清(本周周一) 月清(当月1号)")
    private Date storeWorkData;
    @ApiModelProperty("当年第几周")
    private Integer WeekOfTheYear;
    @ApiModelProperty("月份")
    private Integer MonthOfTheYear;
    @ApiModelProperty("店务名称")
    private String workName;
    @ApiModelProperty("表时间配置")
    private String tableInfo;
    @ApiModelProperty("开始时间")
    private Date beginTime;
    @ApiModelProperty("结束时间")
    private Date endTime;
    @ApiModelProperty("表名称")
    private String tableName;

    private String storeName;

    private String storeId;
    /**
     * 图片集合
     */
    @ApiModelProperty("图片集合")
    private List<StoreWorkPictureCenterColumnVO> storeWorkPictureCenterColumnVOS;








}

package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/10/20 14:59
 * @Version 1.0
 */
@Data
public class PictureCenterRequest extends PageRequest {

    private String workCycle;

    private List<String> regionIds;

    private List<String> storeIds;

    private List<String> regionPathList;

    private Long storeWorkId;

    private Long metaTableId;

    private Long tableMappingId;

    private Date beginTime;

    private Date endTime;

    private List<Long> metaColumnIds;
}

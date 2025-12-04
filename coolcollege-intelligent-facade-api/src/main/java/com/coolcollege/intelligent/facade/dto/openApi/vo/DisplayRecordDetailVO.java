package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/7/12 14:01
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayRecordDetailVO {

    private List<DisplayTableDataColumnVO> tbDisplayDataColumnList;

    private List<DisplayTableDataContentVO> tbDisplayDataContentList;

    private String storeName;

    private String taskName;

    private String tableName;

    private String taskType;

    private String taskDesc;

    private String handleUserName;

    private String approveUserName;

    private String recheckUserName;

    private String avatar;

    private String handlerDuration;

    private String approveDuration;

}

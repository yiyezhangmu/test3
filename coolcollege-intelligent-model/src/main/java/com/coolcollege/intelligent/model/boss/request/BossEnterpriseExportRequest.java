package com.coolcollege.intelligent.model.boss.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: BossEnterpriseExportRequest
 * @Description: boss企业列表/导出
 * @date 2021-10-18 9:52
 */
@Data
public class BossEnterpriseExportRequest extends PageRequest {

    /**
     * 企业名称
     */
    private String name;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 是否个人
     */
    private Boolean isPersonal;

    /**
     * 企业标签
     */
    private String tag;

    /**
     * 套餐开始时间-开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date packageBeginDateStart;

    /**
     * 套餐开始时间-结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date packageBeginDateEnd;

    /**
     * 当前套餐
     */
    private Integer currentPackageId;

    /**
     * 开通类型
     */
    private String appType;

    /**
     * 是否留资
     */
    private Boolean isLeaveInfo;
}

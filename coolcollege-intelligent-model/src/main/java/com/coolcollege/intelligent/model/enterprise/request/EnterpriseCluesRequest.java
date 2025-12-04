package com.coolcollege.intelligent.model.enterprise.request;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author chenyupeng
 * @since 2021/11/23
 */
@Data
public class EnterpriseCluesRequest  extends FileExportBaseRequest {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否个人版
     */
    private Boolean isPersonal;

    /**
     * 开通类型
     *
     */
    private String appType;

    /**
     * 用户类型(1:普通用户 2:付费用户  3:试用用户 4:共创用户)
     */
    private Integer isVip;

    /**
     * 所属行业
     */
    private String industry;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date beginDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    private Long userId;

    private Integer isAuthenticated;
}

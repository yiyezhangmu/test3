package com.coolcollege.intelligent.model.achievement.qyy.vo.ak;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportTaskRecordVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 组织id
     */
    private Long orgId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 是否是导入，0-否，1-是
     */
    private Integer isImport;

    /**
     * 状态：1-进行中，2-成功，3-失败
     */
    private Integer status;

    /**
     * 成功条数
     */
    private Integer successNum;

    /**
     * 总条数
     */
    private Integer totalNum;

    /**
     * 文件下载地址
     */
    private String fileUrl;

    /**
     * 创建人Uid
     */
    private Long creatorId;

    /**
     * 备注
     */
    private String remark;
}


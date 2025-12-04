package com.coolcollege.intelligent.model.impoetexcel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 邵凌志
 * @date 2020/12/9 16:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportTaskDO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型：region-区域，store-门店，user-人员
     */
    private String fileType;

    /**
     * 是否是导入
     */
    private Boolean isImport;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 成功条数
     */
    private Integer successNum;

    /**
     * 总条数
     */
    private Integer totalNum;

    /**
     * 上传人员id
     */
    private String createUserId;

    /**
     * 上传人
     */
    private String createName;

    /**
     * 上传时间
     */
    private Long createTime;

    /**
     * 备注
     */
    private String remark;

    public ImportTaskDO(String fileName, String fileType, Boolean isImport, Integer status, String createUserId, String createName, Long createTime) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.isImport = isImport;
        this.status = status;
        this.createUserId = createUserId;
        this.createName = createName;
        this.createTime = createTime;
    }
}

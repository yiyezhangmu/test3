package com.coolcollege.intelligent.common.enums.license;


import com.coolcollege.intelligent.common.util.DateUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * @author xuanfeng
 * @Description 证照状态枚举
 */
public enum LicenseStatusEnum {
    /**
     * 正常
     */
    NORMAL("normal", "正常"),
    /**
     * 临期
     */
    TEMPORARY("temporary", "临期"),
    /**
     * 过期
     */
    OVERDUE("overdue", "过期"),

    MISSING("missing", "缺失"),

    NO_NEED_UPLOAD("no_need_upload","无需上传" );


    /**
     * 来源
     */
    private String status;

    /**
     * 信息
     */
    private String msg;

    LicenseStatusEnum(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public static LicenseStatusEnum getLicenseStatus(Date expiryEndDate, Integer plusDay) {
        if (Objects.isNull(expiryEndDate) || Objects.isNull(plusDay)) {
            return LicenseStatusEnum.NORMAL;
        }
        if (new Date().after(expiryEndDate)) {
            return LicenseStatusEnum.OVERDUE;
        }
        LocalDateTime localDateTime = DateUtil.plusDays(LocalDateTime.now(), plusDay);
        Date plusDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        if (expiryEndDate.compareTo(plusDate) >= 0) {
            return LicenseStatusEnum.NORMAL;
        } else {
            return LicenseStatusEnum.TEMPORARY;
        }
    }

    public static LicenseStatusEnum getLicenseStatus(String expiryType ,Date expiryEndDate, Integer temporaryPlusDay) {
        if("long".equals(expiryType)){
            return LicenseStatusEnum.NORMAL;
        }
        if(Objects.isNull(expiryEndDate)){
            return LicenseStatusEnum.MISSING;
        }
        if (new Date().after(expiryEndDate)) {
            return LicenseStatusEnum.OVERDUE;
        }
        LocalDateTime localDateTime = DateUtil.plusDays(LocalDateTime.now(), temporaryPlusDay);
        Date plusDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        if (expiryEndDate.compareTo(plusDate) >= 0) {
            return LicenseStatusEnum.NORMAL;
        } else {
            return LicenseStatusEnum.TEMPORARY;
        }
    }
}

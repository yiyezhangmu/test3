package com.coolcollege.intelligent.common.enums;

public enum ExportImageDomainReplaceCodeEnum {
    OSS_COOLCOLLEGE("oss.coolcollege.cn","oss_picture_change_code"),
    OSS_COOLCOLLEGE_PROCESSOR("oss-processor.coolcollege.cn","oss_processor_picture_change_code"),
    OSS_COOLCOLLEGE_STORE("oss-store.coolcollege.cn","oss_store_change_code"),
    OSS_COOLSTORE("oss-cool.coolstore.cn","oss_cool_store_change_code"),
    OSS_COOLSTORE_PREVIEW("preview-cool.coolstore.cn","preview_cool_change_code"),
    OSS_ONE_COOLSTORE("ossfile1.coolstore.cn","oss_cool_store_one_change_code"),
    OSS_ONE_COOLSTORE_PREVIEW("preview-oss.coolstore.cn","preview_cool_one_change_code"),
    OSS_COOLCOLLEGE_LICENSE("store-license.oss-cn-hangzhou.aliyuncs.com","store_license_picture_store_change_code"),
    OSS_COOLSTORE_LICENSE("coolstore-license.oss-cn-hangzhou.aliyuncs.com","coolstore-license_picture_store_change_code"),
    ;
    private String domain;

    private String replaceCode;

    ExportImageDomainReplaceCodeEnum(String domain, String replaceCode) {
        this.domain = domain;
        this.replaceCode = replaceCode;
    }

    public String getDomain() {
        return domain;
    }

    public String getReplaceCode() {
        return replaceCode;
    }

    public static String replaceCode(String url) {
        for (ExportImageDomainReplaceCodeEnum value : values()) {
            url = url.replace(value.getDomain(), value.getReplaceCode());
        }
        return url;
    }

    public static String resetUrl(String url) {
        for (ExportImageDomainReplaceCodeEnum value : values()) {
            url = url.replace(value.replaceCode, value.domain);
        }
        return url;
    }

}

package com.coolcollege.intelligent.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 杰峰云存储设备状态枚举类
 * </p>
 *
 * @author wangff
 * @since 2025/8/13
 */
@Getter
@AllArgsConstructor
public enum JfyStorageStatusEnum {
    STATUS0(0, "正常"),
    STATUS1(1, "没有句柄"),
    STATUS2(2, "文件结束"),
    STATUS3(3, "底层读写错误"),
    STATUS4(4, "底层读写错误"),
    STATUS5(5, "没有空间"),
    STATUS6(6, "文件不存在"),
    STATUS7(7, "没有存储介质"),
    STATUS8(8, "单个硬盘或硬盘时间冲突"),
    STATUS9(9, "必须重新初始化"),
    STATUS10(10, "用户数据无效"),
    STATUS11(11, "句柄无效"),
    STATUS12(12, "文件名无效"),
    STATUS13(13, "文件已经存在"),
    STATUS14(14, "文件系统最后时间和当前时间冲突"),
    STATUS15(15, "没有检查到可用的存储介质"),
    STATUS16(16, "存储介质被移除，拒绝访问"),
    STATUS17(17, "存储介质是只读状态"),
    STATUS18(18, "开机初始化wfs文件系统时读写存储介质卡住"),
    STATUS19(19, "存储介质热插拔初始化wfs文件系统时读写存储介质卡住"),
    STATUS20(20, "存储介质是扩容的并且真实容量小于16MB"),
    STATUS21(21, "存储介质执行格式化操作时卡住"),
    STATUS22(22, "因为读写错误被应用层标记为坏的"),
    STATUS23(23, "驱动层将该存储介质标记为坏的"),
    STATUS24(24, "录像分区大小异常，需要重新分区格式化"),

    ;

    /**
     * 状态编码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String msg;

    public static String getMsgByCode(Integer code) {
        for (JfyStorageStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value.msg;
            }
        }
        return null;
    }
}

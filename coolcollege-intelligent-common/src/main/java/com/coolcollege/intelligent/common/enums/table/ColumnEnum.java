package com.coolcollege.intelligent.common.enums.table;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/9/4 19:48
 */
public enum ColumnEnum {

    IGNORE("ignore","不强制",0),
    FORCE("force","强制",0),

    MUST_PICD("mustPic","强制拍视频",3),

    MUST_PICA("mustPic","强制上传图片",2),
    MUST_PICB("mustPic","强制拍照",1),
    MUST_PICC("mustPic","不强制",0);

    private String code;
    private String msg;

    private Integer num;
    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    public Integer getNum(){
        return num;
    }
    ColumnEnum(String code, String msg,Integer num) {
        this.code = code;
        this.msg = msg;
        this.num  =num;
    }

    public static String getMsgByCode(String code){
        for (ColumnEnum columnEnum : ColumnEnum.values()) {
            if (columnEnum.getCode().equals(code)) {
                return columnEnum.getMsg();
            }
        }
        return null;
    }
    public static String getMsgByNum(Integer num){
        for (ColumnEnum columnEnum : ColumnEnum.values()) {
            if (columnEnum.getNum().equals(num)) {
                return columnEnum.getMsg();
            }
        }
        return null;
    }
}

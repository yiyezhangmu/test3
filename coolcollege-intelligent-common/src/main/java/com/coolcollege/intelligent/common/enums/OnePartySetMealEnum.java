package com.coolcollege.intelligent.common.enums;

/**
 * @author zhangnan
 * @date 2022-06-28 9:09
 */
public enum OnePartySetMealEnum {


    /**
     * 基础版
     */
    FREE("FREE",100, "[{\"code\":0,\"name\":\"普通项\"}]", "[{\"code\":0,\"name\":\"普通表\",\"columnTypes\":[{\"code\":0,\"name\":\"普通项\"}]}]", 3, 50, 3, 0),

    /**
     * 升级版
     */
    RIGHTS_MDT_LEVEL_PRO("RIGHTS_MDT_LEVEL_PRO",Integer.MAX_VALUE, null, null, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 0),

    /**
     * 高级版
     */
    RIGHTS_MDT_LEVEL_ADV("RIGHTS_MDT_LEVEL_ADV",Integer.MAX_VALUE, null, null, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 3),

    /**
     * 尊享版
     */
    RIGHTS_MDT_LEVEL_EXP("RIGHTS_MDT_LEVEL_EXP", Integer.MAX_VALUE, null, null, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 5),

    ;

    /**
     * 套餐版本
     */
    private String version;
    /**
     * sop文档数量
     */
    private Integer sopQuantity;

    /**
     * 检查项属性
     */
    private String metaColumnProperties;

    /**
     * 检查表属性
     */
    private String metaTableProperties;

    /**
     * 检查表数量
     */
    private Integer metaTableQuantity;

    /**
     * 工单数量
     */
    private Integer questionRecordQuantity;

    /**
     * 自检图片数量
     */
    private Integer selfCheckPictureQuantity;

    /**
     * 单个门店摄像头设备数量
     */
    private Integer singleStoreDeviceQuantity;


    OnePartySetMealEnum(String version, Integer sopQuantity, String metaColumnProperties,
                        String metaTableProperties, Integer metaTableQuantity, Integer questionRecordQuantity,
                        Integer selfCheckPictureQuantity, Integer singleStoreDeviceQuantity) {
        this.version = version;
        this.sopQuantity = sopQuantity;
        this.metaColumnProperties = metaColumnProperties;
        this.metaTableProperties = metaTableProperties;
        this.metaTableQuantity = metaTableQuantity;
        this.questionRecordQuantity = questionRecordQuantity;
        this.selfCheckPictureQuantity = selfCheckPictureQuantity;
        this.singleStoreDeviceQuantity = singleStoreDeviceQuantity;
    }

    public static OnePartySetMealEnum getByVersion(String version){
        for (OnePartySetMealEnum value : OnePartySetMealEnum.values()) {
            if(value.getVersion().equals(version)){
                return value;
            }
        }
        return null;
    }


    public String getVersion() {
        return version;
    }

    public Integer getSopQuantity() {
        return sopQuantity;
    }

    public String getMetaColumnProperties() {
        return metaColumnProperties;
    }

    public String getMetaTableProperties() {
        return metaTableProperties;
    }

    public Integer getMetaTableQuantity() {
        return metaTableQuantity;
    }

    public Integer getQuestionRecordQuantity() {
        return questionRecordQuantity;
    }

    public Integer getSelfCheckPictureQuantity() {
        return selfCheckPictureQuantity;
    }

    public Integer getSingleStoreDeviceQuantity() {
        return singleStoreDeviceQuantity;
    }

}

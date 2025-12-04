package com.coolcollege.intelligent.common.enums.region;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: FixedRegionEnum
 * @Description:
 * @date 2023-10-18 15:54
 */
public enum FixedRegionEnum {

    DEFAULT(-2L, "1","path", "默认分组", "/1/", "/1/-2/", 1, false),
    DEFAULT_STORE(-3L, "1","store", "测试门店", "/1/", "/1/-3/",0, false),
    EXTERNAL_USER(-4L, "1","path", "外部用户", "/1/", "/1/-4/",0, true),
    MYSTERIOUS_GUEST(-5L, "-4","path", "神秘访客", "/1/-4/","/1/-4/-5/",0, true),
    ;


    private Long id;

    private String parentId;

    private String regionType;

    private String name;

    private String regionPath;

    private String fullRegionPath;

    private Integer unclassifiedFlag;

    private Boolean isExternalNode;

    FixedRegionEnum(Long id, String parentId, String regionType, String name, String regionPath, String fullRegionPath, Integer unclassifiedFlag, Boolean isExternalNode) {
        this.id = id;
        this.parentId = parentId;
        this.regionType = regionType;
        this.name = name;
        this.regionPath = regionPath;
        this.fullRegionPath = fullRegionPath;
        this.unclassifiedFlag = unclassifiedFlag;
        this.isExternalNode = isExternalNode;
    }

    public Long getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getRegionType() {
        return regionType;
    }

    public String getName() {
        return name;
    }

    public String getRegionPath() {
        return regionPath;
    }

    public Integer getUnclassifiedFlag() {
        return unclassifiedFlag;
    }

    public Boolean getExternalNode() {
        return isExternalNode;
    }

    public String getFullRegionPath() {
        return fullRegionPath;
    }

    /**
     * 获取排除区域
     * @return
     */
    public static List<Long> getExcludeRegion(){
        List<Long> resultList = new ArrayList<>();
        resultList.add(FixedRegionEnum.DEFAULT.id);
        resultList.add(FixedRegionEnum.EXTERNAL_USER.id);
        return resultList;
    }

    public static List<String> getExcludeRegionId(){
        List<String> resultList = new ArrayList<>();
        resultList.add(String.valueOf(FixedRegionEnum.DEFAULT.id));
        resultList.add(String.valueOf(FixedRegionEnum.EXTERNAL_USER.id));
        return resultList;
    }
}

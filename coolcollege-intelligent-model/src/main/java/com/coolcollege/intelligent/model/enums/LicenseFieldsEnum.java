package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/21 16:38
 */
public enum LicenseFieldsEnum {
    NAME("名称","name"),
    LICENSE_TYPE("证照类型","licenseType"),
    LICENSE_STATUS("证照状态","licenseStatus"),
    LICENSE_IMG("证照图片","licenseImg")
    ;

    private String name;

    private String fieldName;

    private static final Map<String, LicenseFieldsEnum> map = Arrays.stream(values()).collect(Collectors.toMap(LicenseFieldsEnum::getName, Function.identity()));


    LicenseFieldsEnum(String name, String fieldName) {
        this.name = name;
        this.fieldName = fieldName;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static LicenseFieldsEnum getEnum(String name){
        return map.get(name);
    }

    public static List<String> nameList(){
        return Arrays.stream(values()).map(data -> data.getName()).collect(Collectors.toList());
    }
}

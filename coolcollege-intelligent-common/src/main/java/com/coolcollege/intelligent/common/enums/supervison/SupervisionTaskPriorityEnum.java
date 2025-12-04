package com.coolcollege.intelligent.common.enums.supervison;

import com.coolcollege.intelligent.common.enums.importexcel.ImportTemplateEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2023/2/6 14:07
 * @Version 1.0
 */
public enum SupervisionTaskPriorityEnum {

    COMMON("common","中",""),
    URGENT("urgent","高优先",""),
    LOW("low","低",""),
    ;

    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 返回信息
     */
    private String dec;

    private static final Map<String, String> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(SupervisionTaskPriorityEnum::getCode, SupervisionTaskPriorityEnum::getName));

    private static final Map<String, SupervisionTaskPriorityEnum> priorityEnumMap = Arrays.stream(values()).collect(
            Collectors.toMap(SupervisionTaskPriorityEnum::getCode, Function.identity()));


    SupervisionTaskPriorityEnum( String code,String name, String dec) {
        this.name = name;
        this.code = code;
        this.dec = dec;
    }

    public static final List<SupervisionTaskPriorityEnum> getSupervisionTaskPriorityEnumList(List<String> codeList){
        ArrayList<SupervisionTaskPriorityEnum> supervisionTaskPriorityEnums = new ArrayList<>();
        if (CollectionUtils.isEmpty(codeList)){
            return supervisionTaskPriorityEnums;
        }
        for (String code:codeList) {
            SupervisionTaskPriorityEnum supervisionTaskPriorityEnum = priorityEnumMap.get(code);
            if (supervisionTaskPriorityEnum!=null){
                supervisionTaskPriorityEnums.add(supervisionTaskPriorityEnum);
            }

        }
        return supervisionTaskPriorityEnums;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public static String getByCode(String code) {
        return MAP.get(code);
    }
}
